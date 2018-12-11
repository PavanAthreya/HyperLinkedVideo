/*
Name: Pavan Athreya Narasimha Murthy
USD ID: 9129210968
Email: pavan.athreya@usc.edu
Course: CSCI 576
Instructor: Prof. Parag Havaldar
Semester: Fall 2018
Project: Final Project - Hyper Linked Video
*/

package com.CSCI576.FinalAssignment.Forms;

import com.CSCI576.FinalAssignment.Audio.PlaySound;
import com.CSCI576.FinalAssignment.ConfigFile.ConfigFile;
import com.CSCI576.FinalAssignment.ConfigFile.VideoLink;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;

public class VideoPlayerForm extends JFrame{
    private JLabel lblProjectName;
    private JLabel lblProfName;
    private JLabel lblMyName;
    private JButton btnPlayPause;
    private JButton btnStop;
    private JPanel panelVideoPlayer;
    private JPanel panelVideo;
    private JLabel labelVideoDisplay;

    private ImageIcon imageIcon;
    private String audioFileNameOriginal;
    private String audioFileNameSecondary;

    private ConfigFile videoConfig;

    private boolean isPlay;
    private boolean keepPlaying = true;

    private DefaultListModel<File> originalVideoImages;
    private DefaultListModel<File> secondaryVideoImages;
    private DefaultListModel<File> displayList;

    private BufferedImage currentDisplayedImage;

    private int currentIndex = 0;

    PlaySound playSound;

    private Thread videoThread;
    private Thread audioThread;

    private int totalFrameLength = 9000;

    private boolean threadsStarted = false;
    private boolean didStop = false;
    private boolean isVideoLooping = false;

    public static boolean ShouldChangeVideo = false;
    public static VideoLink hyperlink = null;
    private boolean didChangeVideo = false;

    public VideoPlayerForm(){
        add(panelVideoPlayer);
        setSize(960,720);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        isPlay = false;
        btnStop.setVisible(false);
        labelVideoDisplay.setSize(panelVideo.getWidth(),panelVideo.getHeight());
        PlayPause();
        Stop();
        InitVideo(false);
    }

    private void InitVideo(boolean skipConiigFile){
        if (!skipConiigFile) {
            OpenConfigFile();
        }

        originalVideoImages = new DefaultListModel<File>();
        secondaryVideoImages = new DefaultListModel<File>();
        LoadImagesFor(originalVideoImages,videoConfig.OriginalVideoPath,true);
        LoadImagesFor(secondaryVideoImages,videoConfig.SecondaryVideoPath,false);

        //Display the first image on respective frames
        DisplayImageOnOriginalFrame(currentIndex,originalVideoImages);

        InitAudioAndList(true);

        SetupAV();
    }

    private void InitAudioAndList(boolean forOriginal){
        playSound = new PlaySound();
        if (forOriginal) {
            playSound.load(audioFileNameOriginal);
            displayList = originalVideoImages;
        }else{
            playSound.load(audioFileNameSecondary);
            displayList = secondaryVideoImages;
        }
    }

    private void LoadImagesFor(DefaultListModel<File> list, String folderPath, boolean isPrimary){
        File selectedFolder = new File(folderPath);
        File[] files = selectedFolder.listFiles();
        Collections.sort(Arrays.asList(files));

        //Get audio file path
        if (isPrimary) {
            audioFileNameOriginal = files[2].getPath();
        }else{
            audioFileNameSecondary = files[2].getPath();
        }

        for (int i = 3; i < files.length;i++){
            File imageFile = files[i];
            list.addElement(imageFile);
        }
        System.out.println("Total Images Added: " + list.getSize());
    }

    private void OpenConfigFile(){
        String userDirectory = System.getProperty("user.home");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setCurrentDirectory(new java.io.File(userDirectory+"/Downloads"));
        int returnValue = fileChooser.showSaveDialog(panelVideoPlayer);
        if (returnValue == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();
            videoConfig = new ConfigFile(null,null);
            videoConfig.readFromfile(selectedFile.getPath());
        }
    }

