package jnesg.tile;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import jnesg.Main;
import jnesg.gui.*;

import java.net.URL;

public class TileEditor extends JPanel
	implements ActionListener, ToolBarListener
{	
	public TileEditor()
	{			
		
		zoom = INITIAL_ZOOM_LEVEL;
		editor = new TileEditArea( INITIAL_ZOOM_LEVEL, false, true, 1, 1 );

		preview = new TilePreview( 2, editor );
		preview.setData( editor.getData() );

		editor.setPreview( preview );
		
		_doLayout();
	}

	private Container setupToolbarArea()
	{
		ClassLoader cl = getClass().getClassLoader();
		
		URL u1 = cl.getResource("icons/paintbrush.jpg" );
		URL u2 = cl.getResource("icons/paintbucket.jpg" );
		URL u3 = cl.getResource("icons/zoomIn24.gif" );
		URL u4 = cl.getResource("icons/zoomOut24.gif" );

		ImageIcon [] icons = {new ImageIcon( u1 ),
								new ImageIcon( u2 ),
								new ImageIcon( u3 ),
								new ImageIcon( u4 ) };
								
		String [] toolTips = { "Paint", "Fill", "Zoom In", "Zoom Out" };
		
		ToolBar tools = new ToolBar( icons, toolTips, ToolBar.HORIZONTAL, true );
		tools.addToolBarListener( this );
		
		
		JPanel b = new JPanel();
		
		b.add( tools );
		
		zoomLabel = new JLabel( "Zoom: " + zoom );
		
		b.add( zoomLabel );
	
		tileWidthTF = new JTextField(3);
		b.add( tileWidthTF );
		b.add( new JLabel("X") );
		
		tileHeightTF = new JTextField(3);
		b.add( tileHeightTF );
		
		b.add( new JLabel("tiles") );
		
		newB = new JButton("New");
		newB.addActionListener( this );
		
		b.add( newB );
		
		clearB = new JButton( "Clear" );
		
		b.add( clearB );
		clearB.addActionListener( this );
		
		gridToggle = new JCheckBox("Grid");
		gridToggle.addActionListener( this );
		gridToggle.setSelected( true );
		editor.setGridVisible( true );
		
		b.add( gridToggle );
		
		return b;		
	}						
	
	public Dimension getMaximumSize() { return MAXSIZE; }
	public Dimension getPreferredSize() { return MAXSIZE; }
	
	private void _doLayout()
	{
		setLayout( new BorderLayout() );
		
		add( setupToolbarArea(), "North" );
		
		editorPanel = new JPanel();
		editorPanel.add( editor );
		
		JScrollPane scroll = new JScrollPane( editorPanel );
		
		add( scroll, "Center" );
		
		previewPanel = new JPanel();
		previewPanel.add( preview );
		previewPanel.setBorder( BorderFactory.createEtchedBorder() );
		
		JPanel pp = new JPanel();
		pp.add( previewPanel );
		
		add( pp, "East" );
				
		setBorder( BorderFactory.createTitledBorder( "Tile Editor" ));
	}

	public void toolBarButtonPressed( ToolBarEvent tbe )
	{
		switch( tbe.getIndex() )
		{
			case PAINT:
				editor.setEditMode( PAINT );
				break;
			case FILL:
				editor.setEditMode( FILL );
				break;
			case ZOOMIN:
				doZoomIn();
				break;
			case ZOOMOUT:
				doZoomOut();
				break;
		}		
	}
	
	public void actionPerformed( ActionEvent e )
	{
		Object o = e.getSource();
		
		if( o == newB )
		{
			String w = tileWidthTF.getText();
			String h = tileHeightTF.getText();
			int wid, hei;
			try
			{
				wid = Integer.parseInt(w);
				hei = Integer.parseInt(h);
			}
			catch( NumberFormatException nfe )
			{
				JOptionPane.showMessageDialog( Main.getMain(), "Non-number(s) entered for tile edit area size!");
				return;
			}
			
			if( wid < 1 || hei < 1 )
			{
				JOptionPane.showMessageDialog( Main.getMain(), "Width or height must be at least 1");
				return;
			}
			
			if( wid > MAXTILEEDITSIZE || hei > MAXTILEEDITSIZE )
			{
				JOptionPane.showMessageDialog( Main.getMain(), "Tile edit area too big!");
			}
			
			if( !editor.empty() )
			{
				int choice = JOptionPane.showConfirmDialog( Main.getMain(), "Okay to delete contents of tile edit area?", "Delete?", 
									JOptionPane.YES_NO_OPTION );
									
				if( choice == JOptionPane.NO_OPTION )
					return;					
			}
			
			editor.setNew( hei, wid, INITIAL_ZOOM_LEVEL );
			resetEditor();
		}	
		
		if( o == clearB )
		{
			editor.reset();
			refresh();
		}
		
		if( o == gridToggle )
			editor.setGridVisible( gridToggle.isSelected() );
	}
	
	private void doZoomIn()
	{
		if( zoom == MAX_PREV_ZOOM )
			return;
			
		++zoom;
		
		editor.setZoom( zoom );
		
		zoomLabel.setText( "Zoom: " + zoom );
		
		resetEditor();
	}
	
	private void doZoomOut()
	{
		if( zoom == 1 )
			return;
			
		--zoom;
	
		editor.setZoom( zoom );
		
		zoomLabel.setText( "Zoom: " + zoom );
		
		resetEditor();
	}
			
	public void resetEditor()
	{
		editorPanel.remove( editor );
		editorPanel.add( editor );

		previewPanel.remove( preview );
		previewPanel.add( preview );
			
		refresh();
	}
	
	public void refresh()
	{
		Graphics g = getGraphics();
		paintAll(g);	
	}
		
		
	public static final int INITIAL_ZOOM_LEVEL = 20;	
	public static final int MAX_PREV_ZOOM = 200;	
	public static final int MAXTILEEDITSIZE = 10;
	
	public static final int PAINT = 0, FILL = 1, ZOOMIN = 2, ZOOMOUT = 3;
	
	private static final Dimension MAXSIZE = new Dimension( 500, 300 );	
		
	private TileEditArea editor;
	private TileEditArea preview;
	
	private int zoom;
	private JButton newB, clearB;
	private JTextField tileWidthTF, tileHeightTF;
	private JLabel zoomLabel;
	private JPanel editorPanel, previewPanel;
	private JCheckBox gridToggle;
}
