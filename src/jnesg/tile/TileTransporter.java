package jnesg.tile;

import java.awt.dnd.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class TileTransporter implements Transferable
{
	public TileTransporter( Tile [][] t )
	{
		tiles = t;
	}
	
	public Tile [][] getTiles() { return tiles; }
	
	public Object getTransferData( DataFlavor flavor )
		throws UnsupportedFlavorException
	{
		if( Tile.class != flavor.getRepresentationClass() )
			throw new UnsupportedFlavorException(flavor);
			
		return tiles;
	}
	
	public DataFlavor [] getTransferDataFlavors() { return flavorArray; }
	
	public boolean isDataFlavorSupported( DataFlavor flavor )
	{
		return Tile.class == flavor.getRepresentationClass();
	}
	
	private static final DataFlavor [] flavorArray = { new DataFlavor( Tile.class, "Tile" ) };
	private Tile [][] tiles;
}

	
	