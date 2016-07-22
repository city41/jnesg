package jnesg.palette;

import java.awt.*;
import javax.swing.JPanel;
import java.awt.dnd.*;

public abstract class Palette extends JPanel
{
	public static class PaletteDragSourceListener
		extends DragSourceAdapter
	{
		// empty do nothing class
		// needed for drag and drop system
	}	
	
	public static void initDnD( DragGestureListener pe )
	{
		DragSource dSource = DragSource.getDefaultDragSource();
		dSource.createDefaultDragGestureRecognizer( (Component)pe,
			DnDConstants.ACTION_COPY_OR_MOVE, pe );
	}
}
