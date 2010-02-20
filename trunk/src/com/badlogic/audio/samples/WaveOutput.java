package com.badlogic.audio.samples;

import java.io.FileInputStream;

import com.badlogic.audio.io.AudioDevice;
import com.badlogic.audio.io.WaveDecoder;

/**
 * A simple example how to read in a Wave file via 
 * a {@link WaveDecoder} and output its contents to 
 * an {@link AudioDevice}.
 * @author mzechner
 *
 */
public class WaveOutput 
{
	public static void main( String[] argv ) throws Exception
	{
		AudioDevice device = new AudioDevice( );
		WaveDecoder decoder = new WaveDecoder( new FileInputStream( "samples/sample.wav" ) );
		float[] samples = new float[1024];
		
		while( decoder.readSamples( samples ) > 0 )
		{
			device.writeSamples( samples );
		}
	}
}
