package jnesg.chr;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;

// dnd
import java.awt.dnd.*;
import java.awt.datatransfer.*;

import jnesg.tile.*;
import jnesg.palette.*;
import jnesg.Main;

public class CHRBlock extends JPanel
	implements DragGestureListener, DropTargetListener
{
	public CHRBlock( int w, int h ) 
	{
		tilew = w;
		tileh = h;
		curRow = 0;
		curCol = 0;
		
		selectedR = selectedC = -2;
		
		tiles = new Tile[h][w];
		
		new DropTarget( this, this );
		Palette.initDnD( this );
		clear();
		
		addMouseListener( new MouseDown() );
		addMouseMotionListener( new MouseMotion() );
	}

	public Dimension getPreferredSize() { return dim(); }
	public Dimension getMinimumSize()	{ return dim(); }
	public Dimension getMaximumSize()	{ return dim(); }		
		
	public void clear()
	{
		for( int r = 0; r < tileh; ++r )
			for( int c = 0; c < tilew; ++c )
				tiles[r][c] = Tile.EMPTY;	
				
		if( Main.getCHRManager() != null )		
			Main.getCHRManager().repaint();
		else
			repaint();	
	}		
	
	public int getSelected()
	{
		if( corner1 == null ) return 0;
		
		return corner1.y * tiles[0].length + corner1.x;
	}		
	
	public void addTile( Tile t )
	{
		if( full )
		{
			JOptionPane.showMessageDialog( this,
				"CHR Palette is full, you must instead overwrite an existing entry" );
			return;
		}
		
		tiles[ curRow ][ curCol ] = t;
		++curCol;
		
		if( curCol == tilew )
		{
			++curRow;
			curCol = 0;
		}
		
		if( curRow == tileh )
		{
			full = true;
		}
		
		Main.getCHRManager().repaint();
	}
	
	public Tile getTile( int i )
	{
		int r = i / tilew;
		int c = i % tilew;
		
		return tiles[r][c];
	}
	
	public void setFromRaw( byte [] raw )
	{
		byte [][] tileData = null;
		
		int chunk = 0;
		for( int r = 0; r < tileh; ++r )
		{
			for( int c = 0; c < tilew; ++c )
			{
				tiles[r][c] = Tile.fromRaw( raw, chunk, 16 );
				chunk += 16;
			}
		}
	}
		
		
	public void paintComponent( Graphics g )
	{
		super.paintComponent(g);
		
		int y = 0, r = 0, divy = 0;
		for( r = 0; r < tileh/2 + 1; ++r )
		{
			y = r * ZOOM * Tile.SIZE;
			for( int c = 0; c < tilew; ++c )
				tiles[r][c].draw( g, y, c * ZOOM * Tile.SIZE );
		}
		
		divy = y;
		
		for( ; r < tileh; ++r )
		{
			y = r * ZOOM * Tile.SIZE + DIVIDER_HEIGHT;
			for( int c = 0; c < tilew; ++c )
				tiles[r][c].draw( g, y, c * ZOOM * Tile.SIZE );
		}	
		
		g.setColor( TileEditArea.LINECOLOR );
		
		for( int i = 0; i < tilew; ++i )
		{
			g.drawLine( i * Tile.SIZE * ZOOM, 
				0,
				i * ZOOM * Tile.SIZE,
				Tile.SIZE * ZOOM * tileh + DIVIDER_HEIGHT );
		}
		
		int i = 0;
		for( i = 0; i < tileh/2; ++i )
		{
			g.drawLine( 0, 
				i * Tile.SIZE * ZOOM,
				tilew * Tile.SIZE * ZOOM,
				i * Tile.SIZE * ZOOM );
		}			
	
		for( ; i < tileh; ++i )
		{
			g.drawLine( 0, 
				i * Tile.SIZE * ZOOM + DIVIDER_HEIGHT,
				tilew * Tile.SIZE * ZOOM,
				i * Tile.SIZE * ZOOM + DIVIDER_HEIGHT );
		}			

		g.setColor( Color.white );
		g.fillRect( 0, divy, Tile.SIZE * ZOOM * tiles[0].length, DIVIDER_HEIGHT );		
					
		drawSelectionBox(g);
	}
		
	private void drawSelectionBox( Graphics g )
	{
		if( corner1 == null ) return;
		
		Graphics2D g2 = (Graphics2D)g;
			
		g2.setStroke( new BasicStroke(3.0F));
				
		// deal with divider
		int adjust = 0;
		if( corner1.y > 15 ) adjust = DIVIDER_HEIGHT;
		
		if( corner1.equals( corner2 )  )
		{	
			Rectangle rec = new Rectangle( corner1.x * Tile.SIZE * ZOOM,
											corner1.y * Tile.SIZE * ZOOM + adjust,
											Tile.SIZE * ZOOM, Tile.SIZE * ZOOM );
			 
			g2.setColor( Color.white );
			g2.draw( rec ); 			
			
			return;
		}
			 
		
		Point otherCorner = null;
		// a "pending" box
		if( cornerPend != null )
			otherCorner = cornerPend;
		else
			otherCorner = corner2;
		
		if( otherCorner.y > 15 ) adjust = DIVIDER_HEIGHT;	
			
		Point p1 = new Point( corner1.x * Tile.SIZE * ZOOM, corner1.y * Tile.SIZE * ZOOM + adjust );
		Point p3 = new Point( otherCorner.x * Tile.SIZE * ZOOM, otherCorner.y * Tile.SIZE * ZOOM + adjust );
		Point p2 = new Point( p1.x, p3.y );
		Point p4 = new Point( p3.x, p1.y );
			
		if( corner2 != null ) g.setColor( Color.white );
		else g.setColor( PEND_BOX_COL );
		
		Polygon poly = new Polygon( new int[] { p1.x, p2.x, p3.x, p4.x },
					new int[] { p1.y, p2.y, p3.y, p4.y }, 4);
		
		g2.draw( poly ); 
	}
		
	private Dimension dim()
	{
		return new Dimension( tilew * Tile.SIZE * ZOOM, tileh * Tile.SIZE * ZOOM + DIVIDER_HEIGHT );
	}		
	
	
	/********************** DROP *************************/
	
	public void dragEnter( DropTargetDragEvent dtde )
	{
	}
	
	public void dragExit( DropTargetEvent dte )
	{
	}
	
	public void dragOver( DropTargetDragEvent dtde )
	{
	}
	
	public void drop( DropTargetDropEvent dtde )
	{
		try
		{
			if( !dropSupported(dtde) )
			{
				dtde.rejectDrop();
				return;
			}
			
			Transferable t = dtde.getTransferable();
			
			DataFlavor d = t.getTransferDataFlavors()[0];
			
			if( !d.getRepresentationClass().equals( Tile.class ) )
			{
				dtde.rejectDrop();
				return;
			}
			
			Tile [][] tiles = (Tile [][])t.getTransferData(d);
			
			//System.out.println("CHRBlock received tile array of : " + tiles.length + " x " + tiles[0].length );
			
			Point loc = dtde.getLocation();
		
			updateTilesAt( loc, tiles );
			
			dtde.acceptDrop( DnDConstants.ACTION_COPY );
					
			dtde.dropComplete(true);
			
			Main.getCHRManager().repaint();
		}
		catch( Exception e )
		{
		}
	}
	
	public void dropActionChanged( DropTargetDragEvent dtde )
	{
	}
	
	private static boolean dropSupported( DropTargetDropEvent d )
	{
		return ( d.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE ) != 0;
	}		
	
	/********** DRAG *************************************/
	
	public void dragGestureRecognized( DragGestureEvent dge )
	{
		MouseEvent me = (MouseEvent)dge.getTriggerEvent();
		
		if( me.isShiftDown() ) return;
		
		Tile tiles [][] = getTilesAt( dge.getDragOrigin() );
		Transferable t = new TileTransporter( tiles );
		dge.startDrag( null, t, new jnesg.palette.Palette.PaletteDragSourceListener() );
	}	
	
	/******************** PULLING INDIVIDUAL TILES ************/
	
	public Tile getTileAt( Point p )
	{
		if( p.y > 16 * Tile.SIZE * ZOOM )
			p.y -= DIVIDER_HEIGHT;
		
		int row = p.y / (Tile.SIZE * ZOOM);
		int col = p.x / (Tile.SIZE * ZOOM);
		
		if( row >= tiles.length ) return null;
		if( col >= tiles[0].length ) return null;
		
		return tiles[row][col];
	}
	
	public Tile [][] getTilesAt( Point p )
	{
		// if there's current a selection, see if user is dragging from it
		boolean fromSelect = false;
		Polygon poly = null;
		
		if( corner2 != null )
		{
			Point p1 = new Point( corner1.x, corner1.y );
			Point p3 = new Point( corner2.x, corner2.y );
			Point p2 = new Point( p1.x, p3.y );
			Point p4 = new Point( p3.x, p1.y );
				
			poly = new Polygon( new int[] { p1.x, p2.x, p3.x, p4.x },
						new int[] { p1.y, p2.y, p3.y, p4.y }, 4);
					
			if( poly.contains( p.x / (Tile.SIZE * ZOOM), p.y / (Tile.SIZE * ZOOM) ) )
				fromSelect = true;		
		}
		
		if( !fromSelect )
		{
			System.out.println("Not from select");
			return new Tile [][] { new Tile [] { getTileAt(p) } };
		}
	
		Rectangle rect = poly.getBounds();
		
		Tile [][] ret = new Tile [ rect.height ][ rect.width ];
		{
			for( int r = 0; r < rect.height; ++r )
			{
				for( int c = 0; c < rect.width; ++c )
				{
					ret[r][c] = tiles[ rect.y + r ][ rect.x + c ];
				}
			}
		}
		
		return ret;
	}	
		
		
	public Tile[][] getTiles() { return tiles; }
	
	private void updateTileAt( Point p, Tile t )
	{
		int y;
		
		if( p.y > Tile.SIZE * ZOOM * 16 )
			y = p.y - DIVIDER_HEIGHT;
		else
			y = p.y;
		
		int row = y / (Tile.SIZE * ZOOM);
		int col = p.x / (Tile.SIZE * ZOOM);
		
		if( row >= tiles.length ) return;
		if( col >= tiles[0].length ) return;
		
		tiles[row][col] = t;		
	}
	
	private void updateTilesAt( Point p, Tile [][] t )
	{
		for( int r = 0; r < t.length; ++r )
		{
			for( int c = 0; c < t[r].length; ++c )
			{
				Point pp = new Point( p.x + c * Tile.SIZE * ZOOM, p.y + r * Tile.SIZE * ZOOM );
				updateTileAt( pp, t[r][c] );
			}
		}
	}
		
	
	private class MouseDown extends MouseAdapter
	{
		public void mousePressed( MouseEvent e )
		{
			//System.out.println("mousePressed");
			if( !e.isShiftDown() ) return;
			
			int y = e.getY();
			int x = e.getX();
			
			if( y > 16 * Tile.SIZE * ZOOM )
				y -= DIVIDER_HEIGHT;
						
			corner1 = new Point(  x / (Tile.SIZE * ZOOM ),  y / (Tile.SIZE * ZOOM ) );
			corner2 = null;
		}
		
		public void mouseReleased( MouseEvent e )
		{
			if( !e.isShiftDown() ) return;			
			
			//System.out.println("mouseReleased");
			int y = e.getY();
			int x = e.getX();
			
			if( y > 16 * Tile.SIZE * ZOOM )
				y -= DIVIDER_HEIGHT;
						
			corner2 = new Point(  x / (Tile.SIZE * ZOOM ),  y / (Tile.SIZE * ZOOM ) );
			cornerPend = null;
			
			repaint();
		}		
	}
			
	private class MouseMotion extends MouseMotionAdapter
	{
		public void mouseDragged( MouseEvent e )
		{
			if( !e.isShiftDown() ) return;			
			
			//System.out.println("mouseDragged");
			int y = e.getY();
			int x = e.getX();
			
			if( y > 16 * Tile.SIZE * ZOOM )
				y -= DIVIDER_HEIGHT;
						
			cornerPend = new Point(  x / (Tile.SIZE * ZOOM ),  y / (Tile.SIZE * ZOOM ) );
			
			repaint();			
		}
	}
	
	private static final int DIVIDER_HEIGHT = 10;
	private static final Color PEND_BOX_COL = new Color( 150, 150, 150 );
	
	
	Point corner1, cornerPend, corner2;
	
	private Tile [][] tiles;
	private int curRow, curCol;
	private final int tilew, tileh;
	private static final int ZOOM = 2;
	private boolean full = false;
	
	private int selectedR, selectedC;
}