package com.badlogic.audio.samples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.io.AudioDevice;
import com.badlogic.audio.io.MP3Decoder;
import com.badlogic.audio.visualization.Plot;

public class ThresholdMultiBand 
{
	public static final String FILE = "samples/mozart.mp3";
	
	public static void main( String[] argv ) throws FileNotFoundException, Exception
	{
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( FILE ) );
		float samples[] = new float[1024];
		float lastSpectrum[] = new float[513];
		int[] bands = { 0, 4000, 8000, 16000 };
		ArrayList[] spectralFlux = new ArrayList[bands.length-1];
		float[][] thresholds = new float[bands.length-1][];
		for( int i = 0; i < spectralFlux.length; i++ )
			spectralFlux[i] = new ArrayList<Float>( );
		FFT fft = new FFT( 1024, 44100 );
		fft.window( FFT.HAMMING );
		
		while( decoder.readSamples( samples ) > 0 )
		{
			fft.forward( samples );
			
			for( int band = 1; band < bands.length; band++ )
			{
				float sum = 0;
				int startFrequency = fft.freqToIndex( bands[band-1] );
				int endFrequency = fft.freqToIndex( bands[band] );
				
				for( int i = startFrequency; i < endFrequency; i++ )
				{
					float value = (fft.getSpectrum()[i] - lastSpectrum[i] );
					sum += value < 0?0:value;
				}
				
				spectralFlux[band-1].add( (Float)sum );
			}
			System.arraycopy( fft.getSpectrum(), 0, lastSpectrum, 0, fft.getSpectrum().length );
		}
		
		for( int i = 0; i < bands.length-1; i++ )
		{
			thresholds[i] = new float[spectralFlux[i].size()];
			
			for( int j = 0; j < spectralFlux[i].size(); j++ )
			{
				int start = Math.max( 0, j - 20 );
				int end = Math.min( spectralFlux[i].size()-1, j + 20 );
				
				float sum = 0;
				for( int k = start; k <= end; k++ )
				{
					sum += (Float)spectralFlux[i].get(k);
				}
				sum /= (end-start);
				sum *= 1.8f;
				thresholds[i][j] = sum;
			}
		}
	
		Plot plot = new Plot( "Spectral Flux", 1024, 512);
		for( int i = 0; i < bands.length-1; i++ )
		{
			float[] spectralFluxArray = new float[spectralFlux[i].size()];
			for( int j = 0; j < spectralFlux[i].size(); j++ )
				spectralFluxArray[j] = (Float)spectralFlux[i].get(j);
	
			plot.plot( spectralFluxArray, 1, -0.6f + i, false, Color.red );
			plot.plot( thresholds[i], 1, -0.6f + i, true, Color.green );
		}
		
		playBack( plot );
	}
	
	public static void playBack( Plot plot ) throws Exception
	{
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( FILE ) );
		AudioDevice device = new AudioDevice( );
		float samples[] = new float[1024];
		
		long startTime = 0;
		while( decoder.readSamples( samples ) > 0 )
		{
			device.writeSamples( samples );
			if( startTime == 0 )
				startTime = System.nanoTime();
			float elapsedTime = (System.nanoTime()-startTime)/1000000000.0f;
			int position = (int)(elapsedTime * (44100/1024)); 
			plot.setMarker( position, Color.white );			
			Thread.sleep(15); // this is needed or else swing has no chance repainting the plot!
		}
	}
}
