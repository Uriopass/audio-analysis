package com.badlogic.audio.samples;

import com.badlogic.audio.io.AudioDevice;

/**
 * A simple generator that outputs a sinewave at some
 * frequency (here 440Hz = Note A) in mono to an {@link AudioDevice}.
 * 
 * @author mzechner
 *
 */
public class NoteGenerator 
{
	public static void main( String[] argv ) throws Exception
	{
		final float frequency = 880; // 440Hz for note A
		float increment = (float)(2*Math.PI) * frequency / 44100; // angular increment for each sample
		float angle = 0;
		AudioDevice device = new AudioDevice( );
		float samples[] = new float[1024];
		
		while( true )
		{
			for( int i = 0; i < samples.length; i++ )
			{
				samples[i] = (float)Math.sin( angle );
				angle += increment;
			}
			
			device.writeSamples( samples );
		}
	}
}