    private void PlayPause(){
        btnPlayPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!threadsStarted) {
                    audioThread.start();
                    videoThread.start();
                    threadsStarted = true;
                }
                PlayPauseVideo();
            }
        });
    }

    private void PlayPauseVideo(){
        if (isPlay) {
            btnPlayPause.setText("Play");
            isPlay = false;
            playSound.Pause();
        }else{
            btnPlayPause.setText("Pause");
            isPlay = true;
            if (playSound.isPaused) {
                playSound.Resume();
                if (isVideoLooping){
                    isVideoLooping = false;
                    audioThread = null;
                    videoThread = null;
                    audioThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            playSound.Play();
                        }
                    });
                    audioThread.start();

                    videoThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            VideoLoop();
                        }
                    });
                    videoThread.start();
                }
            }
        }
        if (btnStop.isVisible()) {
            videoThread.interrupt();
            audioThread.interrupt();
        }
        btnStop.setVisible(true);
    }

    private void InitAudioThread(){
        audioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                playSound.Play();
            }
        });
    }

    private void InitVideoThread(){
        videoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                VideoLoop();
            }
        });
    }

    //Method which periodically changes the frames of the label
    private void VideoLoop(){
        double spf = playSound.getSamplingRate()/30.0;
        int j = 0;

        while (j < Math.round(playSound.getPosition()/spf)){
            currentIndex = currentIndex + 1;
            DisplayImageOnOriginalFrame(currentIndex,displayList);
            j++;
        }

        while (j > Math.round((playSound.getPosition()/spf))){
            //Catch up
        }

        for (int i = j; i < totalFrameLength; i++) {
            while (i > Math.round(playSound.getPosition()/spf)){
                //catch up

                //Check if link was clicked
                if (ShouldChangeVideo){
                    ShouldChangeVideo = false;
                    if (null != hyperlink) {
                        playSound.SeekAudioTime((long)hyperlink.toTime*131072*30);
                        ChangeVideo();
                    }
                }

                if (didStop){
                    break;
                }
            }
            if (didStop){
                DisplayImageOnOriginalFrame(0,displayList);
                didStop = false;
                isVideoLooping = true;
                break;
            }

            while(i<Math.round(playSound.getPosition()/spf)){
                currentIndex = currentIndex + 1;
                DisplayImageOnOriginalFrame(currentIndex,displayList);
                i++;
            }

            currentIndex = currentIndex + 1;
            DisplayImageOnOriginalFrame(currentIndex,displayList);
        }
    }

    private void SetupAV(){
        // plays the sound
        InitAudioThread();

        //plays the video
        InitVideoThread();
    }

    private void ChangeVideo(){
        System.out.println("Clicked on Lnik: " + hyperlink.LinkName);
        System.out.println("Jumping to: " + hyperlink.toTime + " seconds of the second video");

        //Stop the current video
        playSound.Stop();
        videoThread.interrupt();
        audioThread.interrupt();
        videoThread = null;
        audioThread = null;
        playSound = null;
        labelVideoDisplay.remove(hyperlink.panelHyperLink);

        System.gc();

        InitAudioAndList(false);
        playSound.SeekAudioTime((long)hyperlink.toTime*131072*30);
        //set up parameters for next video...
        //Audio Seek
        //playSound.SeekAudioTime((long)hyperlink.toTime*131072*30);
        playSound.SetFramePosition(hyperlink.toTime);

        //Video Frame
        currentIndex = hyperlink.toTime * 30; //30fps

        //Create new threads which handle the video change
        audioThread = null;
        videoThread = null;
        audioThread = new Thread(new Runnable() {
            @Override
            public void run() {
                playSound.Play();
            }
        });
        audioThread.start();

        videoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                VideoLoop();
            }
        });
        videoThread.start();

        btnPlayPause.setText("Pause");
        isPlay = true;
        btnStop.setVisible(true);
        didChangeVideo = true;
    }

    private void Stop(){
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(panelVideoPlayer,"Stop");
                StopVideo();
            }
        });
    }

    private void StopVideo(){
        btnPlayPause.setText("Play");
        isPlay = false;
        btnStop.setVisible(false);
        playSound.Pause();
        videoThread.interrupt();
        audioThread.interrupt();
        currentIndex = 0;
        playSound.SeekAudioTime((long)0.0);
        didStop = true;
    }

    private BufferedImage GetImageFromFile(File imageFile){
        if (imageFile.getAbsolutePath().endsWith(".rgb")){
            try {
                InputStream fileStream = new FileInputStream(imageFile);
                long len = imageFile.length();
                byte[] bytes = new byte[(int)len];
                int offset = 0;
                int numberRead;
                int index = 0;
                while (offset < bytes.length && (numberRead=fileStream.read(bytes,offset,bytes.length-offset)) >= 0){
                    offset = offset + numberRead;
                }
                //Create new buffered image object
                BufferedImage newImage = new BufferedImage(AuthoringToolForm.imageWidth,AuthoringToolForm.imageHeight,BufferedImage.TYPE_INT_RGB);
                //Read the image pixels
                for (int y = 0; y < AuthoringToolForm.imageHeight; y++) {
                    for (int x = 0; x < AuthoringToolForm.imageWidth; x++) {
                        byte r = bytes[index];
                        byte g = bytes[index+(AuthoringToolForm.imageWidth*AuthoringToolForm.imageHeight)];
                        byte b = bytes[index+(2*(AuthoringToolForm.imageHeight*AuthoringToolForm.imageWidth))];
                        int pixel = 0XFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF) << 0;
                        newImage.setRGB(x,y,pixel);
                        index = index  +1;
                    }
                }
                fileStream.close();
                return newImage;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private void DisplayImageOnOriginalFrame(int imageIndex, DefaultListModel<File> list){
        if (imageIndex < list.getSize()) {
            //Get the new image
            BufferedImage newImage = GetImageFromFile(list.getElementAt(imageIndex));
            imageIcon = new ImageIcon(newImage);
            labelVideoDisplay.setIcon(imageIcon);
        }else{
            keepPlaying = false;
            isPlay = true;
            StopVideo();
        }

        if (!didChangeVideo) {
            if (videoConfig.ShouldAddFrame(imageIndex)) {
                videoConfig.Addframe(labelVideoDisplay, imageIndex);
            } else {
                videoConfig.RemoveFrame(labelVideoDisplay);
            }
            labelVideoDisplay.repaint();
        }
    }

}

