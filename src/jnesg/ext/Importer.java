package jnesg.ext;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import jnesg.Main;

public class Importer extends JFrame
	implements ActionListener
{
	public Importer()
	{
		setTitle( "JNesG Importer" );
		setIconImage( Main.turtleImg );
		files = new File[3];
		
		for( int i = 0; i < files.length; ++i )
			files[i] = Main.getRootDir();
		
		setupGui();
		pack();
	}
	
	private void setupGui()
	{
		fileTF = new JTextField[3];
		browseB = new JButton[3];
		
		Box b = new Box( BoxLayout.Y_AXIS );
				
		b.add( createImportBox( "Palette", 0 ) );
		b.add( createImportBox( "CHR", 1 ) );
		b.add( createImportBox( "NameTable", 2 ) );
		
		importB = new JButton("Import");
		importB.addActionListener( this );
		
		getContentPane().add(b, "Center");
		getContentPane().add( importB, "South" );
		
		getContentPane().add( new JLabel( warning ), "North" );
	}
	
	private Container createImportBox( String type, int i )
	{
		Box b = new Box( BoxLayout.X_AXIS );
		b.setBorder( BorderFactory.createTitledBorder( type ) );
		
		fileTF[i] = new JTextField( 10 );
		b.add( fileTF[i] );
		
		browseB[i] = new JButton( "Browse..." );
		b.add( browseB[i] );
		browseB[i].addActionListener( this );
		
		return b;
	}
	
	public void actionPerformed( ActionEvent e )
	{
		int ind = -1;
		Object o = e.getSource();
		
		for( int i = 0; i < browseB.length; ++i )
		{
			if( browseB[i] == o )
				ind = i;
		}
		
		if( ind >= 0 )
		{
			files[ind] = pickFile( ind );
			
			if( files[ind] != null )
				fileTF[ind].setText( files[ind].getAbsolutePath() );
		}
		
		if( o == importB )
			doImport();
	}
	
	private File pickFile( int i )
	{
		JFileChooser fileChooser = new JFileChooser( files[i] );
		
		int val = fileChooser.showOpenDialog( this );
		if( val == JFileChooser.APPROVE_OPTION )
			return fileChooser.getSelectedFile();
		
		return null;
	}
		
	
	private void doImport()
	{
		byte [][] data = new byte [3][];
		
		for( int i = 0; i < files.length; ++i )
		{
			if( files[i] != null )
				data[i] = getData( files[i] );
			else
				data[i] = null;
		}
		
		if( data[0] != null )
		{
			Main.getPaletteEditor().doImport( data[0] );
		}
		if( data[1] != null )
		{
			Main.getCHRManager().doImport( data[1] );
		}
		if( data[2] != null )
		{
			Main.getNameTableManager().doImport( data[2] );
		}
		
		setVisible( false );
	}
	
	private byte [] getData( File f )
	{
		try
		{
			FileInputStream fis = new FileInputStream( f );
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			byte [] buff = new byte[ 1024 ];
			int bytesRead = 0;
			
			while(( bytesRead = fis.read( buff ) ) != -1 )
			{
				baos.write( buff, 0, bytesRead );
			}
			
			byte [] ret = baos.toByteArray();
				
			baos.close();
			fis.close();
			
			return ret;
		}
		catch( IOException ioe )
		{
			return null;
		}
	}
			
			
			
	
	private File [] files;
	private JButton [] browseB;
	private JTextField [] fileTF;
	private JButton importB;
	
	private static final String warning = "<html>Select files to import from.<br>"
							+ "You don't need to select files for<br>"
							+ "all three. Importing will cause the<br>corresponding"
							+ "existing data to be lost.</html>";
}

