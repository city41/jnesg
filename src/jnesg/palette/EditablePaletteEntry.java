package jnesg.palette;

import java.awt.*;
import javax.swing.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.List;

import jnesg.Main;

public class EditablePaletteEntry extends PaletteEntry
	implements DropTargetListener
{
	public EditablePaletteEntry( Palette o, Color c, int i )
	{
		super( o, c, i );
		masterIndex = 0;
		
		new DropTarget( this, this );
	}
	
	public EditablePaletteEntry( Palette o, int r, int g, int b, int i )
	{
		this( o, new Color( r, g, b ), i );
	}
	
	/******************DRAG AND DROP *********************/
	
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
			
			if( !d.getRepresentationClass().equals( PaletteEntry.class )
				&& !d.getRepresentationClass().equals( EditablePaletteEntry.class) )
			{
				dtde.rejectDrop();
				return;
			}
			
			PaletteEntry pe = (PaletteEntry)t.getTransferData(d);
		
			dtde.acceptDrop( DnDConstants.ACTION_COPY );
					
			color = pe.getColor();
			
			dtde.dropComplete(true);
			
			setToolTipText( pe.getToolTipText() );
			masterIndex = pe.getMasterIndex();
			
			repaint();
			
			Main.getTileEditor().repaint();
			Main.getCHRManager().repaint();
			
			if( (id % 4) == 0 )	
				Main.getPaletteEditor().updateMirrors( id );
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
}	