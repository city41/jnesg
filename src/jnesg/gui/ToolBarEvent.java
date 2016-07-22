package jnesg.gui;

import java.awt.AWTEvent;

public class ToolBarEvent extends AWTEvent
{
	public ToolBarEvent( int i, String ac, Object eventObj )
	{
		// 2000 is hopefully a bogus ID, to not mess with with AWT event system
		super( eventObj, 2000 );
		
		index = i;
		actionCommand = ac;
	}
	
	public int getIndex() { return index; }
	public String getActionCommand() { return actionCommand; }
	
	private int index;
	private String actionCommand;
}
