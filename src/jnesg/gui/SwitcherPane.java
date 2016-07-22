package jnesg.gui;

import java.awt.BorderLayout;
import java.awt.Graphics;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.BorderFactory;

/**
	A simple component that accepts JComponents
	and allows the user to select which one is 
	currently being viewed by two JButtons.
	@author Matt Greer <a href="matthew-greer@uiowa.edu">matthew-greer@uiowa.edu</a>
	@version 0.3a
*/

public class SwitcherPane extends JPanel
	implements ActionListener
{
	/**
		A simple test main method
	*/
	public static void main( String [] args )
	{
		JButton b = new JButton("1");
		JButton c = new JButton("2");
		JLabel d = new JLabel("hello");
		
		JComponent [] jcs = { b, c, d };
		
		SwitcherPane sp = new SwitcherPane( jcs, "Testing");
		
		JFrame f = new JFrame();
		f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		f.getContentPane().add( sp );
		f.pack();
		f.setVisible(true);
	}
	
	/** 
		Establish a switcher pane.
		
		@param jc An array of JComponents to embed in the switcher.
		@param t A title to place at the top of the switcher, can be null.
	*/
	public SwitcherPane( JComponent [] jc, String t )
	{
		components = new LinkedList();
		listeners = new LinkedList();
		
		for( int i = 0; i < jc.length; ++i )
			components.add( jc[i] );
	
		title = t;
		curIndex = 0;
				
		setupGui();
	}
	
	/**
		Returns the current index of the component that's visible
	*/
	public int getCurrentIndex() { return curIndex; }
	
	/**
		Add a listener who gets informed on when a component switch occurs.
		@param spl The SwitcherPaneListener to be added
		@see jnesg.gui.SwitcherPaneListener
	*/
	public void addSwitcherPaneListener( SwitcherPaneListener spl )
	{
		listeners.add( spl );
	}
	
	public void removeSwitcherPaneListener( SwitcherPaneListener spl )
	{
		listeners.remove( spl );
	}
	
	public void addComponent( JComponent j, boolean switchTo )
	{
		components.add(j);
		decideWhosEnabled();
		
		if( switchTo )
		{
			curIndex = components.size() - 1;
			decideWhosEnabled();
			updateView();
		}
	}
	
	public void addComponent( JComponent j )
	{
		addComponent( j, false );
	}
	
	public List getAllComponents()
	{
		return components;
	}
	
	public JComponent getJComponent( int i )
	{
		if( i < 0 || i >= components.size() )
			return null;
			
		return (JComponent)components.get(i);
	}
	
	public JComponent getJComponent()
	{
		return (JComponent)components.get( curIndex );
	}
	
	public int getNumberComponents() { return components.size(); }
	
	public void setView( int i )
	{
		if( i < 0 || i >= components.size() )
			return;
			
		curIndex = i;
		updateView();
	}
	
	public boolean removeComponent( JComponent j )
	{
		boolean ret = components.remove(j);
		
		if( ret )
		{
			decideWhosEnabled();
			if( curIndex == components.size() )
				--curIndex;
			
			updateView();
		}
		
		return ret;
	}
	
	public boolean removeComponent( int i )
	{
		if( i >= components.size() || i < 0 ) return false;
	
		return removeComponent( (JComponent)components.get(i) );
	}
		
	public void removeComponents( boolean refresh )
	{
		components.clear();
		
		if( refresh )
			updateView();
	}
	
	public void removeComponents()
	{
		removeComponents( false );
	}
	
	public void actionPerformed( ActionEvent e )
	{
		Object o = e.getSource();
		
		if( o != rightB && o != leftB ) return;
		
		if( o == rightB && curIndex < components.size() - 1 )
			++curIndex;
			
		if( o == leftB && curIndex > 0 )
			--curIndex;
			
		decideWhosEnabled();
		
		updateView();
		
		fireComponentChanged();
	}
			
	private void fireComponentChanged()
	{
		for( Iterator iter = listeners.iterator(); iter.hasNext(); )
		{
			SwitcherPaneListener spl = (SwitcherPaneListener)iter.next();
			spl.componentChanged();
		}
	}
		
	
	private void setupGui()
	{
		setLayout( new BorderLayout() );
		
		establishSwitchSection();
		
		add( switchSection, "South" );
	
		if( components.size() > 0 )	
			add( (JComponent)components.get(0), "Center" );
		
		curComp = (JComponent)components.get(0);	
			
		if( title != null )
			setBorder( BorderFactory.createTitledBorder( title ) );
	}
	
	private void establishSwitchSection()
	{
		leftB = new JButton("<");
		leftB.addActionListener(this);
		rightB = new JButton(">");
		rightB.addActionListener(this);
		indexLabel = new JLabel("  " + (curIndex+1) + " of " + components.size() );
		
		switchSection = new Box( BoxLayout.X_AXIS );
		
		switchSection.add( leftB );
		switchSection.add( rightB );
		switchSection.add( indexLabel );
		
		decideWhosEnabled();
	}
		
	private void decideWhosEnabled()
	{
		if( curIndex == 0 )
			leftB.setEnabled( false );
		else
			leftB.setEnabled( true );
			
		if( curIndex == components.size() - 1 )
			rightB.setEnabled( false );
		else
			rightB.setEnabled( true );
	}
	
	private void updateView()
	{
		remove( curComp );
		curComp = (JComponent)components.get( curIndex );
		add( curComp, "Center" );
		
		indexLabel.setText("  " + (curIndex + 1) + " of " + components.size());
		
		Graphics g = getGraphics();
		paintAll(g);
		
	}
	
	private JButton rightB, leftB;
	private List components, listeners;
	private JComponent curComp;
	private int curIndex;
	private Box switchSection;
	private JLabel indexLabel;
	private String title;
}

