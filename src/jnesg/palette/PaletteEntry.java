package jnesg.palette;

import java.awt.*;
import javax.swing.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.List;

public class PaletteEntry extends JPanel
	implements DragGestureListener
{
	public PaletteEntry( Palette o, Color c, int i )
	{
		owner = o;
		color = c;
		id = i;
		masterIndex = i;
			
		setBorder( BorderFactory.createLineBorder( BORDERCOLOR ) );
	}
	
	public PaletteEntry( Palette o, int r, int g, int b, int i )
	{
		this( o, new Color( r, g, b ), i );
	}	
			
	public Dimension getPreferredSize() { return dim; }
	public Dimension getMinimumSize()	{ return dim; }
	public Dimension getMaximumSize()	{ return dim; }
	
	public void setFocused( boolean b ) { focused = b; repaint(); }
	public Color getColor() { return color; }
	public int getMasterIndex() { return masterIndex; }
	public void setMasterIndex( int mi ) { masterIndex = mi; }
	
	public void paintComponent(Graphics g)
	{	
		super.paintComponent(g);
		
		g.setColor(color);
		g.fillRect( 0, 0, dim.width, dim.height );
		
		if(focused)
		{
			g.setColor( Color.white );
			g.drawRect( 3, 3, dim.width - 6, dim.height - 6 );
		}
			
	}
	
	public void setColor( Color c )
	{
		color = c;
	}
	
	// DragGestureListener implementation
	
	public void dragGestureRecognized( DragGestureEvent dge )
	{
		Transferable t = new PaletteTransporter( this );
		dge.startDrag( null, t, new Palette.PaletteDragSourceListener() );
	}
	
	public static final Color BORDERCOLOR = new Color( 232, 244, 249 );	
	
	public static final int WIDTH = 15, HEIGHT = 15;	
	
	protected static Dimension dim = new Dimension( WIDTH, HEIGHT );
	protected Color color;
	protected boolean focused;
	protected final Palette owner;
	protected final int id;
	protected int masterIndex;
}

	