package jnesg.palette;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import jnesg.Main;

public class EditablePalette extends Palette
{
	public EditablePalette( byte [] pal )
	{
		this();
		
		int bad = 0;
		
		for( int i = 0; i < pal.length; ++i )
		{
			byte mi = pal[i];
			
			if( mi < 0 || mi > 63 )
			{
				mi = 0;
				bad++;
			}
			
			setPaletteEntryMasterIndex( i, pal[i] );
		}
		
		if( bad > 0 )
		{
			JOptionPane.showMessageDialog( Main.getMain(), "Warning: " + 
				bad + " colors were out of range and reset to zero" );
		}
	}
	
	public EditablePalette()
	{
		MouseDown md = new MouseDown();
		
		setLayout( new GridLayout( 1, NUM_COLORS ));
		
		colors = new EditablePaletteEntry[ NUM_COLORS ];
		
		Color startCol = new Color( 0x80, 0x80, 0x80 );
		
		for( int i = 0; i < colors.length; ++i )
		{			
			colors[i] = new EditablePaletteEntry( this, startCol, i );
			colors[i].addMouseListener( md );
			initDnD( colors[i] );
			add( colors[i] );
		}
		
		colors[0].setFocused( true );
		focusedIndex = 0;
		
		dim = new Dimension( colors.length * colors[0].getPreferredSize().width,
			colors[0].getPreferredSize().height );	
	}

	public void updateMirrors( int source )
	{
		int newMasterIndex = colors[source].getMasterIndex();
		String newToolTip = colors[source].getToolTipText();
		Color newColor = colors[source].getColor();
		
		for( int i = 0; i < colors.length; i += 4 )
		{
			colors[i].setMasterIndex( newMasterIndex );
			colors[i].setToolTipText( newToolTip );
			colors[i].setColor( newColor );
			colors[i].repaint();
		}
	}
		
	
	public static void setMirroredColor( Color c, String toolTipText, int mi )
	{
		for( Iterator iter = mirroredColors.iterator(); iter.hasNext(); )
		{
			EditablePaletteEntry pe = (EditablePaletteEntry)iter.next();
			pe.setColor(c);
			pe.setToolTipText( toolTipText );
			pe.setMasterIndex( mi );
		}
	}

	public void clear()
	{
		for( int i = 0; i < colors.length; ++i )
			setPaletteEntryMasterIndex( i, 0 );
			
		repaint();
	}
		
	public String toString()
	{
		StringBuffer sbuf = new StringBuffer();
		
		for( int i = 0; i < colors.length; ++i )
		{
			if( i % 4 == 0 ) sbuf.append("\n");
			sbuf.append( colors[i].toString() );
		}
		
		return sbuf.toString();
	}	
	
	public int getFocusedIndex()
	{
		return focusedIndex;
	}
	
	public int getMasterIndex( int i )
	{
		return colors[i].getMasterIndex();
	}
	
	public void setPaletteEntryMasterIndex( int i, int v )
	{
		colors[i].setMasterIndex(v);
		colors[i].setColor( Main.getPaletteEditor().getTrueMasterColor(v) );
	}
	
	/****************** MOUSE LISTENER *****************/
	
	private class MouseDown extends MouseAdapter
	{
		public void mousePressed( MouseEvent e )
		{			
			Object o = e.getSource();
			
			for( int i = 0; i < colors.length; ++i )
			{
				if( colors[i] == o )
				{
					focusedIndex = i;
					colors[i].setFocused(true);
				}
				else
					colors[i].setFocused(false);
			}	
		}
	}
		
	public Dimension getPreferredSize() { return dim; }
	public Dimension getMinimumSize()	{ return dim; }
	public Dimension getMaximumSize()	{ return dim; }		
	
	public static final int NUM_COLORS = 16;
	
	private Dimension dim;
				
	private EditablePaletteEntry [] colors;
	private static List mirroredColors = new LinkedList();
	private int focusedIndex;
	private String title;
}