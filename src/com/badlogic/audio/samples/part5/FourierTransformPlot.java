package com.badlogic.audio.samples.part5;

import java.awt.Color;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.visualization.Plot;

/**
 * Simple example that generates a 1024 samples sine wave at 440Hz
 * and plots the resulting spectrum. 
 * 
 * @author mzechner
 *
 */
public class FourierTransformPlot 
{
	public static void main( String[] argv )
	{
		final float frequency = 440; // Note A		
		float increment = (float)(2*Math.PI) * frequency / 44100;		
		float angle = 0;		
		float samples[] = new float[1024];
		FFT fft = new FFT( 1024, 44100 );
		
		for( int i = 0; i < samples.length; i++ )
		{
			samples[i] = (float)Math.sin( angle );
			angle += increment;		
		}
		
		fft.forward( samples );
		
		Plot plot = new Plot( "Note A Spectrum", 512, 512);
		plot.plot(fft.getSpectrum(), 1, Color.red );
	}
}
