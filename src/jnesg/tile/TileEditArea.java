package jnesg.tile;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

import jnesg.palette.PaletteEditor;
import jnesg.Main;

public class TileEditArea extends JPanel
{
	public TileEditArea( int z, boolean sg, boolean ed, int rows, int cols)
	{
		zoom = z;
		editable = ed;
		showGrid = sg;		
		editMode = PAINT;
		
		if( cols > 0 ) tilew = cols;
		if( rows > 0 ) tileh = rows;
		
		if(editable)
		{
			addMouseListener( new MouseDown() );	
			addMouseMotionListener( new MouseMotion() );
			data = new byte[ Tile.SIZE * tileh ][ Tile.SIZE * tilew ];
			reset();			
		}	
		setToolTipText( "" + zoom + "x" );
		
		me = this;
		untouched = true;
	}
	
	public String toString()
	{
		String ret = "";
		for( int r = 0; r < data.length; ++r )
		{
			for( int c = 0; c < data[r].length; ++c )
				ret += "[" + data[r][c] + "]";
			
			ret += "\n";
		}
		
		return ret;
	}			
	
	public boolean empty() { return untouched; }
	
	public void setGridVisible( boolean g )
	{
		showGrid = g;
		repaint();
	}
	
	public void setNew( int r, int c, int z )
	{
		tileh = r;
		tilew = c;
		zoom = z;
		
		byte [][] d = new byte[ Tile.SIZE * tileh ][ Tile.SIZE * tilew ];
		setData( d );
		
		reset();	
		
		//System.out.println("Set New: r: " + r + " c: " + c );
	}

	public byte [][] getData() { return data; }
	
	public void setData( byte [][] d )
	{
		data = d;
		tileh = d.length / Tile.SIZE;
		tilew = d[0].length / Tile.SIZE;
		
		//System.out.println("setData: data.length: " + data.length + " data[0].length: " + data[0].length );
		
		if( preview != null ) preview.setData( data );
		
		if( Main.getTileEditor() != null )
			Main.getTileEditor().resetEditor();
	}
	
	public void setData( Tile [][] t )
	{
		byte [][] d = new byte[ t.length * Tile.SIZE ][ t[0].length * Tile.SIZE ];
		
		for( int r = 0; r < t.length; ++r )
		{
			for( int c = 0; c < t[r].length; ++c )
			{
				byte [][] td = t[r][c].getData();
				
				int rbase = r * Tile.SIZE;
				int cbase = c * Tile.SIZE;
				
				for( int tr = 0; tr < td.length; ++tr )
				{
					for( int tc = 0; tc < td[tr].length; ++tc )
					{
						d[rbase + tr][cbase + tc] = td[tr][tc];
					}
				}
			}
		}
		
		System.out.println("setData with tiles...");
		System.out.println("tile array r: " + t.length + " c: " + t[0].length );
		System.out.println("byte array r: " + d.length + " c: " + d[0].length );
		
		setData( d );
	}
			
	public void setPreview( TileEditArea tp )
	{
		preview = tp;
		preview.tilew = tilew;
		preview.tileh = tileh;
	}
	
	public void setEditMode( int em )
	{
		if( em < PAINT || em > FILL )
			return;
			
		editMode = em;
	}
	
	public void reset()
	{
		for( int i = 0; i < data.length; ++i )
		{
			for( int k = 0; k < data[i].length; ++k )
				data[i][k] = 0;
		}
		
		untouched = true;
	}
	
	public Tile getTile()
	{
		Tile t = new Tile( data, true );
		return t;
	}

	public void setZoom( int z )
	{
		zoom = z;
	}
	
	public Dimension getPreferredSize() { return dim(); }
	public Dimension getMinimumSize()	{ return dim(); }
	public Dimension getMaximumSize()	{ return dim(); }		
	
	public static final Color LINECOLOR = new Color( 100, 100, 100 );
	
