package com.badlogic.audio.samples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.io.AudioDevice;
import com.badlogic.audio.io.WaveDecoder;
import com.badlogic.audio.visualization.Plot;

/**
 * A simple example that shows how to use the {@link Plot} class.
 * Note that the plots will not be entirely synchronous to the
 * music playback. This is just an example, you should not do
 * real-time plotting with the Plot class it is just not made for
 * this.
 * 
 * @author mzechner
 *
 */
public class PlotExample 
{
	public static void main( String[] argv ) throws FileNotFoundException, Exception
	{
		Plot plot = new Plot( "Sine Plot", 512, 512 );
		WaveDecoder decoder = new WaveDecoder( new FileInputStream( "samples/sample.wav" ) );
		AudioDevice device = new AudioDevice( );
		float samples[] = new float[1024];
		float lowerSamples[] = new float[1024];
		
		while( decoder.readSamples( samples ) > 0 )
		{
			device.writeSamples( samples );
			plot.clear();
			plot.plotArray( samples, 2, Color.red );
			for( int i = 0; i < samples.length; i++ )
				lowerSamples[i] = samples[i] * 0.5f;
			plot.plotArray( lowerSamples, 2, Color.green );
		}
	}
}
