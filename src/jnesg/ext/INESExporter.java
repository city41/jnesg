package jnesg.ext;

import java.io.*;
import javax.swing.JOptionPane;

import jnesg.Main;
import jnesg.chr.*;
import jnesg.tile.*;

public class INESExporter
{
	public INESExporter( File r, CHRManager c ) throws IOException
	{
		chrm = c;
		
		int choice = JOptionPane.showConfirmDialog( Main.getMain(), "Warning: JNesG may not properly export to ROMs which have\n"
			+ " trainers. JNesG always exports into a copy of the file,\n it won't be overwritten. Proceed?", "Trainer", 
								JOptionPane.YES_NO_OPTION );
								
		if( choice == JOptionPane.NO_OPTION )
			throw new IOException("Exportation aborted");
		
		
		romFile = r;
		
		verify();
		export();	
	}
	
	public INESExporter( CHRManager c )
	{
		chrm = c;
	}
	
	private void export() throws IOException
	{
		createRawCHRBlocks();
		
		RandomAccessFile raf = createRAFCopy();
		
		int bytesTillCHR = INESHEADER_SIZE + ((16 * 1024) * numPRGBlocks);
		
		raf.seek( bytesTillCHR );
		
		// write each block, one at a time
		for( int i = 0; i < rawCHRBlocks.length; ++i )
		{
			raf.write( rawCHRBlocks[i] );
		}

		raf.close();
		
		JOptionPane.showMessageDialog( Main.getMain(), "ROM successfully exported" );
	}	
				
	private RandomAccessFile createRAFCopy() throws IOException
	{
		exportedRomFile = createExportedRomFile( romFile );
		
		FileInputStream fis = new FileInputStream( romFile );
		FileOutputStream fos = new FileOutputStream( exportedRomFile );
		
		byte [] buff = new byte [1024];
		int bytesRead = 0;
		
		while(( bytesRead = fis.read( buff )) != -1 )
			fos.write( buff, 0, bytesRead );
			
		fos.close();
		fis.close();
		
		return new RandomAccessFile( exportedRomFile, "rw" );
	}
	
	private void verify() throws IOException
	{
		FileInputStream fis = new FileInputStream( romFile );
		
		byte [] b = new byte[6];
		
		int bytesRead = fis.read( b );
		
		if( bytesRead != 6 )
			throw new IOException("Chosen file to export data into is not an iNES ROM");
		
		fis.close();
		
		if( b[0] != 'N'
				|| b[1] != 'E'
				|| b[2] != 'S'
				|| b[3] != 0x1A )
			throw new IOException("Chosen file to export data into is not an iNES ROM");
			
		numPRGBlocks = b[4];	
		numCHRBlocks = b[5];
		
		if( numCHRBlocks < chrm.getNumberBlocks() )		
		{
			int choice = JOptionPane.showConfirmDialog( Main.getMain(), "Warning: You are attempting to export more CHR Blocks than this"
										+ " ROM has room for, ok to export the amount that will fit?", "Truncate?", 
								JOptionPane.YES_NO_OPTION );
								
			if( choice == JOptionPane.NO_OPTION )
				throw new IOException("Exportation aborted");
		}
		
		if( numCHRBlocks > chrm.getNumberBlocks() )
		{
			int choice = JOptionPane.showConfirmDialog( Main.getMain(), "Warning: You are attempting to export fewer CHR Blocks than this"
										+ " ROM has room for, ok to export them all and leave the last "
										+ (numCHRBlocks - chrm.getNumberBlocks()) + " blocks untouched?", "Short?", 
								JOptionPane.YES_NO_OPTION );
								
			if( choice == JOptionPane.NO_OPTION )
				throw new IOException("Exportation aborted");			
		}
	}
	
	public static byte [][] createRawCHRBlocks( CHRManager cm )
	{
		INESExporter ine = new INESExporter( cm );
		ine.numCHRBlocks = cm.getNumberBlocks();
		ine.createRawCHRBlocks();
		
		return ine.rawCHRBlocks;
	}
		
	private void createRawCHRBlocks()
	{
		rawCHRBlocks = new byte[ numCHRBlocks ][ CHRManager.CHR_BYTE_SIZE ];

		CHRBlock [] chrs = chrm.getCHRBlocks();
		
		for( int i = 0; i < rawCHRBlocks.length; ++i )
		{
			Tile [][] tiles = chrs[i].getTiles();
			
			tilesToRaw( tiles, rawCHRBlocks[i] );
		}
	
		chrSize = CHRManager.CHR_BYTE_SIZE * numCHRBlocks;
	}
	
	private void tilesToRaw( Tile [][] tiles, byte [] raw )
	{
		// temp check
		
		if( raw.length != (tiles.length * tiles[0].length * 16) )
		{
			System.err.println("mismatch in tilesToRaw! raw.length: " + raw.length +
				" tiles.length: " + tiles.length + " tiles[0].length: " + tiles[0].length );
			System.exit(1);
		}
		
		int r = 0, c = 0;
		for( int i = 0; i < raw.length-16; i += 8 )
		{
			Tile t = tiles[r][c];
			++c;
			if( c == tiles[0].length )
			{
				c = 0;
				++r;
			}
			
			byte [][] td = t.getData();
			
			for( int k = 0; k < 8; ++k )
				raw[i+k] = (byte)(arrayToByte( td[k], 0x01 ));
				
			i += 8;
				
			for( int k = 0; k < 8; ++k )
				raw[i+k] = arrayToByte( td[k], 0x02 );
				//raw[i+k] = (byte)(~arrayToByte( td[k], 0x02 ));
		}
	}
	
	private byte arrayToByte( byte [] b, int and )
	{
		if( b.length != 8 )
		{
			System.err.println(" arrayToByte: b.length! " + b.length );
			System.exit(1);
		}
	
		String s = "";
		for( int i = 0; i < Tile.SIZE; ++i )
		{
			if( b[i] > 3 || b[i] < 0 )
			{
				System.err.println("ERROR, byte in tile not in range!: " + b[i] );
				System.exit(1);
			}
			
			int v = (b[i] & and) == 0 ? 0 : 1;
			s = s + v;
		}
		
		int a = Integer.parseInt( s, 2 );
				
		return (byte)a;
	}
		
		
	private static File createExportedRomFile( File orig )
	{
		// this function assumes orig is a legitimate iNES rom
		
		String abspath = orig.getAbsolutePath();
		String newFile = null;
		
		int extInd = abspath.indexOf(".nes");
		
		if( extInd < 0 )
			newFile = abspath + EXTENSION;
		else
			newFile = abspath.substring(0, extInd) + EXTENSION;
		
		return new File( newFile );
	}
				
	public static final int INESHEADER_SIZE = 16;
	public static final String EXTENSION = "_exported.nes";
	
	private File romFile, exportedRomFile;
	private CHRManager chrm;
	private int numPRGBlocks, numCHRBlocks;
	private int chrSize; 	// size of the all the chr blocks in bytes 
	private byte [][] rawCHRBlocks;
}