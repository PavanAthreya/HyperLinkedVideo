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

import com.CSCI576.FinalAssignment.ConfigFile.ConfigFile;
import com.CSCI576.FinalAssignment.ConfigFile.VideoLink;
import com.CSCI576.FinalAssignment.Rectangle.Resizable;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

//Form which handles the UI and Functionality of the Authoring Tool
public class AuthoringToolForm extends JFrame{
    private JPanel panelAuthoringTool;
    private JLabel lblProjectName;
    private JLabel lblInstructorName;
    private JLabel lblMyName;
    private JSplitPane splitPaneBottom;
    private JSlider sliderSecondary;
    private JSlider sliderOriginal;
    private JList listCreatedLinks;
    private JButton btnImportOriginalVideo;
    private JButton btnImportSecondaryVideo;
    private JButton btnCreateNewHyperLink;
    private JButton btnConnectVideo;
    private JButton btnSave;
    private JPanel panelTop;
    private JSplitPane splitPaneTop;
    private JSplitPane splitPaneTopRight;
    private JPanel panelList;
    private JPanel panel2Buttons;
    private JPanel panel3Buttons;
    private JPanel panelLeftVideoHolder;
    private JPanel panelRightVideoHolder;

    private JPanel panelLeftVideo;
    private JPanel panelRightVideo;
    private JLabel leftVideoDisplayLabel;
    private JLabel rightVideoDisplayLabel;

    private JButton btnEditLink;
    private JButton btnDeleteLink;
    private JPanel panelEditOptions;
    private JLabel lblLeftFrameNumber;
    private JLabel lblRightFrameNumber;

    //List of links created
    private ConfigFile videoConfig;
    private int SelectedListIndex;

    //Frame dimensions of the image/video
    static int imageWidth = 352;
    static int imageHeight = 288;

    //List to hold the images to be shown
    private DefaultListModel<BufferedImage> leftImageList;
    private DefaultListModel<BufferedImage> rightImageList;

    //ImageIcons used to display the video
    private ImageIcon leftIcon;
    private ImageIcon rightIcon;

    private boolean isOriginalVideoImported = false;
    private boolean isSecondaryVideoImported = false;

    //Video Folder Path
    private String originalVideoPath;
    private String secondaryVideoPath;


    public AuthoringToolForm(){

        setSize(717,610);

        SelectedListIndex = -1;

        panelTop.setSize(panelTop.getWidth(),200);

        //panel3Buttons.setSize(400,300);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        leftImageList = new DefaultListModel<BufferedImage>();
        rightImageList = new DefaultListModel<BufferedImage>();

        SetUpSlider();

        ShowLinkOptions();

//        splitPaneBottom.setDividerLocation(splitPaneBottom.getWidth()/2);
//        splitPaneBottom.resetToPreferredSizes();

        //Add Listeners for the buttons and sliders
        ImportOriginalVideo();
        ImportSecondaryVideo();
        CreateHyperLink();
        SelectedItemInJList();
        DeleteLink();
        EditLinkName();
        ConnectVideo();
        SaveVideoConfig();
        SliderValueChanged();
        AddDisplayLabelsToPanels();

        add(panelAuthoringTool);
    }


    private void SetUpSlider(){
        //300 seconds for a 5 minute video
        //While scrolling through the videos the mid of the 30fps frame will be shown instead of scrolling through 9000 images
        sliderOriginal.setMaximum(299);
        sliderOriginal.setMinimum(0);
        sliderSecondary.setMaximum(299);
        sliderSecondary.setMinimum(0);
        sliderOriginal.setValue(0);
        sliderSecondary.setValue(0);
        sliderOriginal.setEnabled(false);
        sliderSecondary.setEnabled(false);
    }

