package com.badlogic.audio.samples.part6;

import java.awt.Color;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.audio.analysis.SpectrumProvider;
import com.badlogic.audio.analysis.ThresholdFunction;
import com.badlogic.audio.io.MP3Decoder;
import com.badlogic.audio.visualization.PlaybackVisualizer;
import com.badlogic.audio.visualization.Plot;

/**
 * Demonstrates the calculation of the spectral flux function
 * as well as the threshold function and displays the result.
 * 
 * @author mzechner
 *
 */
public class SpectralFluxThreshold 
{
	public static final String FILE = "samples/dfourth.mp3";
	public static final int HOP_SIZE = 512;
	public static final int HISTORY_SIZE = 20;
	public static final float MULTIPLIER = 1.6f;
	
	public static void main( String[] argv ) throws Exception
	{
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( FILE  ) );
		SpectrumProvider spectrumProvider = new SpectrumProvider( decoder, 1024, HOP_SIZE, true );			
		float[] spectrum = spectrumProvider.nextSpectrum();
		float[] lastSpectrum = new float[spectrum.length];
		ArrayList<Float> spectralFlux = new ArrayList<Float>( );
		
		do
		{
			float flux = 0;
			for( int i = 0; i < spectrum.length; i++ )
			{
				float value = (spectrum[i] - lastSpectrum[i]); 
				flux += value < 0?0:value;
			}
			spectralFlux.add( flux );
			
			System.arraycopy( spectrum, 0, lastSpectrum, 0, spectrum.length );
		}
		while( (spectrum = spectrumProvider.nextSpectrum() ) != null );
		
		List<Float> threshold = new ThresholdFunction( HISTORY_SIZE, MULTIPLIER ).calculate( spectralFlux );
		
		Plot plot = new Plot( "Spectral Flux", 1024, 512 );
		plot.plot( spectralFlux, 1, Color.red );
		plot.plot( threshold, 1, Color.green );
		
		new PlaybackVisualizer( plot, HOP_SIZE, new MP3Decoder( new FileInputStream( FILE ) ) );
	}
}
