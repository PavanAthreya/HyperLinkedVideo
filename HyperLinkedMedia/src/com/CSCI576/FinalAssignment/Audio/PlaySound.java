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


import java.io.*;
import javax.sound.sampled.*;

//Class to play the audio wav file
public class PlaySound implements LineListener {

	private boolean playCompleted;

	public boolean isStopped;
	public boolean isPaused;

	private Clip audioClip;

	private AudioFormat format = null;

    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb

    //Initiating a new audio input stream
    public void load(String filePath){
    	format = null;
    	try{
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
			format = audioStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class,format);
			audioClip = null;
			audioClip = (Clip)AudioSystem.getLine((info));
			audioClip.addLineListener(this);
			audioClip.open(audioStream);
		}catch (Exception ec) {
            ec.printStackTrace();
		}
	}

	//Method to get the current duration of the audio which is being played
	public long getClipSecondLength(){
    	return audioClip.getMicrosecondLength()/1000000;
	}

	//Method to play the sound stream
	public void Play(){
    	audioClip.start();
    	playCompleted = false;
    	isStopped = false;
    	while (!playCompleted){
    		try{
    			Thread.sleep(1000);
			}catch (InterruptedException ex){
    			if (isStopped){
    				audioClip.stop();;
				}
				if (isPaused){
					audioClip.stop();
				}else{
					audioClip.start();
				}
			}
		}
		audioClip.close();
	}

	//Stopping the audio being played
	public void Stop(){
		isStopped=true;
	}

	//Pause the audio being played
	public void Pause(){
		isPaused = true;
	}

	//Resume the audio which was playing earlier
	public void Resume(){
		isPaused = false;
	}

	@Override
	public void update(LineEvent event) {
		LineEvent.Type type = event.getType();
		if (type == LineEvent.Type.STOP){
			if (isStopped || !isPaused){
				playCompleted = true;
			}
		}
	}

	//Method which returns the current frame position in the audio stream
	public long getPosition(){
    	return audioClip.getLongFramePosition();
	}

	//Sets the frame position of the audio stream
	public void SetFramePosition(int framePosition){
        audioClip.setFramePosition(framePosition);
    }

    //Method which returns the sampling rate of the audio stream - 128Kbps 44100Hz
	public float getSamplingRate(){
    	return format.getFrameRate();
	}

	//Method to advance or set time of the frame which is to be played
	public void SeekAudioTime(long time){
        audioClip.setMicrosecondPosition(time);
    }
}
