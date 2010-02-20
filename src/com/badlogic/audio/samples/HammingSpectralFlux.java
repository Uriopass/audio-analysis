package com.badlogic.audio.samples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.io.MP3Decoder;
import com.badlogic.audio.visualization.Plot;

/**
 * Example exactly the sames a RectifiedSpectralFlux but with
 * hamming windowing enabled (smooths out the waveform before fft).
 * @author mzechner
 *
 */
public class HammingSpectralFlux 
{
	public static void main( String[] argv ) throws FileNotFoundException, Exception
	{
		MP3Decoder decoder = new MP3Decoder( new FileInputStream( "samples/cochise.mp3" ) );
		float samples[] = new float[1024];
		float lastSpectrum[] = new float[513];
		ArrayList<Float> spectralFlux = new ArrayList<Float>( );
		FFT fft = new FFT( 1024, 44100 );
		fft.window( FFT.HAMMING );
		
		while( decoder.readSamples( samples ) > 0 )
		{
			fft.forward( samples );
			
			float sum = 0;
			for( int i = 0; i < lastSpectrum.length; i++ )
			{
				float value = (fft.getSpectrum()[i] - lastSpectrum[i] );
				sum += value < 0?0:value;
			}
			System.arraycopy( fft.getSpectrum(), 0, lastSpectrum, 0, fft.getSpectrum().length );
			spectralFlux.add( sum );
		}
		
		float[] spectralFluxArray = new float[spectralFlux.size()];
		for( int i = 0; i < spectralFlux.size(); i++ )
			spectralFluxArray[i] = spectralFlux.get(i);
		Plot plot = new Plot( "Spectral Flux", 512, 512);
		plot.plot( spectralFluxArray, 1, Color.red );
	}
}
