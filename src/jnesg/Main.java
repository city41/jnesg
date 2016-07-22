package jnesg;

import java.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import jnesg.tile.*;
import jnesg.palette.*;
import jnesg.chr.*;
import jnesg.nt.*;
import jnesg.gui.*;
import jnesg.ext.*;

/**
	Main container of the program.
	All other components get instantiated and ran from here
	
	@author Matt Greer <a href="mailto:matthew-greer@uiowa.edu">matthew-greer@uiowa.edu</a>
	@version 0.3a
*/
public final class Main extends JFrame
	implements ActionListener, ToolBarListener
{
	static
	{

		
		rootDir = new File("./");
	}
	
	/**
		The version of the program. In the form of<br/> 
		&lt;major number&gt;.&lt;minor number&gt;&lt;general stability letter&gt;<br/>
		a = alpha, b = beta, p = production, m = mature
	*/
	public static final String VERSION = "0.3a";	
	
	/** 
		Main entry point for the program 
	*/
	public static void main( String [] args )
	{
		// set swing up to mimic the native widgets if possible
		try
		{
        	UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{ 
			// if not, oh well
		}

		//nasty hack!
		EditablePalette ep = new EditablePalette();

		ImageIcon turt = new ImageIcon( ep.getClass().getClassLoader().getResource( "icons/turtle.gif" ));
		turtleImg = turt.getImage();		
				
		// establish the major pieces of the program
		paletteEditor = new PaletteEditor();
		chrManager = new CHRManager();
		tileEditor = new TileEditor();
		nameTableManager = new NameTableManager();
		mainInstance = new Main();		
		
		// and run the program
		mainInstance.setVisible(true);
	}
	
	/**
		Establish the main frame container.
	*/
	public Main()
	{
		setTitle("JNesG v" + VERSION );
		setIconImage( turtleImg );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		
		// subcomponents local to Main, not worthy of being 
		// globally accessible
		importer = new Importer();
		exporter = new Exporter();
		about = new About();
		
		setupGui();
	}
	
	/**
		Repaint all the major, paintable, components of the program.
	*/
	public void refresh()
	{
		paintAll( getGraphics() );
	}
	
	private void setupGui()
	{
		setupMenu();
		getContentPane().add( chrManager, "East" );
		
		JTabbedPane jtp = new JTabbedPane( JTabbedPane.TOP );
		
		JPanel p = new JPanel();
		p.setLayout( new BorderLayout() );
		
		p.add( paletteEditor, "North" );
		p.add( tileEditor, "Center" );
		
		jtp.addTab( "Tiles", p );
		jtp.addTab( "Name Tables", nameTableManager );
		
		getContentPane().add( jtp, "Center" );
		
		setupToolbar();
		
		getContentPane().add( mainToolbarPanel, "North" );
		
		pack();
	}
	
	private void setupMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("File");
		
		openM = new JMenuItem("Open");
		openM.setAccelerator( KeyStroke.getKeyStroke( "control O" ) );
		openM.addActionListener( this );
		
		file.add( openM );
		
		saveM = new JMenuItem("Save");
		saveM.setAccelerator( KeyStroke.getKeyStroke( "control S" ) );
		saveM.addActionListener( this );
		
		file.add( saveM );
		file.addSeparator();
		
		JMenu importMenu = new JMenu("Import");
		
		
		iNESImportM = new JMenuItem("from iNES rom");
		iNESImportM.addActionListener( this );
		importMenu.add( iNESImportM );
		
		regImportM = new JMenuItem("from Binary");
		regImportM.addActionListener( this );
		importMenu.add( regImportM );
		
		file.add( importMenu );
		
		JMenu exportMenu = new JMenu("Export");
		
		
		iNESExportM = new JMenuItem("to iNES rom");
		iNESExportM.addActionListener( this );
		exportMenu.add( iNESExportM );
		
		regExportM = new JMenuItem("to binary");
		regExportM.addActionListener( this );
		exportMenu.add( regExportM );
		
		file.add( exportMenu );		
		
		file.addSeparator();
		file.add( new JMenuItem("Reset") );
		file.addSeparator();
		file.add( new JMenuItem("Quit") );
		
		JMenu help = new JMenu("Help");
		help.add( new JMenuItem("Help") );
		
		aboutM = new JMenuItem("About");
		aboutM.addActionListener( this );
		help.add( aboutM );
		
		menuBar.add(file);
		menuBar.add(help);
		
		setJMenuBar(menuBar);
	}	
	
	/**
		Establish the toolbar buttons at the top of the window.
		the main frame then listens for toolbar events that indicate
		buttons being pressed
	*/
	private void setupToolbar()
	{	
		ImageIcon [] icons = {  new ImageIcon( getClass().getClassLoader().getResource("icons/open24.gif") ),
								new ImageIcon( getClass().getClassLoader().getResource("icons/save24.gif") ),
								new ImageIcon( getClass().getClassLoader().getResource("icons/importROM24.jpg") ),
								new ImageIcon( getClass().getClassLoader().getResource("icons/import24.gif") ),
								new ImageIcon( getClass().getClassLoader().getResource("icons/exportROM24.jpg") ),
								new ImageIcon( getClass().getClassLoader().getResource("icons/export24.gif") ),
								new ImageIcon( getClass().getClassLoader().getResource("icons/new24.gif" )) };
								
		String [] toolTips = { "Open", "Save", "Import from ROM",
			"Import from Binary", "Export to ROM", "Export to Binary", "Reset" };
		
		mainToolbar = new ToolBar( icons, toolTips, ToolBar.HORIZONTAL, false );
		mainToolbarPanel = new Box( BoxLayout.X_AXIS );
		
		mainToolbarPanel.add( mainToolbar );
		mainToolbar.addToolBarListener( this );
	}
	
	/********* ACTIONS **********************/
	/**
		Listens for events for all the major, toplevel components. Each
		major subcomponent has listeners for its own components
	*/
	public void actionPerformed( ActionEvent e )
	{
		Object o = e.getSource();
		
		if( o == iNESImportM )
		{
			iNESImport();
		}
		
		if( o == iNESExportM )
		{
			iNESExport();
		}
		
		if( o == aboutM )
		{
			showAboutBox();
		}
		
		if( o == regExportM )
		{
			regExport();
		}
		
		if( o == regImportM )
		{
			regImport();
		}
		
		if( o == saveM )
		{
			save();
		}
		
		if( o == openM )
		{
			open();
		}
	}
	
	/**
		Save the current state of the program in an internal
		format to disk. This is used instead of serialization
		because exporting meant I already had an easy way to
		save states. It's also lighterweight
	*/
	private void save()
	{
		JFileChooser saveChooser;
		if( saveFile == null )
		{
			saveChooser = new JFileChooser();
		}
		else
		{
			saveChooser = new JFileChooser( saveFile );
		}
		saveChooser.setDialogType( JFileChooser.SAVE_DIALOG );
		
		int val = saveChooser.showOpenDialog( this );
		
		if( val == JFileChooser.APPROVE_OPTION )
			saveFile = saveChooser.getSelectedFile();
		else
			return;
		
		System.out.println( saveFile );	
			
		try
		{	
			FileOutputStream fos = new FileOutputStream( saveFile );
			writeHeader( fos );
			paletteEditor.save( fos );
			chrManager.save( fos );
			nameTableManager.save( fos );

			fos.close();				
		}
		catch( Exception ioe )
		{
			JOptionPane.showMessageDialog( this, "Error while saving: " + ioe );
			ioe.printStackTrace();
		}
	}
	
	/**
		Write a JNesG internal file save format to the outputstream give.
		@param os the OutputStream to write to.
		<p>
			The header format is...
		<pre>
			byte	value
			0		'J'
			1		'N'
			2		'G'
			3		'\0'
			4		number of palettes
			5		number of CHR blocks
			6		number of NameTables
			7-15	all zeros
		</pre>
		</p>
	*/
	
	private void writeHeader( OutputStream os ) throws IOException
	{
		os.write( 'J' );
		os.write( 'N' );
		os.write( 'G' );
		os.write( 0 );
		os.write( paletteEditor.getNumberPalettes() );
		os.write( chrManager.getNumberBlocks() );
		os.write( nameTableManager.getNumberNameTables() );
		
		for( int i = 0; i < 9; ++i )
			os.write( 0 );
	}	
	
	
	/** 
		Open a jnesg save file and load its contents into 
		the various components of the program.
	*/
	private void open()
	{
		JFileChooser openChooser;
		if( openFile == null && saveFile == null )
		{
			openChooser = new JFileChooser();
		}
		else if( openFile == null )
		{
			openChooser = new JFileChooser( saveFile );
		}
		else
		{
			openChooser = new JFileChooser( openFile );
		}
		
		int val = openChooser.showOpenDialog( this );
		
		if( val == JFileChooser.APPROVE_OPTION )
			openFile = openChooser.getSelectedFile();
		else
			return;
		
		try
		{	
			FileInputStream fis = new FileInputStream( openFile );
			
			if( !verifyInternalFormat( fis ) )
				throw new Exception("This file appears to not be a valid JNesG save file.");
			
			int numPals = fis.read();
			int numCHRs = fis.read();
			int numNTs = fis.read();
			fis.skip( 9 );
			
			byte [] buff = new byte[1024];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int bytesRead;
			
			while( (bytesRead = fis.read( buff )) != -1 )
			{
				baos.write( buff, 0, bytesRead );
			}
			fis.close();
			
			byte [] savedData = baos.toByteArray();
			baos.close();
			
			int position = 0;
			
			position = paletteEditor.open( savedData, position, numPals );
			position = chrManager.open( savedData, position, numCHRs );
			nameTableManager.open( savedData, position, numNTs );
		}
		catch( Exception ioe )
		{
			JOptionPane.showMessageDialog( this, "Error while loading: " + ioe );
			ioe.printStackTrace();
		}
	}
	
	private boolean verifyInternalFormat( FileInputStream fis ) throws IOException
	{
		return fis.read() == 'J'
			&& fis.read() == 'N'
			&& fis.read() == 'G'
			&& fis.read() == 0;
	}
	
	/**
		Pull up the exporter window to allow the user
		to export the current state of each component
		to files for use in NES projects
	*/
	private void regExport()
	{
		// make sure the export window appears in the center
		// of the main frame
		Dimension parSize = getSize();
		Dimension chiSize = exporter.getSize();
		
		Point parP = getLocation();
	
		int x = parSize.width / 2 - chiSize.width / 2 + parP.x;
		int y = parSize.height / 2 - chiSize.height / 2 + parP.y;
		
		exporter.setLocation( x, y );
		exporter.setVisible( true );		
	}
	
	/**
		Bring up the importer window to allow the user
		to import raw binary files containing exactly
		the NES graphic contents to then be edited in the program
	*/
	private void regImport()
	{
		// make sure the import window appears in the center
		// of the main window
		Dimension parSize = getSize();
		Dimension chiSize = importer.getSize();
		
		Point parP = getLocation();
	
		int x = parSize.width / 2 - chiSize.width / 2 + parP.x;
		int y = parSize.height / 2 - chiSize.height / 2 + parP.y;
		
		importer.setLocation( x, y );
		importer.setVisible( true );		
	}	
	
	/**
		Show the about box which describes the program, version, etc.
		Invoked from the help menu
	*/
	private void showAboutBox()
	{
		//center about box
		Dimension parSize = getSize();
		Dimension chiSize = about.getSize();
		
		Point parP = getLocation();
	
		int x = parSize.width / 2 - chiSize.width / 2 + parP.x;
		int y = parSize.height / 2 - chiSize.height / 2 + parP.y;
		
		about.setLocation( x, y );
		about.setVisible( true );
	}
		
	/**
		Brings up an INESImporter to import CHR blocks from
		an iNES ROM into the program for editing.
	*/
	private void iNESImport()
	{
		// have the user select an iNES ROM file
		File romFile = null;
		JFileChooser romChooser = new JFileChooser();
		
		int val = romChooser.showOpenDialog( this );
		if( val == JFileChooser.APPROVE_OPTION )
			romFile = romChooser.getSelectedFile();
		else
			return;	
			
		byte [][] rawChrBanks = null;	
		try
		{
			INESImporter imp = new INESImporter( romFile );
			rawChrBanks = imp.getCHRBanks();
		}
		catch( IOException ioe )
		{
			JOptionPane.showMessageDialog( this, "Failed to import ROM" );
			ioe.printStackTrace();
		}
		/*
			JNesG is incapable of extracting palettes from ROMs.
			So to make sure the tiles show up clearly, a palette
			with four high contrast colors is set
		*/
		paletteEditor.setReasonableColors();		
		
		// hand off the raw chr blocks to the chr manager
		// to be loaded into the system
		if( rawChrBanks.length > 0 )
		{	
			chrManager.setBanksFromRaw( rawChrBanks );
		}
		else
		{
			JOptionPane.showMessageDialog( this, "ROM has no CHR Blocks" );
		}

		refresh();
	}
	
	/**
		Brings up an INESExporter to export the current
		contents of CHR Blocks into an iNES ROM
	*/
	private void iNESExport()
	{
		System.out.println("iNESExport()");
		
		File romFile = null;
		JFileChooser romChooser = new JFileChooser();
		
		int val = romChooser.showOpenDialog( this );
		if( val == JFileChooser.APPROVE_OPTION )
			romFile = romChooser.getSelectedFile();
			
		try
		{
			INESExporter imp = new INESExporter( romFile, chrManager );
		}
		catch( IOException ioe )
		{
			JOptionPane.showMessageDialog( this, "Failed to export ROM: " + ioe.getMessage() );
		}
	}	
	
	/**
		The toolbar listener which keeps an eye on
		button presses on the toolbar.
	*/
	public void toolBarButtonPressed( ToolBarEvent tbe )
	{
		// the "tooltip" is the best way to distinguish
		// buttons here, which gets stored in the event's
		// actionCommand string
		String text = tbe.getActionCommand();
		
		if( text.equals("Import from ROM"))
			iNESImport();
			
		if( text.equals("Import from Binary"))
			regImport();
			
		if( text.equals("Export to ROM"))
			iNESExport();
			
		if( text.equals("Export to Binary"))
			regExport();
			
		if( text.equals("Open"))
			open();
			
		if( text.equals("Save"))
			save();
	}
	
	
	/********** MENU ITEMS *********************/
	
	private JMenuItem saveM, openM, exportM, iNESImportM, aboutM, iNESExportM;
	private JMenuItem regExportM, regImportM;
	
	private ToolBar mainToolbar;
	private Container mainToolbarPanel;
	private Importer importer;
	private Exporter exporter;
	private File saveFile, openFile;
	
	
	/******************* sTATIC STUFF BELOW ***************************/
	
	
	// static instances, used to access any part of program from anywhere	
	/**
		Returns the program's palette editor.
	*/
	public static PaletteEditor getPaletteEditor() { return paletteEditor; }
	
	/**
		Returns the program's tile editor.
	*/
	public static TileEditor getTileEditor() { return tileEditor; }
	
	/**
		Returns the program's NameTable manager.
	*/
	public static NameTableManager getNameTableManager() { return nameTableManager; }
	
	/**
		Returns the program's CHR manager. 
	*/
	public static CHRManager getCHRManager() { return chrManager; }
	
	/**
		Returns the program's main instance (the instance of this class that is running).
	*/
	public static Main getMain() { return mainInstance; }
	
	/**
		Return's the root directory the program is running from.
		Soon to be deprecated.
	*/
	public static File getRootDir() { return rootDir; }

	// icon displayed in the title bars of windows
	public static Image turtleImg;
	
	private static PaletteEditor paletteEditor;
	private static CHRManager chrManager;
	private static TileEditor tileEditor;
	private static NameTableManager nameTableManager;
	private static Main mainInstance;
	private static About about;
	private static final File rootDir;
}
	