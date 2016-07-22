package jnesg.ext;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import jnesg.Main;

public class Exporter extends JFrame
	implements ActionListener
{
	public Exporter()
	{
		setIconImage( Main.turtleImg ); 
		dir = Main.getRootDir();
		setupGUI();
		pack();
		
		setTitle("JNesG Exporter");
	}
	
	private void setupGUI()
	{
		JPanel b = new JPanel();
		b.setLayout( new GridBagLayout() );
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = gbc.BOTH;
		gbc.gridx = gbc.gridy = 0;
		gbc.gridwidth = gbc.gridheight = 1;
		
		b.add( setupRootField(), gbc );
		
		gbc.gridy++;
		b.add( setBinaryTextToggle(), gbc );
		
		gbc.gridy++;	
		b.add( setupTypeCheckBoxes(), gbc );
		
		gbc.gridy++;
		b.add( setupPalChrSameFileCheckBoxes(), gbc );
		
		gbc.gridy++;
		b.add( setupDirectoryChooser(), gbc );
		
		gbc.gridy++;
		b.add( exportB = new JButton("Export"), gbc );
	
		exportB.addActionListener( this );
			
		getContentPane().add( b, "Center" );
	}
	
	private Container setBinaryTextToggle()
	{
		Box b = new Box( BoxLayout.Y_AXIS );
		b.setBorder( BorderFactory.createTitledBorder( "Export mode(s)" ) );
		texCB = new JCheckBox( "ascii" );
		binCB = new JCheckBox( "binary" );
		
		b.add( texCB );
		b.add( binCB );
	
		return b;
	}
		
		
	private Container setupRootField()
	{
		JPanel p = new JPanel();
		p.setBorder( BorderFactory.createEtchedBorder() );
		
		JLabel rootTFL = new JLabel("Root for file names: ");
		p.add(rootTFL);
		
		rootTF = new JTextField(10);
		p.add(rootTF);
		
		return p;
	}
	
	private Container setupTypeCheckBoxes()
	{
		Box b = new Box( BoxLayout.Y_AXIS );
		b.setBorder( BorderFactory.createTitledBorder( "What should be exported?" ) );
			
		b.add( palCB = new JCheckBox("Palette(s)", true) );
		b.add( chrCB = new JCheckBox("CHR Block(s)", true) );
		b.add( ntCB = new JCheckBox("NameTable(s)", true ) );
		
		return b;
	}
	
	private Container setupPalChrSameFileCheckBoxes()
	{
		Box b = new Box( BoxLayout.Y_AXIS );
		b.setBorder( BorderFactory.createEtchedBorder() );
					
		b.add( multCB = new JCheckBox("Save each type to multiple files", true) );
		
		b.add( new JLabel("<html>For example: If there are multiple nametables,<br>this"
			+ " option saves them to &lt;root&gt;_nt0.bin,<br> &lt;root&gt;_nt1.bin, &lt;root&gt;_nt2.bin...</html>") );
		
		return b;
	}
	
	private Container setupDirectoryChooser()
	{
		JPanel p = new JPanel();
		p.setBorder( BorderFactory.createTitledBorder( "Save Directory" ));
		
		p.add( directoryTF = new JTextField( dir.getAbsolutePath(), 10 ));
		p.add( browseB = new JButton("Browse...") );
		
		browseB.addActionListener( this );
		
		return p;
	}
		
	public void actionPerformed( ActionEvent e )
	{
		Object o = e.getSource();
		if( o == exportB ) doExport();
		
		if( o == browseB ) browse();
	}
	
	private void browse()
	{
		JFileChooser dirChooser = new JFileChooser( dir );
		dirChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		
		int val = dirChooser.showOpenDialog( this );

		if( val != JFileChooser.APPROVE_OPTION )
			return;
			
		dir = dirChooser.getSelectedFile();
		directoryTF.setText( dir.getAbsolutePath() );
		repaint();		
		
	}	
	
	private void doExport()
	{
		root = rootTF.getText();
		boolean fail = false;
		try 
		{
			if( palCB.isSelected() )
			{
				Main.getPaletteEditor().export( dir, root + PALTAG, multCB.isSelected() );	
			}
			
			if( ntCB.isSelected() )
			{
				Main.getNameTableManager().export( dir, root + NTTAG, multCB.isSelected() );
			}
			
			if( chrCB.isSelected() )
			{
				Main.getCHRManager().export( dir, root + CHRTAG, multCB.isSelected() );
			}
			
		}
		catch( IOException ioe )
		{
			JOptionPane.showMessageDialog( this, "There was an error during export." );
			setVisible( false );
			return;
		}
		
		JOptionPane.showMessageDialog( this, "Successfull export." );
		setVisible( false );	
	}

	private String root;
	private File dir;
	public static final String PALTAG = "_pal", CHRTAG = "_chr", NTTAG = "_nt";
	public static final String BINARY_EXT = ".bin";
	
	private JTextField rootTF, directoryTF;
	private JCheckBox palCB, chrCB, ntCB, multCB, texCB, binCB;
	private JButton browseB, exportB;
}

