/*
Name: Pavan Athreya Narasimha Murthy
USD ID: 9129210968
Email: pavan.athreya@usc.edu
Course: CSCI 576
Instructor: Prof. Parag Havaldar
Semester: Fall 2018
Project: Final Project - Hyper Linked Video
*/

package com.CSCI576.FinalAssignment.Audio;

//Class to handle the player exceptions
public class PlayWaveException extends Exception {

    public PlayWaveException(String message) {
	super(message);
    }

    public PlayWaveException(Throwable cause) {
	super(cause);
    }

    public PlayWaveException(String message, Throwable cause) {
	super(message, cause);
    }

}
