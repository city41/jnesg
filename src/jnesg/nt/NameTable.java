package jnesg.nt;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import jnesg.tile.*;
import jnesg.Main;

public class NameTable extends JPanel
{
	public NameTable( int z, boolean sg, int rows, int cols)
	{
		zoom = z;
		showGrid = sg;
		
		data = new byte[ rows ][ cols ];
		
		addMouseListener( new MouseDown() );
		addMouseMotionListener( new MouseMotion() );
		editMode = TileEditor.PAINT; 
		invalid = new LinkedList();
	}
	
	public NameTable( int z, boolean sg, byte [][] d )
	{
		zoom = z;
		showGrid = sg;
		data = d;
	
		addMouseListener( new MouseDown() );
		addMouseMotionListener( new MouseMotion() );
		editMode = TileEditor.PAINT; 
		invalid = new LinkedList();
	}
	
	public byte [][] getData() { return data; }
	
	public void setEditMode( int em )
	{
		editMode = em;
	}
	
	public void clear()
	{
		for( int r= 0; r < data.length; ++r )
			for( int c = 0; c < data[r].length; ++c )
				data[r][c] = 0;
				
		repaint();
	}
	
	public void setHigh()
	{
		half = 0;
		repaint();
	}
	
	public void setLow()
	{
		half = 256;
		repaint();
	}
	
	public void setGridVisible( boolean g )
	{
		showGrid = g;
		repaint();
	}
	
	public void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		
		for( int r = 0; r < data.length; ++r )
		{
			for( int c = 0; c < data[r].length; ++c )
			{
				int tileInd = data[r][c];
				if( tileInd < 0 ) tileInd += 256;
				
				Tile t = Main.getCHRManager().getTile( tileInd + half );
				g.drawImage( t.getImage(), c * Tile.SIZE * zoom, r * Tile.SIZE * zoom, this );
			}
		}
		
		if( showGrid )
		{
			g.setColor( TileEditArea.LINECOLOR );
			
			for( int i = 0; i < data[0].length; ++i )
			{								
				g.drawLine( i*zoom * Tile.SIZE , 0, i * zoom * Tile.SIZE, zoom * data.length * Tile.SIZE );
			}
			for( int i = 0; i < data.length; ++i )
			{
				g.drawLine( 0, i * zoom * Tile.SIZE, zoom * data[0].length * Tile.SIZE, i * zoom * Tile.SIZE );
			}
		}
	}
		
	
	private void updateTile( MouseEvent e )
	{
		int row = e.getY() / ( zoom * Tile.SIZE );
		int col = e.getX() / ( zoom * Tile.SIZE );
		
		if( row >= data.length
			|| col >= data[0].length
			|| row < 0
			|| col < 0 
			)
			return;
		
		invalid.add( new Point(col, row) );	
			
		int newCol = 0;
		
		if( e.isMetaDown() || e.isShiftDown() )
			newCol = half;
		else	
			newCol = Main.getCHRManager().getCurrentTileIndex() - half;
			
		if( newCol < 0 || newCol > 255 )
			return;
		
		data[row][col] = (byte)newCol;
		
		untouched = false;		
		//repaintInvalid();
		repaint();
	}
	
	private void repaintInvalid()
	{
		Graphics g = getGraphics();
		
		for( Iterator iter = invalid.iterator(); iter.hasNext(); )
		{
			Point p = (Point)iter.next();
			iter.remove();
			
			Tile t = Main.getCHRManager().getTile( data[p.y][p.x] );
			
			g.drawImage( t.getImage(), p.x * Tile.SIZE * zoom, p.y * Tile.SIZE * zoom, this );
		}
	}
				
	private void fill( MouseEvent e )
	{
		int row = e.getY() / ( zoom * Tile.SIZE );
		int col = e.getX() / ( zoom * Tile.SIZE );

		if( row >= data.length
			|| col >= data[0].length
			|| row < 0
			|| col < 0 
			)
			return;
		
		byte oldCol = data[ row ][ col ];	
		int nc;
		
		if( e.isMetaDown() || e.isShiftDown() )
			nc = half;
		else	
			nc = Main.getCHRManager().getCurrentTileIndex() - half;
			
		if( nc < 0 || nc > 255 )
			return;
		
		byte newCol = (byte)nc;
		
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
		if( data[r][c] != oc ) return;
		
		data[r][c] = nc;
		
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
			if( editMode == TileEditor.PAINT )
				updateTile(e);
				
			else if( editMode == TileEditor.FILL )
				fill(e);
				
			//else eyeDrop(e);	
		}
	}
	
	private class MouseMotion extends MouseMotionAdapter
	{
		public void mouseDragged( MouseEvent e )
		{
			if( editMode == TileEditor.PAINT )
				updateTile(e);
				
			else if( editMode == TileEditor.FILL )
				fill(e);
		}
	}		
	
	public Dimension getPreferredSize() { return dim(); }
	public Dimension getMinimumSize()	{ return dim(); }
	public Dimension getMaximumSize()	{ return dim(); }		
	
	private Dimension dim()
	{
		return new Dimension( data[0].length * Tile.SIZE * zoom, data.length * Tile.SIZE * zoom );
	}			
	
	private int editMode;
	private int half;
	private int zoom;
	private boolean showGrid, untouched;
	private byte [][] data;
	private boolean [][] visited;
	private List invalid;
}
