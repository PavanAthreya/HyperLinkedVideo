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

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainForm extends JFrame{
    private JLabel lblProjectName;
    private JLabel lblProfessorName;
    private JLabel lblMyName;
    private JButton btnVideoPlayer;
    private JButton btnAuthoringTool;
    private JPanel panelMain;

    private VideoPlayerForm videoPlayerForm;
    private AuthoringToolForm authoringToolForm;

    public MainForm(){
        //Adding the panel so that it can be visible to the user
        //if this is not set, the UI is not shown to the user
        add(panelMain);

        //Setting the default width anf height of the window
        setSize(640,480);

        btnAuthoringTool.setSelected(false);
        btnVideoPlayer.setSelected(false);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (JOptionPane.showConfirmDialog(panelMain,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    super.windowClosing(e);
                    System.exit(0);
                }

            }
        });

        //Clicked on Video Player
        btnVideoPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Load Video Player Form
                if (null == videoPlayerForm) {
                    videoPlayerForm = new VideoPlayerForm();
                    videoPlayerForm.setVisible(true);
                    videoPlayerForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    videoPlayerForm.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            if (JOptionPane.showConfirmDialog(panelMain,
                                    "Are you sure you want to close the Video Player?", "Close Window?",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                                super.windowClosing(e);
                                videoPlayerForm = null;
                            }
                        }
                    });
                }else{
                    JOptionPane.showMessageDialog(panelMain,"Video Player already running");
                }
            }
        });

        //Clicked on Authoring Tool
        btnAuthoringTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Load Authoring Tool Form
                if (null == authoringToolForm) {
                    authoringToolForm = new AuthoringToolForm();
                    authoringToolForm.setVisible(true);
                    authoringToolForm.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    authoringToolForm.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            if (JOptionPane.showConfirmDialog(panelMain,
                                    "Are you sure you want to close the Authoring Tool?", "Close Window?",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                                super.windowClosing(e);
                                authoringToolForm = null;
                            }
                        }
                    });
                }else{
                    JOptionPane.showMessageDialog(panelMain,"Authoring Tool already running");
                }
            }
        });
    }

}
