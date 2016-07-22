package jnesg.palette;

import java.awt.dnd.*;
import java.awt.*;
import java.awt.datatransfer.*;

public class PaletteTransporter implements Transferable
{
	public PaletteTransporter( PaletteEntry pe )
	{
		pentry = pe;
	}
	
	public PaletteEntry getPaletteEntry() { return pentry; }
	
	public Object getTransferData( DataFlavor flavor )
		throws UnsupportedFlavorException
	{
		if( PaletteEntry.class != flavor.getRepresentationClass() )
			throw new UnsupportedFlavorException(flavor);
			
		return pentry;
	}
	
	public DataFlavor [] getTransferDataFlavors() { return flavorArray; }
	
	public boolean isDataFlavorSupported( DataFlavor flavor )
	{
		return PaletteEntry.class == flavor.getRepresentationClass();
	}
	
	private static final DataFlavor [] flavorArray = { new DataFlavor( PaletteEntry.class, "PaletteEntry" ) };
	private PaletteEntry pentry;
}

	
	