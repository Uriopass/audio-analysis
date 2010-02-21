package com.badlogic.audio.samples.part6;

import java.awt.Color;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.badlogic.audio.analysis.SpectrumProvider;
import com.badlogic.audio.io.MP3Decoder;
import com.badlogic.audio.visualization.PlaybackVisualizer;
import com.badlogic.audio.visualization.Plot;

/**
 * Calculates the spectral flux of a song and displays the
 * resulting plot.
 * 
 * @author mzechner
 *
 */
public class RectifiedSpectralFlux 
{
	public static final String FILE = "samples/cochise.mp3";
	public static final int HOP_SIZE = 1024;
	
	public static void main( String[] argv ) throws Exception
	{
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( FILE  ) );
		SpectrumProvider spectrumProvider = new SpectrumProvider( decoder, 1024, HOP_SIZE, false );			
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
		
		
		Plot plot = new Plot( "Spectral Flux", 1024, 512 );
		plot.plot( spectralFlux, 1, Color.red );
		
		new PlaybackVisualizer( plot, HOP_SIZE, new MP3Decoder( new FileInputStream( FILE ) ) );
	}
}
