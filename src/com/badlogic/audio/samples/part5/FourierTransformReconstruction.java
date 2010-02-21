package com.badlogic.audio.samples.part5;

import java.io.FileInputStream;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.io.AudioDevice;
import com.badlogic.audio.io.WaveDecoder;

/**
 * A simple example that shows that transforming samples to
 * the frequency domain and back to the time domain preserves
 * the original signal nearly perfectly.
 * @author mzechner
 *
 */
public class FourierTransformReconstruction 
{
	public static void main( String[] argv ) throws Exception
	{
		AudioDevice device = new AudioDevice( );
		WaveDecoder decoder = new WaveDecoder( new FileInputStream( "samples/sample.wav" ) );
		float[] samples = new float[1024];
		FFT fft = new FFT( 1024, 44100 );
		
		while( decoder.readSamples( samples ) > 0 )
		{
			fft.forward( samples );
			fft.inverse( samples );
			device.writeSamples( samples );
		}
	}
}
