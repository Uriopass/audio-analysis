package com.badlogic.audio.samples.part4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.badlogic.audio.io.AudioDevice;
import com.badlogic.audio.io.MP3Decoder2;

/**
 * Simple example that shows how to decode an mp3 file.
 * 
 * @author mzechner
 *
 */
public class MP3Output 
{
	public static void main( String[] argv ) throws FileNotFoundException, Exception
	{			
		AudioDevice device = new AudioDevice( );
		MP3Decoder2 decoder = new MP3Decoder2( new FileInputStream( "samples/mozart.mp3" ) );
		float[] samples = new float[1024];
		
		while( decoder.readSamples( samples ) > 0 )
		{
			device.writeSamples( samples );
		}
	}
}
