package com.badlogic.audio.samples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.audio.io.AudioDevice;
import com.badlogic.audio.io.MP3Decoder;
import com.badlogic.audio.io.WaveDecoder;
import com.badlogic.audio.visualization.Plot;

public class RealTimePlot 
{
	public static void main( String[] argv ) throws FileNotFoundException, Exception
	{
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( "samples/cochise.mp3" ) );
		ArrayList<Float> allSamples = new ArrayList<Float>( );
		float[] samples = new float[1024];

		while( decoder.readSamples( samples ) > 0 )
		{
			for( int i = 0; i < samples.length; i++ )
				allSamples.add( samples[i] );
		}

		samples = new float[allSamples.size()];
		for( int i = 0; i < samples.length; i++ )
			samples[i] = allSamples.get(i);

		Plot plot = new Plot( "Wave Plot", 512, 512 );
		plot.plot( samples, 44100 / 100, Color.red );
		allSamples = null; // let'S preserve some memory...
		
		decoder = new MP3Decoder( new FileInputStream( "samples/cochise.mp3" ) );
		AudioDevice device = new AudioDevice( );
		samples = new float[1024];
		
		int windowCount = 0;
		while( decoder.readSamples( samples ) > 0 )
		{
			device.writeSamples( samples );
			int x = (int)(( 1024.0f / (44100 / 100) ) * windowCount); 
			plot.drawLine( x, 0, x, 512, Color.white );
			windowCount++;
		}
	}
}
