package jnesg;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class About extends JFrame
	implements ActionListener
{
	public About()
	{		
		setTitle("About JNesG");
		setIconImage( Main.turtleImg );			
		JPanel p = new JPanel();
		p.add( new JLabel(aboutText));
		getContentPane().add(p, "Center");
		
		JPanel pp = new JPanel();
		pp.add( okB = new JButton("Okay"));
		
		okB.addActionListener(this);
		
		getContentPane().add( pp, "South" );
		
		pack();
	}
	
	public void actionPerformed( ActionEvent e )
	{
		setVisible(false);
	}
	
	private static final String aboutText = 
	"<html><center><b>JNesG v" + Main.VERSION + "</b>"
	+"<br>An NES Graphics Development Program"
	+"<br><br>"
	+"by Matt Greer (matthew-greer@uiowa.edu)"
	+"<br><br>"
	+"Please email me with comments, suggestions, bug notices, etc";
	
	private JButton okB;
}
	
	