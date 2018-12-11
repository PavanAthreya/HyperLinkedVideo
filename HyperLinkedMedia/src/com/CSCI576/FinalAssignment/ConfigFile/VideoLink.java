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

import com.CSCI576.FinalAssignment.Forms.VideoPlayerForm;
import com.CSCI576.FinalAssignment.Rectangle.Resizable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//Class that has the link information of the hyperlinks
public class VideoLink {
    public String LinkName;

    public int atTime;
    public int toTime;

    public boolean isConnected;

    public boolean isComponentAdded = false;

    public JPanel panelHyperLink;

    //Constructor - used by authoring tool
    public VideoLink(String linkName,int atTime, int toTime, JPanel panelHyperLink) {
        this.LinkName = linkName;
        this.atTime = atTime;
        this.toTime = toTime;
        this.isConnected = false;
        this.panelHyperLink = panelHyperLink;
    }

    //Empty Constructor - used by player
    public VideoLink(){

    }

    //Click listener for when the user taps on the hyperlink to change the content
    public void SetupMouseClickedListerner(){
        this.panelHyperLink.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                //Do Nothing
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Pressed on link: " + LinkName);
                VideoPlayerForm.ShouldChangeVideo = true;
                VideoPlayerForm.hyperlink = new VideoLink(LinkName,atTime,toTime,panelHyperLink);
                VideoPlayerForm.hyperlink.isConnected = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //Do Nothing
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //Do Nothing
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //Do Nothing
            }
        });
    }

    //Over riding toString for the list adapter
    @Override
    public String toString(){
        return this.LinkName;
    }

    //Method to construct a string from the properties for logging them on the config file
    public String getConfigText(){
        Rectangle rect = new Rectangle();
        for (Component comp:panelHyperLink.getComponents()) {
            if (comp.getClass() == Resizable.class){
                 rect = comp.getBounds();
            }
        }
        String rectFormat = rect.x + "," + rect.y + "," + rect.width + "," + rect.height;
        StringBuilder log = new StringBuilder();
        log.append("LinkName:");
        log.append(this.LinkName);
        log.append(";");
        log.append("atTime:");
        log.append(this.atTime);
        log.append(";");
        log.append("totime:");
        log.append(this.toTime);
        log.append(";");
        log.append("PanelDimensions:");
        log.append(rectFormat);
        log.append(";");
        return log.toString();
    }
}
