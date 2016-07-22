package jnesg.ext;

import java.io.*;

public class INESImporter
{
	public INESImporter( File r ) throws IOException
	{
		romFile = r;
		
		if( !verify() )
			throw new IOException( r.getName() + " does not appear to be an NES rom in the iNES format" );
		
		extractData();
	}
	
	public byte[][] getCHRBanks()
	{
		return CHRBanks;
	}
	
	private boolean verify() throws IOException
	{
		FileInputStream fis = new FileInputStream( romFile );
		
		byte [] b = new byte[4];
		
		int bytesRead = fis.read( b );
		
		if( bytesRead != 4 ) return false;
		
		fis.close();
		
		return (b[0] == 'N'
				&& b[1] == 'E'
				&& b[2] == 'S'
				&& b[3] == 0x1A);
	}
	
	private void extractData() throws IOException
	{
		FileInputStream fis = new FileInputStream( romFile );
		fis.skip(4); // skip "NES" magic string
		
		numROMBanks = fis.read();
		numVROMBanks = fis.read();
		int mask = fis.read();
		
		if( (mask & TRAINERMASK) != 0)
			hasTrainer = true;
		else 
			hasTrainer = false;
		
		fis.skip(9); // skip rest of header
		
		if( hasTrainer ) fis.skip(512);
		
		fis.skip( 16 * 1024 * numROMBanks ); // skip the 16k ROM banks
		
		CHRBanks = new byte[ numVROMBanks ][ 8 * 1024 ];
		
		int bytesRead = 0;
		
		for( int i = 0; i < numVROMBanks; ++i )
		{
			bytesRead = fis.read( CHRBanks[i] );
			
			if( bytesRead != (8 * 1024) )
				throw new IOException("Read error on CHR Banks");
		}		
	}
		
	public static final int TRAINERMASK = 0x04;
	
	private int numROMBanks, numVROMBanks;
	private boolean hasTrainer;
	private byte[][] CHRBanks;
	private File romFile;
}