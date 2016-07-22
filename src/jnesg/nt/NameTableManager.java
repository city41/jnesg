package jnesg.nt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.*;

import java.util.List;
import java.util.Iterator;

import jnesg.gui.*;
import jnesg.tile.*;
import jnesg.Main;
import jnesg.ext.Exporter;

public class NameTableManager extends JPanel
	implements ActionListener, ToolBarListener
{
	public NameTableManager()
	{
		setupGui();
	}
	
	public boolean isHigh() { return highRB.isSelected(); }
	
	public int getNumberNameTables()
	{
		return ntSwitcher.getNumberComponents();
	}
		
	public void export( File dir, String root, boolean separate ) throws IOException
	{
		List nts = ntSwitcher.getAllComponents();
		int ntNum = 0;
		
		String fname = root;
		
		if( separate ) fname += ntNum;
		
		fname += Exporter.BINARY_EXT;
		
		FileOutputStream outStream = new FileOutputStream( dir.getAbsolutePath() + File.separator + fname );
							
		for( Iterator iter = nts.iterator(); iter.hasNext(); )
		{
			NameTable nt = (NameTable)iter.next();
			
			byte [][] data = nt.getData();			
			byte [] lined = new byte[ data.length * data[0].length ];			
			
			for( int i = 0; i < data.length; ++i )
			{
				System.arraycopy( data[i], 0, lined, i * data[0].length, data[0].length );
			}
			
			outStream.write( lined );
			
			if( separate && iter.hasNext() )
			{
				++ntNum;
				outStream.close();
				outStream = new FileOutputStream( dir.getAbsolutePath() + File.separator + root + ntNum + Exporter.BINARY_EXT );
			}	
		}
		outStream.close();
	}
	
	
	public void save( OutputStream os ) throws IOException
	{
		List nts = ntSwitcher.getAllComponents();
		
		for( Iterator iter = nts.iterator(); iter.hasNext(); )
		{
			NameTable nt = (NameTable)iter.next();	
			byte [][] data = nt.getData();
			byte [] lined = new byte[ data.length * data[0].length ];
					
			for( int i = 0; i < data.length; ++i )
			{
				System.arraycopy( data[i], 0, lined, i * data[0].length, data[0].length );
			}
			
			os.write( lined );
		}
	}
		
	public int open( byte [] data, int position, int numNTs )
	{
		ntSwitcher.removeComponents();
		
		for( int i = 0; i < numNTs; ++i )
		{
			byte [][] nt = new byte[30][32];

			for( int r = 0; r < nt.length; ++r )
			{
				for( int c = 0; c < nt[r].length; ++c )
				{
					nt[r][c] = data[ position ];
					++position;
				}
			}
			NameTable ntb = new NameTable( 2, true, nt );
			ntSwitcher.addComponent( ntb, true );
		}
		
		paintAll( getGraphics() );
		return position;
	}		
	
	public void doImport( byte [] data )
	{
		int numNTs = data.length / (32 * 30);
	
		if( numNTs < 1 )
		{
			JOptionPane.showMessageDialog( Main.getMain(), "Warning: no name tables got imported" );
		}
			
		ntSwitcher.removeComponents();
		
		for( int i = 0; i < numNTs; ++i )
		{
			byte [][] nt = new byte[30][32];

			int ii = 0;
			for( int r = 0; r < nt.length; ++r )
			{
				for( int c = 0; c < nt[r].length; ++c )
				{
					nt[r][c] = data[ii];
					++ii;
				}
			}
			NameTable ntb = new NameTable( 2, true, nt );
			ntSwitcher.addComponent( ntb, true );
		}
		
		paintAll( getGraphics() );
	}
	
	private void setupGui()
	{
		setLayout( new BorderLayout() );
		
		NameTable nt = new NameTable( 2, true, 30, 32);	
		
		JComponent [] jc = { nt };
		
		ntSwitcher = new SwitcherPane( jc, "Name Tables" );
		
		clearB = new JButton("Clear");
		clearB.addActionListener( this );
		newB = new JButton("New");
		Box b = new Box( BoxLayout.X_AXIS );
		b.add( clearB );
		b.add( newB );
		
		add( ntSwitcher, "Center" );
		add( b, "South" );
		add( setupToolbarArea(), "North" );
		
		setBorder( BorderFactory.createEmptyBorder( 7, 7, 7, 7 ) );
	}
	
	private Container setupToolbarArea()
	{
		File rootDir = Main.getRootDir();
		
		ImageIcon [] icons = { new ImageIcon( getClass().getClassLoader().getResource("icons/paintbrush.jpg") ),
								new ImageIcon( getClass().getClassLoader().getResource("icons/paintbucket.jpg") )};
								
		String [] toolTips = { "Paint", "Fill" };
		
		ToolBar tools = new ToolBar( icons, toolTips, ToolBar.HORIZONTAL, true );
		tools.addToolBarListener( this );
		
		
		JPanel b = new JPanel();
		
		b.add( tools );
		clearB.addActionListener( this );
		
		gridToggle = new JCheckBox("Grid");
		gridToggle.addActionListener( this );
		gridToggle.setSelected( true );
		
		b.add( gridToggle );
		
		ButtonGroup bg = new ButtonGroup();
		highRB = new JRadioButton( "Hi" );
		lowRB = new JRadioButton( "Lo" );
		bg.add( highRB );
		bg.add( lowRB );
		highRB.addActionListener( this );
		lowRB.addActionListener( this );
		
		JPanel hilop = new JPanel();
		hilop.setBorder( BorderFactory.createTitledBorder( "CHR Source" ) );
		
		hilop.add( highRB );
		hilop.add( lowRB );
		b.add( hilop );
		highRB.setSelected( true );
		
		return b;		
	}				
	
	/**************ACTION LISTENER*************/
	
	public void actionPerformed( ActionEvent e )
	{
		Object o = e.getSource();
		
		if( o == newB )
		{
			ntSwitcher.addComponent( new NameTable( 2, true, 30, 32), true );
		}
		
		if( o == highRB )
		{
			((NameTable)ntSwitcher.getJComponent()).setHigh();
		}
		
		if( o == lowRB )	
		{
			((NameTable)ntSwitcher.getJComponent()).setLow();
		}
		if( o == clearB )
		{
			((NameTable)ntSwitcher.getJComponent()).clear();
		}
		
		if( o == gridToggle )
		{
			((NameTable)ntSwitcher.getJComponent()).setGridVisible( gridToggle.isSelected() );
		}		
	}
	
	public void toolBarButtonPressed( ToolBarEvent tbe )
	{
		switch( tbe.getIndex() )
		{
			case TileEditor.PAINT:
				((NameTable)ntSwitcher.getJComponent()).setEditMode( TileEditor.PAINT );
				break;
			case TileEditor.FILL:
				((NameTable)ntSwitcher.getJComponent()).setEditMode( TileEditor.FILL );
				break;
		}		
	}
		
	private SwitcherPane ntSwitcher;
	private ToolBar tools;
	private JButton clearB, newB;
	private JCheckBox gridToggle;
	private JRadioButton highRB, lowRB;
}
	