    private void AddDisplayLabelsToPanels(){
        leftIcon = new ImageIcon(new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB));
        rightIcon = new ImageIcon(new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB));
        leftVideoDisplayLabel.setSize(panelLeftVideo.getWidth(),panelLeftVideo.getHeight());
        rightVideoDisplayLabel.setSize(panelRightVideo.getWidth(),panelRightVideo.getHeight());
        leftVideoDisplayLabel.setIcon(leftIcon);
        rightVideoDisplayLabel.setIcon(rightIcon);
    }

    private void ShowLinkOptions(){
        if (null != videoConfig) {
            if (videoConfig.links.getSize() > 0) {
                if (!panelEditOptions.isVisible()) {
                    panelEditOptions.setVisible(true);
                }
            } else {
                panelEditOptions.setVisible(false);
            }
        }else{
            panelEditOptions.setVisible(false);
        }
        panel3Buttons.setSize(350,300);
    }

    private void ImportOriginalVideo(){
        btnImportOriginalVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Import Original Video Clicked");
                OpenFolderFor(leftImageList,true);
                isOriginalVideoImported = true;
                sliderOriginal.setEnabled(true);
            }
        });
    }

    private void ImportSecondaryVideo(){
        btnImportSecondaryVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Import Secondary Video Clicked");
                OpenFolderFor(rightImageList,false);
                isSecondaryVideoImported = true;
                sliderSecondary.setEnabled(true);
            }
        });
    }

    private void CreateHyperLink(){
        btnCreateNewHyperLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Crete new HyperLink Clicked");
                if ((isOriginalVideoImported && isSecondaryVideoImported) && (null != originalVideoPath) && (null != secondaryVideoPath)){

                    if (null ==videoConfig) {
                        videoConfig = new ConfigFile(originalVideoPath, secondaryVideoPath);
                        listCreatedLinks.setModel(videoConfig.links);
                    }

                    String newLinkName = JOptionPane.showInputDialog(panelAuthoringTool,"Enter the new link name");

                    if (null != newLinkName) {
                        System.out.println("New Link Name: " + newLinkName);

                        Resizable res;
                        JPanel newPanel = new JPanel(null);
                        JPanel area = new JPanel();
                        area.setOpaque(false);
                        res = new Resizable(area);
                        res.setBounds(0,0,50,50);
                        res.setOpaque(false);
                        newPanel.add(res);
                        newPanel.setOpaque(false);
                        newPanel.setSize(leftVideoDisplayLabel.getWidth(),leftVideoDisplayLabel.getHeight());

                        VideoLink newLink = new VideoLink(newLinkName,0,0, newPanel);
                        videoConfig.links.addElement(newLink);

                        leftVideoDisplayLabel.add(newLink.panelHyperLink);
                        requestFocus();
                        res.repaint();

                        ShowLinkOptions();
                    }
                }else{
                    JOptionPane.showMessageDialog(panelAuthoringTool,"You should import both the videos first");
                }
            }
        });
    }

    private void SelectedItemInJList(){
        listCreatedLinks.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    SelectedListIndex = listCreatedLinks.getSelectedIndex();
                }
            }
        });
    }

    private void DeleteLink(){
        btnDeleteLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SelectedListIndex >= 0){
                    VideoLink videoLink = videoConfig.links.elementAt(SelectedListIndex);
                    leftVideoDisplayLabel.remove(videoLink.panelHyperLink);
                    videoConfig.links.removeElementAt(SelectedListIndex);
                    SelectedListIndex = -1;
                    requestFocus();
                    leftVideoDisplayLabel.repaint();
                }else{
                    JOptionPane.showMessageDialog(panelAuthoringTool,"Invalid Selection: Cannot delete");
                }
                ShowLinkOptions();
                listCreatedLinks.clearSelection();
            }
        });
    }

    private void EditLinkName(){
        btnEditLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (SelectedListIndex >= 0){
                    String editedLinkName = JOptionPane.showInputDialog(panelAuthoringTool,"Enter the link name to be edited");
                    if (null != editedLinkName) {
                        System.out.println("Edited Link Name: " + editedLinkName);
                        VideoLink editableLink = videoConfig.links.getElementAt(SelectedListIndex);
                        editableLink.LinkName = editedLinkName;
                        videoConfig.links.setElementAt(editableLink,SelectedListIndex);
                        ShowLinkOptions();
                        SelectedListIndex = -1;
                        listCreatedLinks.clearSelection();
                    }
                }else{
                    JOptionPane.showMessageDialog(panelAuthoringTool,"Invalid Selection: Cannot Edit");
                }
            }
        });
    }

    private void SaveVideoConfig(){
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Save Video Clicked");
                if (null != videoConfig) {
                    if (videoConfig.AreVideosConnected()) {
                        String filePath = JOptionPane.showInputDialog(panelAuthoringTool,"Enter the name of the config file you wish to save");
                        videoConfig.writeToFile(filePath);
                    } else {
                        JOptionPane.showMessageDialog(panelAuthoringTool, "Connect all the links before saving the configuration");
                    }
                }else{
                    JOptionPane.showMessageDialog(panelAuthoringTool, "Import and Connect all the links before saving the configuration");
                }
            }
        });
    }

    private void ConnectVideo(){
        btnConnectVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Connect Video Clicked");
                if (SelectedListIndex >= 0) {
                    VideoLink link = videoConfig.links.getElementAt(SelectedListIndex);
                    link.atTime = sliderOriginal.getValue();
                    link.toTime = sliderSecondary.getValue();
                    link.isConnected = true;
                    videoConfig.links.setElementAt(link,SelectedListIndex);
                }else{
                    JOptionPane.showMessageDialog(panelAuthoringTool,"Select a link before connecting");
                }
            }
        });
    }

    private void SliderValueChanged(){
        sliderOriginal.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("Slider Left Value: ");
                System.out.println(sliderOriginal.getValue());
                DisplayImageOnOriginalFrame(sliderOriginal.getValue(),true);
                lblLeftFrameNumber.setText("Video Time: " + sliderOriginal.getValue() + " seconds");
            }
        });

        sliderSecondary.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("Slider Right Value: ");
                System.out.println(sliderSecondary.getValue());
                DisplayImageOnOriginalFrame(sliderSecondary.getValue(),false);
                lblRightFrameNumber.setText("Video Time: " + sliderSecondary.getValue() + " seconds");
            }
        });
    }

    private void OpenFolderFor(DefaultListModel<BufferedImage> imageList, boolean toOriginal){
        String userDirectory = System.getProperty("user.home");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setCurrentDirectory(new java.io.File(userDirectory+"/Downloads/AIFilm"));
        int returnValue = fileChooser.showSaveDialog(panelAuthoringTool);
        if (returnValue == JFileChooser.APPROVE_OPTION){
            File selectedFolder = fileChooser.getSelectedFile();
            //Read the rgb files and store them in a list for slider display
            File[] fileList = selectedFolder.listFiles();
            //Sorting the file names since the list in unsorted
            Collections.sort(Arrays.asList(fileList));
            if (toOriginal){
                System.out.println("Selected Folder Path for Original Image: " + selectedFolder.getAbsolutePath());
                originalVideoPath = selectedFolder.getAbsolutePath();
            }else{
                System.out.println("Selected Folder Path for Secondary Image: " + selectedFolder.getAbsolutePath());
                secondaryVideoPath = selectedFolder.getAbsolutePath();
            }

            for (int i = 0; i < fileList.length;i++){
                if (0 == i % 30) {
                    File imageFile = fileList[i];
                    AddImageToList(imageFile, imageList);
                }
            }
            System.out.println("Total Images Added: " + imageList.getSize());

            //Display the first image on respective frames
            DisplayImageOnOriginalFrame(0,toOriginal);
        }
    }

    private void AddImageToList(File imageFile, DefaultListModel<BufferedImage> imageList){
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
                BufferedImage newImage = new BufferedImage(imageWidth,imageHeight,BufferedImage.TYPE_INT_RGB);
                //Read the image pixels
                for (int y = 0; y < imageHeight; y++) {
                    for (int x = 0; x < imageWidth; x++) {
                        byte r = bytes[index];
                        byte g = bytes[index+(imageWidth*imageHeight)];
                        byte b = bytes[index+(2*(imageHeight*imageWidth))];
                        int pixel = 0XFF000000 | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF) << 0;
                        newImage.setRGB(x,y,pixel);
                        index = index  +1;
                    }
                }
                fileStream.close();
                imageList.addElement(newImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void DisplayImageOnOriginalFrame(int imageIndex, boolean onOriginal){
        if (onOriginal){
            leftIcon = new ImageIcon(leftImageList.getElementAt(imageIndex));
            leftVideoDisplayLabel.setIcon(new ImageIcon(leftImageList.getElementAt(imageIndex)));
            leftVideoDisplayLabel.repaint();
        }else{
            rightIcon = new ImageIcon(rightImageList.getElementAt(imageIndex));
            rightVideoDisplayLabel.setIcon(rightIcon);
            rightVideoDisplayLabel.repaint();
        }
    }
}
