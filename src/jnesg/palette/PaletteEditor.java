package jnesg.palette;

import java.awt.Color;
import java.awt.Container;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.io.*;

import jnesg.gui.SwitcherPane;
import jnesg.ext.Exporter;
import jnesg.Main;

public class PaletteEditor extends JPanel
	implements ActionListener
{
	public static void main( String [] args )
	{		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.getContentPane().add( new PaletteEditor() );
		f.pack();
		f.setVisible(true);
	}
	
	public PaletteEditor()
	{
		curSubPalIndex = 0;
		setupGui();
	}
	
	public int getNumberPalettes()
	{
		return paletteSwitcher.getNumberComponents();
	}
	
	public void updateMirrors( int i )
	{
		if( !mirrorAcrossCB.isSelected() )
		{
			EditablePalette ep = (EditablePalette)paletteSwitcher.getJComponent();
			ep.updateMirrors(i);
		}
		else
		{
			for( int k = 0; k < paletteSwitcher.getNumberComponents(); ++k )
			{
				EditablePalette ep = (EditablePalette)paletteSwitcher.getJComponent(k);
				ep.updateMirrors(i);
			}
		} 
	}
	
	private static final int [] defaultColors = { 1, 5, 56, 16, 1, 5, 56, 16, 1, 5, 56, 16, 1, 5, 56, 16 };
	
	public void setReasonableColors()
	{
		for( int i = 0; i < paletteSwitcher.getNumberComponents(); ++i )
		{
			EditablePalette ep = (EditablePalette)paletteSwitcher.getJComponent(i);
			
			for( int k = 0; k < 16; ++k )
			{
				ep.setPaletteEntryMasterIndex( k, defaultColors[k] );
			}
				
			ep.updateMirrors(0);
		}
	}

	public void export( File dir, String root, boolean separate ) throws IOException
	{
		List palettes = paletteSwitcher.getAllComponents();
		int palNum = 0;
		
		String fname = root;
		
		if( separate ) fname += palNum;
		
		fname += Exporter.BINARY_EXT;
		
		FileOutputStream outStream = new FileOutputStream( dir.getAbsolutePath() + File.separator + fname );
		byte [] out = new byte[16];
					
		for( Iterator iter = palettes.iterator(); iter.hasNext(); )
		{
			EditablePalette ep = (EditablePalette)iter.next();
			
			for( int i = 0; i < out.length; ++i )
				out[i] = (byte)ep.getMasterIndex(i);
				
			outStream.write( out );
			
			if( separate && iter.hasNext() )
			{
				++palNum;
				outStream.close();
				outStream = new FileOutputStream( dir.getAbsolutePath() + File.separator + root + palNum + Exporter.BINARY_EXT );
			}	
		}
		outStream.close();
	}
	
	public void save( OutputStream os ) throws IOException
	{
		List palettes = paletteSwitcher.getAllComponents();

		byte [] out = new byte[16];
		
		for( Iterator iter = palettes.iterator(); iter.hasNext(); )
		{
			EditablePalette ep = (EditablePalette)iter.next();

			for( int i = 0; i < out.length; ++i )
				out[i] = (byte)ep.getMasterIndex(i);
				
			os.write( out );
		}
	}		
	
	public int open( byte [] data, int position, int numPals )
	{
		paletteSwitcher.removeComponents();
		
		for( int i = 0; i < numPals; ++i )
		{
			byte [] pal = new byte[16];
			System.arraycopy( data, position, pal, 0, 16 );
			EditablePalette ep = new EditablePalette( pal );
			paletteSwitcher.addComponent( ep, true );
			position += 16;
		}
		paintAll( getGraphics() );
		
		return position;
	}
	
	public void doImport( byte [] data )
	{
		int numPals = data.length / 16;
	
		if( numPals < 1 )
		{
			JOptionPane.showMessageDialog( Main.getMain(), "Warning: no palettes got imported" );
		}
			
		paletteSwitcher.removeComponents();
		
		for( int i = 0; i < numPals; ++i )
		{
			byte [] pal = new byte[16];
			// arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
			System.arraycopy( data, i * 16, pal, 0, 16 );
			
			EditablePalette ep = new EditablePalette( pal );
			paletteSwitcher.addComponent( ep, true );
		}
		
		paintAll( getGraphics() );
	}
			
		
		
	/****************** COLOR ACCESSES ********************/
	
	public Color getMasterColor( int subIndex )
	{
		return getMasterColor( curSubPalIndex, subIndex );
	}

	public Color getTrueMasterColor( int i )
	{
		return masterPalette.getColor(i);
	}
	
	public Color getMasterColor( int subPalIndex, int subIndex )
	{
		return getMasterColor( paletteSwitcher.getCurrentIndex(), subPalIndex, subIndex );
	}
		
	public Color getMasterColor( int palIndex, int subPalIndex, int subIndex )
	{
		int userPalIndex = subPalIndex * SUBPAL_SIZE + subIndex;
		
		EditablePalette ep = (EditablePalette)paletteSwitcher.getJComponent( palIndex );
		
		int masterIndex = ep.getMasterIndex( userPalIndex );
		
		return masterPalette.getColor( masterIndex );
	}
	
	public byte getCurrentSubIndex()
	{
		EditablePalette ep = (EditablePalette)paletteSwitcher.getJComponent();
		
		return (byte)(ep.getFocusedIndex() % 4);
	}
		 
	
	/********************* GUI INIT **************************/
	
	private void setupGui()
	{
		Box b = new Box( BoxLayout.X_AXIS );
		
		b.add( masterPalette = new MasterPalette() );
		
		Box b2 = new Box( BoxLayout.Y_AXIS );
		
		b2.add( setupToggleButtons() );
		b2.add( setupUserPalettes() );
	
		b.add( b2 );
		
		setBorder( BorderFactory.createTitledBorder( "Palette Editor" ));
		
		add(b);
	}
		
	private Container setupToggleButtons()
	{
		ButtonGroup bg = new ButtonGroup();
		Box b = new Box( BoxLayout.X_AXIS );
		subPalBs = new JToggleButton[4];
		
		for( int i = 0; i < 4; ++i )
		{
			String label = Integer.toBinaryString( i );
			if( label.length() != 2 ) label = "0" + label;
			
			subPalBs[i] = new JToggleButton(label);
			bg.add( subPalBs[i] );
			b.add( subPalBs[i] );
		}
		
		return b;
	}
	
	private Container setupUserPalettes()
	{
		EditablePalette ep = new EditablePalette();
		
		JComponent [] jc = { ep }; 
		
		paletteSwitcher = new SwitcherPane( jc, null );
		
		clearB = new JButton("Clear");
		newB = new JButton("New");
		clearB.addActionListener( this );
		newB.addActionListener( this );
		
		Box ret = new Box( BoxLayout.Y_AXIS );
		Box buttons = new Box( BoxLayout.X_AXIS );
		
		buttons.add( clearB );
		buttons.add( newB );
		
		mirrorAcrossCB = new JCheckBox("Mirror across all palettes");
		
		buttons.add( mirrorAcrossCB );
				
		ret.add( paletteSwitcher );
		ret.add( buttons );
		
		ret.setBorder( BorderFactory.createTitledBorder("User Palettes"));
		
		return ret;
	}
					
	/*************** ACTION LISTENER *********************/
	
	public void actionPerformed( ActionEvent e )
	{
		Object o = e.getSource();
		
		if( o == clearB )
		{
			EditablePalette ep = (EditablePalette)paletteSwitcher.getJComponent();
			ep.clear();
		}
		if( o == newB )
		{
			EditablePalette ep = new EditablePalette();
			paletteSwitcher.addComponent( ep, true );
		}
	}
	
	
	public static final int SUBPAL_SIZE = 4;
	
	private int curSubPalIndex;
	
	private MasterPalette masterPalette;
	private SwitcherPane paletteSwitcher;
	private JToggleButton [] subPalBs;
	private JButton clearB, newB;
	private JCheckBox mirrorAcrossCB;
}
