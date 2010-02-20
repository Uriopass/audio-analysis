package com.badlogic.audio.io;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * A simple MP3 decoder based on JLayer
 * @author mzechner
 *
 */
public class MP3Decoder 
{
	/** inverse max short value as float **/
	private final float MAX_VALUE = 1.0f / Short.MAX_VALUE;
	
	/** the bit stream **/
	private final Bitstream bitStream;
	
	/** the decoder **/
	private final Decoder decoder;
	
	/** samples left over **/
	private float[] leftOverSamples = new float[1024];
	
	/** how many samples are left over **/
	private int leftOver = 0;
	
	/**
	 * Constructor, sets the input stream to read the mp3 from
	 * @param stream The input stream.
	 * @throws Exception in case something baaaaad happened.
	 */
	public MP3Decoder( InputStream stream ) throws Exception
	{
		bitStream = new Bitstream( new BufferedInputStream( stream, 1024*1024) );		
		decoder = new Decoder( );
	}
	
	/**
	 * Tries to read in samples.length samples, merging stereo to a mono
	 * channel by averaging and converting non float formats to float 32-bit.
	 * Returns the number of samples actually read. Guarantees that samples.length
	 * samples are read in if there was enough data in the stream.
	 * 
	 * @param samples The samples array to write the samples to
	 * @return The number of samples actually read.
	 */
	public int readSamples( float[] samples )
	{
		int readSamples = 0;
		if( leftOver > 0 )
		{
			int maxSamples = Math.min( leftOver, samples.length );
			for( int i = 0; i < maxSamples; i++, readSamples++ )
				samples[i] = leftOverSamples[i];
			
			if( leftOver > samples.length )
			{
				leftOver = leftOver - samples.length;
				System.arraycopy( leftOverSamples, leftOverSamples.length - leftOver, leftOverSamples, 0, leftOver );
				return samples.length;
			}
			else
				leftOver = 0;
		}
		
		try 
		{
			Header header = bitStream.readFrame();
			if( header == null )
				return 0;
			
			float frequency = header.frequency();
			if( frequency != 44100 )
			{
				bitStream.closeFrame();			
				return 0;			
			}
			
			SampleBuffer frame = (SampleBuffer)decoder.decodeFrame( header, bitStream);
			if( frame.getBufferLength() > leftOverSamples.length )
				leftOverSamples = new float[frame.getBufferLength()];
			
			int channels = frame.getChannelCount();
			if( channels > 2 )
			{
				bitStream.closeFrame();			
				return 0;
			}						
						
			for( int i = 0; i < frame.getBufferLength(); )
			{
				float value = frame.getBuffer()[i++] * MAX_VALUE;
				if( channels == 2 )
				{
					value += frame.getBuffer()[i++] * MAX_VALUE;
					value /= 2;
				}					
				
				if( readSamples >= samples.length )								
					leftOverSamples[leftOver++] = value;				
				else				
					samples[readSamples++] = value;				
			}
			
			bitStream.closeFrame();
			return readSamples;
		} catch (Exception e) 
		{		
			return 0;
		}
	}
}
