package com.badlogic.audio.samples;

import java.awt.Color;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.io.AudioDevice;
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
		final float frequencyA = 440; // Note A
		final float frequencyAOctave = 880; // Note A in the next octave
		float incrementA = (float)(2*Math.PI) * frequencyA / 44100;
		float incrementAOctave = (float)(2*Math.PI)*frequencyAOctave / 44100;
		float angleA = 0;
		float angleAOctave = 0;
		float samples[] = new float[1024];
		FFT fft = new FFT( 1024, 44100 );
		
		for( int i = 0; i < samples.length; i++ )
		{
			samples[i] = ((float)Math.sin( angleA ) + (float)Math.sin( angleAOctave) ) / 2;
			angleA += incrementA;
			angleAOctave += incrementAOctave;
		}
		
		fft.forward( samples );
		
		Plot plot = new Plot( "Note A Spectrum", 512, 512);
		plot.plot(fft.getSpectrum(), 1, Color.red );
	}
}
