import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPopupMenu;

import prefuse.controls.ControlAdapter;
import prefuse.controls.Control;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class FinalControlListener1 extends ControlAdapter implements Control {

	public void itemClicked(VisualItem item, MouseEvent e) 
	{
		if(item instanceof NodeItem)
		{
			String label = ((String) item.get("label"));
			String value = (String) item.get("value");
			String source = (String) item.get("source");

			JPopupMenu jpub = new JPopupMenu();
			jpub.add("label: " + label);
			jpub.add("value: " + value);
			jpub.add("source: " + source);
			jpub.show(e.getComponent(),(int) item.getX(), (int) item.getY());
		}
	}

}
