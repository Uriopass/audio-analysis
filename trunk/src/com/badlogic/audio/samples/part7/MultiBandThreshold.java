package com.badlogic.audio.samples.part7;

import java.awt.Color;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.audio.analysis.SpectrumProvider;
import com.badlogic.audio.analysis.ThresholdFunction;
import com.badlogic.audio.io.MP3Decoder;
import com.badlogic.audio.visualization.PlaybackVisualizer;
import com.badlogic.audio.visualization.Plot;

public class MultiBandThreshold 
{
	public static final String FILE = "samples/jazz.mp3";
	public static final int HOP_SIZE = 512;
	public static final int HISTORY_SIZE = 50;
	public static final float[] multipliers = { 2f, 2f, 2f };
	public static final float[] bands = { 80, 4000, 4000, 10000, 10000, 16000 };
	
	public static void main( String[] argv ) throws Exception
	{		
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( FILE  ) );
		SpectrumProvider spectrumProvider = new SpectrumProvider( decoder, 1024, HOP_SIZE, true );			
		float[] spectrum = spectrumProvider.nextSpectrum();
		float[] lastSpectrum = new float[spectrum.length];		
		List<List<Float>> spectralFlux = new ArrayList<List<Float>>( );
		for( int i = 0; i < bands.length / 2; i++ )
			spectralFlux.add( new ArrayList<Float>( ) );
				
		do
		{						
			for( int i = 0; i < bands.length; i+=2 )
			{				
				int startFreq = spectrumProvider.getFFT().freqToIndex( bands[i] );
				int endFreq = spectrumProvider.getFFT().freqToIndex( bands[i+1] );
				float flux = 0;
				for( int j = startFreq; j <= endFreq; j++ )
				{
					float value = (spectrum[j] - lastSpectrum[j]);
					value = (value + Math.abs(value))/2;
					flux += value;
				}
				spectralFlux.get(i/2).add( flux );
			}
					
			System.arraycopy( spectrum, 0, lastSpectrum, 0, spectrum.length );
		}
		while( (spectrum = spectrumProvider.nextSpectrum() ) != null );				
		
		List<List<Float>> thresholds = new ArrayList<List<Float>>( );
		for( int i = 0; i < bands.length / 2; i++ )
		{
			List<Float> threshold = new ThresholdFunction( HISTORY_SIZE, multipliers[i] ).calculate( spectralFlux.get(i) );
			thresholds.add( threshold );
		}
		
		Plot plot = new Plot( "Spectral Flux", 1024, 512 );
		for( int i = 0; i < bands.length / 2; i++ )
		{
			plot.plot( spectralFlux.get(i), 1, -0.6f * (bands.length / 2 - 2) + i, false, Color.red );
			plot.plot( thresholds.get(i), 1, -0.6f * (bands.length / 2 - 2) + i, true, Color.green );
		}
		
		new PlaybackVisualizer( plot, HOP_SIZE, new MP3Decoder( new FileInputStream( FILE ) ) );
	}
}
