/*
Name: Pavan Athreya Narasimha Murthy
USD ID: 9129210968
Email: pavan.athreya@usc.edu
Course: CSCI 576
Instructor: Prof. Parag Havaldar
Semester: Fall 2018
Project: Final Project - Hyper Linked Video
*/

package com.CSCI576.FinalAssignment.ConfigFile;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

//Class which hold the configuration/metadata of the hyperlinks
public class ConfigFile {
    public String OriginalVideoPath;
    public String SecondaryVideoPath;

    public DefaultListModel<VideoLink> links;

    //Instantiator
    public ConfigFile(String firstVideo, String secondVideo){
        this.OriginalVideoPath = firstVideo;
        this.SecondaryVideoPath = secondVideo;
        links = new DefaultListModel<VideoLink>();
    }

    //Returns true if the videos are connected
    public boolean AreVideosConnected() {
        if (0 == links.getSize()) {
            return false;
        }
        for (int i = 0; i < links.getSize(); i++) {
            VideoLink link = links.getElementAt(i);
            if (link.isConnected == false){
                return false;
            }
        }
        return true;
    }

    //Writes the configuration to the txt file name specified
    public void writeToFile(String filePath){
        //Write this to a txt file
        String fileName = System.getProperty("user.home") + "/Downloads/" + filePath +".txt";
        System.out.println("Writing Config to a File:");
        try(PrintWriter writer = new PrintWriter(fileName)){
            writer.println("OriginalVideoLink:" + OriginalVideoPath);
            writer.println("SecondaryVideoLink:" + SecondaryVideoPath);
            System.out.println("OriginalVideoLink:" + OriginalVideoPath);
            System.out.println("SecondaryVideoLink:" + SecondaryVideoPath);
            for (int i = 0; i < links.getSize(); i++) {
                VideoLink link = links.getElementAt(i);
                writer.println(link.getConfigText());
                System.out.println(link.getConfigText());
            }
        }catch (IOException ex){

        }
    }

    //reads the selected config file and sets up the data structure
    public void readFromfile(String configFilePath){
        try(BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                ParseConfigFile(line);
                line = br.readLine();
            }
            String everything = sb.toString();
            System.out.println(everything);
            System.out.println("Parsing of config File complete...\nLoading the video now...");
        }catch (IOException ex){

        }
    }

    //Method to parse the config file
    private void ParseConfigFile(String line){
        if (line.contains("OriginalVideoLink:")){
            OriginalVideoPath = line.replace("OriginalVideoLink:","");
        }else if (line.contains("SecondaryVideoLink:")){
            SecondaryVideoPath = line.replace("SecondaryVideoLink:","");
        }else{
            String[] stringList = line.split(";");
            VideoLink link = new VideoLink();
            link.LinkName = stringList[0].replace("LinkName:","");
            link.atTime = Integer.parseInt(stringList[1].replace("atTime:",""));
            link.toTime = Integer.parseInt(stringList[2].replace("totime:",""));
            String[] panelDimensions = stringList[3].replace("PanelDimensions:","").split(",");
            int x = Integer.parseInt(panelDimensions[0]);
            int y = Integer.parseInt(panelDimensions[1]);
            int width = Integer.parseInt(panelDimensions[2]);
            int height = Integer.parseInt(panelDimensions[3]);
            link.panelHyperLink = new JPanel();
            link.panelHyperLink.setLocation(x,y);
            link.panelHyperLink.setSize(width,height);
            link.panelHyperLink.setOpaque(false);
            Color randomColor = new Color((int)(Math.random() * 0x1000000));
            Border borderLine = BorderFactory.createLineBorder(randomColor.brighter());
            link.panelHyperLink.setBorder(borderLine);
            link.isConnected = true;
            link.SetupMouseClickedListerner();
            links.addElement(link);
        }
    }

    //Returns true if the frame need to be added in the video that is being played
    public boolean ShouldAddFrame(int currentFrameNumber){
        for (int i = 0; i < links.getSize(); i++) {
            VideoLink link = links.getElementAt(i);
            int minFrameNumber = (link.atTime-2) * 30;
            int maxFrameNumber = (link.atTime+2) * 30;
            if ((minFrameNumber <= currentFrameNumber) && (currentFrameNumber <= maxFrameNumber)){
                return true;
            }
        }
        return false;
    }

    //Adds the frame Jpanel to the label which is rendering the video
    public void Addframe(JLabel videoLabel, int currentFrameNumber){
        for (int i = 0; i < links.getSize(); i++) {
            if (!DoesLabelHaveComponent(videoLabel, i)) {
                VideoLink link = links.getElementAt(i);
                int minFrameNumber = (link.atTime-2) * 30;
                int maxFrameNumber = (link.atTime+2) * 30;
                if ((minFrameNumber <= currentFrameNumber) && (currentFrameNumber <= maxFrameNumber)){
                    videoLabel.add(link.panelHyperLink);
                    link.isComponentAdded = true;
                    link.panelHyperLink.setVisible(true);
                }
            }
        }
    }

    //Checks if the Jpanel is a subview of the label
    private boolean DoesLabelHaveComponent(JLabel videoLabel, int index){
        return links.getElementAt(index).isComponentAdded;
    }

    //Removes the JPanel from the label
    public void RemoveFrame(JLabel videoLabel){
        for (int i = 0; i < links.getSize(); i++) {
            if (DoesLabelHaveComponent(videoLabel,i)){
                VideoLink link = links.getElementAt(i);
                videoLabel.remove(link.panelHyperLink);
                link.isComponentAdded = false;
            }
        }
    }
}
