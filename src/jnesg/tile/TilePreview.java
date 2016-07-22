package jnesg.tile;

/* 
 *	essentially the same class as TileEditArea,
 *	but with support for dragging into and out of
 *	CHRBlocks
 */

import java.awt.dnd.*;
import java.awt.*;
import java.awt.datatransfer.*;

import jnesg.palette.*;

public class TilePreview extends TileEditArea
	implements DragGestureListener, DropTargetListener
{

	public TilePreview( int z, TileEditArea p )
	{
		// setup via super class, zoom level of z, no editing, no gridlines
		super( z, false, false, 0, 0 );
		parent = p;
		
		Palette.initDnD( this );
		new DropTarget( this, this );
	}
	
	
	/******************* DROP *********************/
	
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
		
		
			parent.setData( tiles );
		
			dtde.acceptDrop( DnDConstants.ACTION_COPY );
					
			dtde.dropComplete(true);
			
			parent.repaint();
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
		Transferable t = new TileTransporter( Tile.createArray( getData() ) );
		dge.startDrag( null, t, new jnesg.palette.Palette.PaletteDragSourceListener() );
	}

	private TileEditArea parent;	
}
