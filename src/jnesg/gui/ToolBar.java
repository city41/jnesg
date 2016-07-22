package jnesg.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.List;

public class ToolBar extends JPanel
	implements ActionListener
{
	public ToolBar( ImageIcon [] i, String [] tt, int or, boolean tog )
	{
		icons = i;
		toolTips = tt;
		orientation = or;
		toggle = tog;
		listeners = new LinkedList();
		
		init();
	}
	
	public void addToolBarListener( ToolBarListener tbl )
	{
		listeners.add( tbl );
	}
	
	public void removeToolBarListener( ToolBarListener tbl )
	{
		listeners.remove( tbl );
	}
	
	
	public Dimension getPreferredSize() { return toolbarDim; }
	public Dimension getMinimumSize()	{ return toolbarDim; }
	public Dimension getMaximumSize()	{ return toolbarDim; }	
	
	public static final int HORIZONTAL = 1, VERTICAL = 2;
		
	public void actionPerformed( ActionEvent e )
	{
		int i = 0;
		
		for( ; i < buttons.length; ++i )
		{
			if( buttons[i] == e.getSource() )
				break;
		}
		
		ToolBarEvent tbe = new ToolBarEvent( i, toolTips[i], buttons[i] );
		
		for( Iterator iter = listeners.iterator(); iter.hasNext(); )
		{
			ToolBarListener tbl = (ToolBarListener)iter.next();
			tbl.toolBarButtonPressed( tbe );
		}
	}
	
	private void init()
	{
		buttonInsets = new Insets( 3, 3, 3, 3 );		
		buttonDim = new Dimension( icons[0].getIconWidth() + buttonInsets.left + buttonInsets.right,
						icons[0].getIconHeight() + buttonInsets.top + buttonInsets.bottom );		
		
		buttons = new AbstractButton[ icons.length ];
	
		
		if( orientation == HORIZONTAL )
		{
			setLayout( new GridLayout( 1, icons.length ) );
			toolbarDim = new Dimension( buttonDim.width * icons.length, buttonDim.height );
		}
		else
		{
			setLayout( new GridLayout( icons.length, 1 ) );
			toolbarDim = new Dimension( buttonDim.width, buttonDim.height * icons.length );
		}
		
		ButtonGroup bg = new ButtonGroup();
		
		for( int i = 0; i < icons.length; ++i )
		{
			if( toggle ) buttons[i] = new JToggleButton( icons[i] );
			else buttons[i] = new JButton( icons[i] );
		
			
			buttons[i].addActionListener( this );
			if( toggle ) bg.add( buttons[i] );
			
			if( toolTips != null )
				buttons[i].setToolTipText( toolTips[i] );
				
			add( buttons[i] );
			
			buttons[i].setPreferredSize( buttonDim );
			
		}
		buttons[0].setSelected(true);
	}	
	
	private boolean toggle;
	private AbstractButton [] buttons;
	private ImageIcon [] icons;
	private String [] toolTips;
	private int orientation;
	private List listeners;
	private Dimension buttonDim, toolbarDim;
	private Insets buttonInsets;
}
	
		