package jnesg.tile;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

import jnesg.Main;
import jnesg.palette.PaletteEditor;

public class Tile
{
	static
	{
		byte [][] data = new byte [8][8];
		
		for( int r = 0; r < 8; ++r )
		{
			for( int c = 0; c < 8; ++c )
			{
				data[r][c] = 0;
			}
		}
		
		EMPTY = new Tile( data, false );
	}
	
	public Tile( byte[][] d, int sp, boolean copy )
	{
		if( copy )
		{
			data = new byte[ SIZE ][ SIZE ];
			for( int r = 0; r < SIZE; ++r )
			{
				for( int c = 0; c < SIZE; ++c )
				{
					data[r][c] = d[r][c];
				}
			}	
		}
		
		data = d;
		subPalette = sp;
		createImage();
	}
	
	public Tile( byte [][] d, boolean copy )
	{
		this( d, 0, copy );
	}
			
	public Image getImage() { return image; }
	
	public void createImage()
	{
		BufferedImage tmp = new BufferedImage( SIZE * ZOOM, SIZE * ZOOM, BufferedImage.TYPE_INT_RGB );
		
		paint( tmp.getGraphics(), 0, 0 );
		
		image = (Image)tmp;
	}
		
		
	
	public void paint( Graphics g, int xbase, int ybase, int zoom, int subpal )
	{
		for( int r = 0; r < data.length; ++r )
		{
			for( int c = 0; c < data[r].length; ++c )
			{
				g.setColor( Main.getPaletteEditor().getMasterColor( subpal, data[r][c] ));
				g.fillRect( c*zoom + xbase, r*zoom + ybase, zoom, zoom );
			}
		}
	} 
	
	public void paint( Graphics g, int xbase, int ybase, int zoom )
	{
		paint( g, xbase, ybase, zoom, subPalette );
	}
	
	public void paint( Graphics g, int xbase, int ybase )
	{
		paint( g, xbase, ybase, ZOOM );
	}			
				
	public byte [][] getData() { return data; }
		
	public void draw( Graphics g, int r, int c )
	{
		for( int rr = 0; rr < SIZE; ++rr )
		{
			int y = rr * ZOOM  + r;
			for( int cc = 0; cc < SIZE; ++cc )
			{
				g.setColor( Main.getPaletteEditor().getMasterColor( subPalette, data[rr][cc] ) );
				g.fillRect( cc * ZOOM + c, y, SIZE * ZOOM, SIZE * ZOOM );
			}
		}
	}

	public static Tile fromRaw( byte[] b, int start, int len )
	{
		byte [][] tile = new byte[8][8];
		
		// get upper tile
		int i;
		for( i = start; i < start+8; ++i )
		{
			byte [] row = getRowFromRaw( b[i] );
			tile[i - start] = row;
		}
		
		for( ; i < start+len; ++i )
		{
			byte [] row = getRowFromRaw( b[i] );
			orRow( tile[i-start-8], row );
		}
		
		return new Tile( tile, false );
	}
		
	public static Tile [][] createArray( byte [][] data )
	{
		Tile [][] ret = new Tile [ data.length / SIZE ][ data[0].length / SIZE ];
		
		for( int r = 0; r < ret.length; ++r )
		{
			for( int c = 0; c < ret[r].length; ++c )
			{
				byte [][] d = new byte[SIZE][SIZE];
				
				int rbase = r * SIZE;
				int cbase = c * SIZE;
				
				for( int dr = 0; dr < d.length; ++dr )
				{
					for( int dc = 0; dc < d[dr].length; ++dc )
					{
						d[dr][dc] = data[rbase + dr][cbase + dc];
					}
				}
				ret[r][c] = new Tile( d, false );
			}
		}
		return ret;
	}	
				
	
	private static byte [] getRowFromRaw( byte b )
	{
		byte [] row = new byte[8];
		
		for( int i = 7; i >= 0; --i )
		{
			row[i] = (byte)(0x01 & b);
			b = (byte)(b >> 1);
		}
		return row;
	}
	
	private static void orRow( byte [] dest, byte [] src )
	{
		for( int i = 0; i < dest.length; ++i )
		{
			dest[i] = (byte)(dest[i] + 2*src[i]);
			
			if( dest[i] > 3 || dest[i] < 0 )
			{
				throw new RuntimeException("Tile value out of range!! val: " + dest[i]);
			}
		}
	}	
	
	
		
	public static final int SIZE = 8;
	public static final int ZOOM = 2;
	public static final Tile EMPTY;
	
	private int subPalette;
	private byte [][] data;
	private Image image;
}
			