	public void paintComponent( Graphics g )
	{
		super.paintComponent(g);		
			
		for( int r = 0; r < data.length; ++r )
		{
			int y = r * zoom;
		
			for( int c = 0; c < data[r].length; ++c )
			{
				g.setColor( Main.getPaletteEditor().getMasterColor( data[r][c] ));
				g.fillRect( c * zoom, y, zoom, zoom );
			}
		}
		
		if( showGrid )
		{

			for( int i = 0; i < Tile.SIZE * tilew; ++i )
			{	
				if( i % Tile.SIZE == 0 )
					g.setColor( Color.white );
				else
					g.setColor( LINECOLOR );
												
				g.drawLine( i*zoom , 0, i * zoom, Tile.SIZE * zoom * tileh );
			}
			for( int i = 0; i < Tile.SIZE * tileh; ++i )
			{
				if( i % Tile.SIZE == 0 )
					g.setColor( Color.white );
				else
					g.setColor( LINECOLOR );
									
				g.drawLine( 0, i * zoom, Tile.SIZE * zoom * tilew, i * zoom );
			}
		}
		
		if( preview != null )
		{
			preview.repaint();
		}		
	}
	
	private Dimension dim()
	{	
		Dimension d = new Dimension( Tile.SIZE * tilew * zoom, Tile.SIZE * tileh * zoom ); 
		//System.out.println("Tile.SIZE: " + Tile.SIZE );
		//System.out.println("tilew: " + tilew );
		//System.out.println("tileh: " + tileh );
		//System.out.println("zoom: " + zoom );
		//System.out.println("dim: " + d );
		return d;
	}
		
	private void updateTile( MouseEvent e )
	{
		int row = e.getY() / zoom;
		int col = e.getX() / zoom;
		
		if( row >= data.length
			|| col >= data[0].length
			|| row < 0
			|| col < 0 
			)
			return;
		
		byte newCol;
		
		if( e.isMetaDown() || e.isShiftDown() )
			newCol = 0;
		else	
			newCol = Main.getPaletteEditor().getCurrentSubIndex();
		
		
		data[row][col] = newCol;
		
		untouched = false;		
		repaint();
	}
	
	private boolean [][] visited;
	
	private void fill( MouseEvent e )
	{
		int row = e.getY() / zoom;
		int col = e.getX() / zoom;
	
		if( row >= data.length
			|| col >= data[0].length
			|| row < 0
			|| col < 0 
			)
			return;
		
		byte oldCol = data[ row ][ col ];	
		byte newCol;
		
		if( e.isMetaDown() || e.isShiftDown() )
			newCol = 0;
		else
			newCol = Main.getPaletteEditor().getCurrentSubIndex();
			
		visited = new boolean[ data.length][ data[0].length ];	
			
		try
		{
			recurseFill( row, col, oldCol, newCol );	
		}
		catch( StackOverflowError sofe )
		{
			System.err.println("Stack overflow");
		}
		repaint();
		untouched = false;		
	}
	
	private void recurseFill( int r, int c, byte oc, byte nc )
	{
		if( data[r][c] != oc || visited[r][c] ) return;
		
		data[r][c] = nc;
		visited[r][c] = true;
		
		if( r > 0 )
			recurseFill( r-1, c, oc, nc );
			
		if( c > 0 )
			recurseFill( r, c-1, oc, nc );			
			
		if( r < data.length-1 )
			recurseFill( r+1, c, oc, nc );
			
		if( c < data[0].length-1 )
			recurseFill( r, c+1, oc, nc );
	}	
			
	private class MouseDown extends MouseAdapter
	{
		public void mousePressed( MouseEvent e )
		{
			if( editMode == PAINT )
				updateTile(e);
				
			else if( editMode == FILL )
				fill(e);
		}
	}
	
	private class MouseMotion extends MouseMotionAdapter
	{
		public void mouseDragged( MouseEvent e )
		{
			if( editMode == PAINT )
				updateTile(e);
				
			else if( editMode == FILL )
				fill(e);
		}
	}		
	
	public static final int PAINT = 0, FILL = 1;	
		
	protected boolean untouched;	
	protected int zoom;	
	protected byte [][] data;
	protected int editMode;	
			
	private static int tilew, tileh;
	private TileEditArea me;
	private boolean editable, showGrid;	
	private TileEditArea preview;
}
	
	


	