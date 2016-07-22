package jnesg.chr;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

import java.io.*;

import jnesg.gui.SwitcherPane;
import jnesg.gui.SwitcherPaneListener;
import jnesg.tile.Tile;
import jnesg.Main;
import jnesg.ext.Exporter;

public class CHRManager extends JPanel
	implements ActionListener, SwitcherPaneListener
{
	public static void main( String [] args )
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.getContentPane().add( new CHRManager() );
		
		f.pack();
		f.setVisible(true);
	}
	
	public CHRManager()
	{
		setupGui();
	}
	
	public int getNumberBlocks()
	{
		return chrSwitcher.getNumberComponents();
	}
	
	public int getCurrentTileIndex()
	{
		CHRBlock cb = (CHRBlock)chrSwitcher.getJComponent();
		
		return cb.getSelected();
	}	
	
	public Tile getTile( int i )
	{
		CHRBlock cb = (CHRBlock)chrSwitcher.getJComponent();
		
		return cb.getTile(i);
	}		
	
	
	private void setupGui()
	{
		setLayout( new BorderLayout() );
	
		CHRBlock cb = new CHRBlock( 16, 32 );
		
		JComponent [] jc = { cb };
		
		chrSwitcher = new SwitcherPane( jc, "CHR Manager" );
		chrSwitcher.addSwitcherPaneListener( this );
		
		add( chrSwitcher, "Center" );
		
		Box b = new Box( BoxLayout.X_AXIS );
		
		clearB = new JButton("Clear");
		newB = new JButton("New");
		
		clearB.addActionListener( this );
		newB.addActionListener( this );
		
		b.add( clearB );
		b.add( newB );
		
		add( b, "South" );
		
		add( new JLabel("shift+drag to select"), "North");
		
	}
	
	public void addTile( Tile t )
	{
		CHRBlock cb = (CHRBlock)chrSwitcher.getJComponent();
		
		cb.addTile( t );
	}
	
	public void setBanksFromRaw( byte[][] chrBanks )
	{	
		chrSwitcher.removeComponents( false );		
		
		for( int i = 0; i < chrBanks.length; ++i )
		{
			CHRBlock cb = new CHRBlock( 16, 32 );
			cb.setFromRaw( chrBanks[i] );
			chrSwitcher.addComponent( cb, false );
		}
		
		chrSwitcher.setView(0);
		
		repaint();	
	}	
	
	public void export( File dir, String root, boolean separate ) throws IOException
	{
		int chrNum = 0;
		
		String fname = root;
		
		if( separate ) fname += chrNum;
		
		fname += Exporter.BINARY_EXT;
		
		byte [][] raw = jnesg.ext.INESExporter.createRawCHRBlocks( this );
		
		FileOutputStream outStream = new FileOutputStream( dir.getAbsolutePath() + File.separator + fname );
		
		for( int i = 0; i < raw.length; ++i )
		{
			outStream.write( raw[i] );
			
			if( separate && i < raw.length-1 )
			{
				++chrNum;
				outStream.close();
				outStream = new FileOutputStream( dir.getAbsolutePath() + File.separator + root + chrNum + Exporter.BINARY_EXT );
			}	
		}
		outStream.close();
	}	

	public void save( OutputStream os ) throws IOException
	{
		byte [][] raw = jnesg.ext.INESExporter.createRawCHRBlocks( this );
		
		System.out.println("raw.length: " + raw.length );
		System.out.println("raw[0].length: " + raw[0].length );
		
		for( int i = 0; i < raw.length; ++i )
		{
			os.write( raw[i] );
		}
	}
		
	public int open( byte [] data, int position, int numCHRs )
	{
		int positionOrig = position;
		
		byte [][] banks = new byte[ numCHRs ][];
		
		for( int i = 0; i < numCHRs; ++i )
		{
			byte [] bank = new byte[ CHR_BYTE_SIZE ];
			// arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
			
			try { System.arraycopy( data, position, bank, 0, CHR_BYTE_SIZE ); }
			catch( ArrayIndexOutOfBoundsException aie )
			{
				System.out.println( "data.length: " + data.length );
				System.out.println( "positionOrig: " + positionOrig );
				System.out.println( "position: " + position );
			}
				
			banks[i] = bank;
			position += CHR_BYTE_SIZE;
		}
		
		setBanksFromRaw( banks );
		return position;
	}
			
			
	public void doImport( byte [] data )
	{
		int numBanks = data.length / ( CHR_BYTE_SIZE );
		
		byte [][] banks = new byte[numBanks][];
		
		for( int i = 0; i < banks.length; ++i )
		{
			byte [] bank = new byte[ CHR_BYTE_SIZE ];
			// arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
			System.arraycopy( data, i * CHR_BYTE_SIZE, bank, 0, CHR_BYTE_SIZE );
			banks[i] = bank;
		}
		
		setBanksFromRaw( banks );
	}
	
	
	/******************** ACTION LISTENER **************/
	
	public void actionPerformed( ActionEvent e )
	{
		Object o = e.getSource();
		
		if( o == newB )
		{
			CHRBlock cb = new CHRBlock( 16, 32 );
			chrSwitcher.addComponent( cb, true );
		}
		
		if( o == clearB )
		{
			((CHRBlock)chrSwitcher.getJComponent()).clear();
		}
	}
	
	public void componentChanged()
	{
		Main.getNameTableManager().repaint();
	}
	
	public CHRBlock [] getCHRBlocks()
	{
		List l = chrSwitcher.getAllComponents();
		
		CHRBlock [] ret = (CHRBlock [])l.toArray( new CHRBlock [] { } );
	
		return ret;
	}
	
	public static final int CHR_BYTE_SIZE = 8 * 1024;

	private SwitcherPane chrSwitcher;
	private JButton clearB, newB;
}