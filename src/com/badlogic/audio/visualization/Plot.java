package com.badlogic.audio.visualization;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * A simple class that allows to plot float[] arrays
 * to a swing window. The first function to plot that
 * is given to this class will set the minimum and 
 * maximum height values. I'm not that good with Swing
 * so i might have done a couple of stupid things in here :)
 * 
 * @author mzechner
 *
 */
public class Plot 
{
	/** the frame **/
	private JFrame frame;
	
	/** the scroll pane **/
	private JScrollPane scrollPane;
	
	/** the image gui component **/
	private JLabel panel;
	
	/** the image **/
	private BufferedImage image;
	
	/** current samples to draw **/
	private float[] currentSamples;
	
	/** the last scaling factor to normalize samples **/
	private float scalingFactor = 1;
	
	/** wheter the plot was cleared, if true we have to recalculate the scaling factor **/
	private boolean cleared = true;
	
	/**
	 * Creates a new Plot with the given title and dimensions.
	 * 
	 * @param title The title.
	 * @param width The width of the plot in pixels.
	 * @param height The height of the plot in pixels.
	 */
	public Plot( final String title, final int width, final int height )
	{
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() 
			{
				frame = new JFrame( title );
				frame.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
				frame.setPreferredSize( new Dimension( width, height ) );
				BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_4BYTE_ABGR );
				Graphics2D g = (Graphics2D)img.getGraphics();
				g.setColor( Color.black );
				g.fillRect( 0, 0, width, height );
				g.dispose();
				image = img;
				panel = new JLabel( new ImageIcon( img ) );
				scrollPane = new JScrollPane( panel );	
				frame.getContentPane().add(scrollPane);
				frame.pack();
				frame.setVisible( true );
				
			}
		});
	}
	
	public void clear( )
	{
		SwingUtilities.invokeLater( new Runnable( ) {

			@Override
			public void run() {
				Graphics2D g = image.createGraphics();
				g.setColor( Color.black );
				g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
				g.dispose();
				cleared = true;
			}
		});
	}
	
	public void plot( float[] samples, final float samplesPerPixel, final Color color )
	{
		final float[] smps = new float[samples.length];
		System.arraycopy( samples, 0, smps, 0, samples.length );
		SwingUtilities.invokeLater( new Runnable( ) 
		{
			@Override
			public void run() 
			{
				if( image.getWidth() <  smps.length / samplesPerPixel )
				{
					image = new BufferedImage( (int)(smps.length / samplesPerPixel), frame.getHeight(), BufferedImage.TYPE_4BYTE_ABGR );
					Graphics2D g = image.createGraphics();
					g.setColor( Color.black );
					g.fillRect( 0, 0, image.getWidth(), image.getHeight() ); 
					g.dispose();
				}
					
				if( cleared )
				{
					float min = 0;
					float max = 0;
					for( int i = 0; i < smps.length; i++ )
					{
						min = Math.min( smps[i], min );
						max = Math.max( smps[i], max );
					}
					scalingFactor = max - min;
					cleared = false;
				}
				
				Graphics2D g = image.createGraphics();
				g.setColor( color );
				float lastValue = (smps[0] / scalingFactor) * image.getHeight() / 3 + image.getHeight() / 2;
				for( int i = 1; i < smps.length; i++ )
				{
					float value = (smps[i] / scalingFactor) * image.getHeight() / 3 + image.getHeight() / 2;
					g.drawLine( (int)((i-1) / samplesPerPixel), image.getHeight() - (int)lastValue, (int)(i / samplesPerPixel), image.getHeight() - (int)value );
					lastValue = value;
				}
				g.dispose();
				
				panel.setIcon( new ImageIcon( image ) );
			}
		});
	}
}
