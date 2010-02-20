package com.badlogic.audio.samples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.audio.io.AudioDevice;
import com.badlogic.audio.io.MP3Decoder;
import com.badlogic.audio.visualization.Plot;

/**
 * A simple example on how to do real-time plotting. First all samples
 * from an mp3 file are read in and plotted, 1024 samples per pixel. Next
 * we open a new MP3Decoder and an AudioDevice and play back the music. While
 * doing this we also set a marker in the plot that shows us the current position
 * of the music playback. The marker position is calculated in pixels by
 * measuring the elapsed time between the start of the playback and the 
 * current time. The elapsed time is then multiplied by the frequency divided
 * by the sample window size (1024 samples in this case). This gives us the
 * x-coordinate of the marker in the plot. After writting a sample window
 * to the audio device and setting the marker we sleep for 20ms to give
 * the Swing GUI thread time to repaint the plot with the updated marker
 * position.
 * 
 * @author mzechner
 *
 */
public class RealTimePlot 
{
	private static final int SAMPLE_WINDOW_SIZE = 1024;	
	private static final String FILE = "samples/sample.mp3";
	
	public static void main( String[] argv ) throws FileNotFoundException, Exception
	{
		float[] samples = readInAllSamples( FILE );

		Plot plot = new Plot( "Wave Plot", 1024, 512 );
		plot.plot( samples, SAMPLE_WINDOW_SIZE, Color.red );		
		
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( FILE ) );
		AudioDevice device = new AudioDevice( );
		samples = new float[SAMPLE_WINDOW_SIZE];
		
		long startTime = System.nanoTime();
		while( decoder.readSamples( samples ) > 0 )
		{
			device.writeSamples( samples );
			float elapsedTime = (System.nanoTime()-startTime)/1000000000.0f;
			int position = (int)(elapsedTime * (44100/SAMPLE_WINDOW_SIZE)); 
			plot.setMarker( position, Color.white );			
			Thread.sleep(20); // this is needed or else swing has no chance repainting the plot!
		}
	}
	
	public static float[] readInAllSamples( String file ) throws FileNotFoundException, Exception
	{
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( file ) );
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

		return samples;
	}
}
