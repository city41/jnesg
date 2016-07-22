package jnesg.palette;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class MasterPalette extends Palette
{
	public MasterPalette()
	{
		setLayout( new GridLayout( TILESH, TILESW ));
		
		for( int i = 0; i < colors.length; ++i )
		{
			initDnD( colors[i] );
			add( colors[i] );
			colors[i].setToolTipText( createToolTip(i) );
		}
		
		TitledBorder tb = BorderFactory.createTitledBorder( "Master Palette" );
		setBorder( tb );			
		
		Insets ins = tb.getBorderInsets( this );
		
		dim = new Dimension( TILESW * colors[0].getPreferredSize().width
				+ ins.left + ins.right,
			TILESH * colors[0].getPreferredSize().height + ins.top + ins.bottom );		
			 
	}

	public Color getColor( int i )
	{
		if( i >= 0 && i < colors.length )
		{
			return colors[i].getColor();
		}
		return null;
	}
	
	public Dimension getPreferredSize() { return dim; }
	public Dimension getMinimumSize()	{ return dim; }
	public Dimension getMaximumSize()	{ return dim; }	
				
	public static final int TILESW = 16, TILESH = 4;
	
	private static String createToolTip( int i )
	{
		String ret = "$";
		if( i < 16 )
			ret += "0";
			
		ret += Integer.toHexString(i).toUpperCase();
		
		return ret;
	}
		
	
	// the master palette in the NES, approximated in RGB values
	private final PaletteEntry [] colors =
	{
	   new PaletteEntry( null, 0x80,0x80,0x80, 0 ), new PaletteEntry( null, 0x00,0x00,0xBB, 1 ),
	   new PaletteEntry( null, 0x37,0x00,0xBF, 2 ), new PaletteEntry( null, 0x84,0x00,0xA6, 3 ),
	   new PaletteEntry( null, 0xBB,0x00,0x6A, 4 ), new PaletteEntry( null, 0xB7,0x00,0x1E, 5 ),
	   new PaletteEntry( null, 0xB3,0x00,0x00, 6 ), new PaletteEntry( null, 0x91,0x26,0x00, 7 ),
	   new PaletteEntry( null, 0x7B,0x2B,0x00, 8 ), new PaletteEntry( null, 0x00,0x3E,0x00, 9 ),
	   new PaletteEntry( null, 0x00,0x48,0x0D, 10 ), new PaletteEntry( null, 0x00,0x3C,0x22,11 ),
	   new PaletteEntry( null, 0x00,0x2F,0x66, 12 ), new PaletteEntry( null, 0x00,0x00,0x00,13 ),
	   new PaletteEntry( null, 0x05,0x05,0x05, 14 ), new PaletteEntry( null, 0x05,0x05,0x05,15 ),

	   new PaletteEntry( null, 0xC8,0xC8,0xC8, 16 ), new PaletteEntry( null, 0x00,0x59,0xFF,17 ),
	   new PaletteEntry( null, 0x44,0x3C,0xFF, 18 ), new PaletteEntry( null, 0xB7,0x33,0xCC,19 ),
	   new PaletteEntry( null, 0xFF,0x33,0xAA, 20 ), new PaletteEntry( null, 0xFF,0x37,0x5E,21 ),
	   new PaletteEntry( null, 0xFF,0x37,0x1A, 22 ), new PaletteEntry( null, 0xD5,0x4B,0x00,23 ),
	   new PaletteEntry( null, 0xC4,0x62,0x00, 24 ), new PaletteEntry( null, 0x3C,0x7B,0x00,25 ),
	   new PaletteEntry( null, 0x1E,0x84,0x15, 26 ), new PaletteEntry( null, 0x00,0x95,0x66,27 ),
	   new PaletteEntry( null, 0x00,0x84,0xC4, 28 ), new PaletteEntry( null, 0x11,0x11,0x11,29 ),
	   new PaletteEntry( null, 0x09,0x09,0x09, 30 ), new PaletteEntry( null, 0x09,0x09,0x09,31 ),

	   new PaletteEntry( null, 0xFF,0xFF,0xFF, 32 ), new PaletteEntry( null, 0x00,0x95,0xFF,33 ),
	   new PaletteEntry( null, 0x6F,0x84,0xFF, 34 ), new PaletteEntry( null, 0xD5,0x6F,0xFF,35 ),
	   new PaletteEntry( null, 0xFF,0x77,0xCC, 36 ), new PaletteEntry( null, 0xFF,0x6F,0x99,37 ),
	   new PaletteEntry( null, 0xFF,0x7B,0x59, 38 ), new PaletteEntry( null, 0xFF,0x91,0x5F,39 ),
	   new PaletteEntry( null, 0xFF,0xA2,0x33,40 ), new PaletteEntry( null, 0xA6,0xBF,0x00,41 ),
	   new PaletteEntry( null, 0x51,0xD9,0x6A,42 ), new PaletteEntry( null, 0x4D,0xD5,0xAE,43 ),
	   new PaletteEntry( null, 0x00,0xD9,0xFF,44 ), new PaletteEntry( null, 0x66,0x66,0x66,45 ),
	   new PaletteEntry( null, 0x0D,0x0D,0x0D,46 ), new PaletteEntry( null, 0x0D,0x0D,0x0D,47 ),

	   new PaletteEntry( null, 0xFF,0xFF,0xFF,48 ), new PaletteEntry( null, 0x84,0xBF,0xFF,49 ),
	   new PaletteEntry( null, 0xBB,0xBB,0xFF,50 ), new PaletteEntry( null, 0xD0,0xBB,0xFF,51 ),
	   new PaletteEntry( null, 0xFF,0xBF,0xEA,52 ), new PaletteEntry( null, 0xFF,0xBF,0xCC,53 ),
	   new PaletteEntry( null, 0xFF,0xC4,0xB7,54 ), new PaletteEntry( null, 0xFF,0xCC,0xAE,55 ),
	   new PaletteEntry( null, 0xFF,0xD9,0xA2,56 ), new PaletteEntry( null, 0xCC,0xE1,0x99,57 ),
	   new PaletteEntry( null, 0xAE,0xEE,0xB7,58 ), new PaletteEntry( null, 0xAA,0xF7,0xEE,59 ),
	   new PaletteEntry( null, 0xB3,0xEE,0xFF,60 ), new PaletteEntry( null, 0xDD,0xDD,0xDD,61 ),
	   new PaletteEntry( null, 0x11,0x11,0x11,62 ), new PaletteEntry( null, 0x11,0x11,0x11,63 )
	};
	
	private Dimension dim;	
}