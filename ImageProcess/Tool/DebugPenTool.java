package ImageProcess.Tool;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.Dimension;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JLabel;
import ImageProcess.ImageHolder;
import java.awt.Color;
import java.awt.Graphics2D;

public class DebugPenTool extends ImageTool{
	Graphics2D graphics = null;
	
	public DebugPenTool(){
		debugPrintln("Made Debug Pen Tool");
	}
	
	public void setImageHolder(ImageHolder iH){
		holder = iH;
		graphics = holder.getImage().createGraphics();
		graphics.setColor(color1);
	}
	
	public JPanel createOptionsPanel(){
		JPanel panel = new JPanel();
		panel.add(new JLabel("Debug Pen Tool"));
		panel.setPreferredSize(new Dimension(200,50));
			
		return panel;
	}
	
	public void mousePressed(MouseEvent e){
		e.translatePoint(-1,-1);
		if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) !=0){
			graphics.setColor(color1);
			graphics.fillRect(e.getX(),e.getY(),5,5);
			processor.setImageHolder(holder);
		} else if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) !=0){
			graphics.setColor(color2);
			graphics.fillRect(e.getX(),e.getY(),5,5);
			processor.setImageHolder(holder);
		}
	}

	public void mouseReleased(MouseEvent e){

	}
	
	public void mouseExited(MouseEvent e){
		
	}
	
	public void mouseEntered(MouseEvent e){
		
	}
	
	public void mouseClicked(MouseEvent e){
		
	}
	
	public void mouseDragged(MouseEvent e){
		e.translatePoint(-1,-1);
		if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) !=0){
			graphics.setColor(color1);
			graphics.fillRect(e.getX(),e.getY(),5,5);
			processor.setImageHolder(holder);
		} else if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) !=0){
			graphics.setColor(color2);
			graphics.fillRect(e.getX(),e.getY(),5,5);
			processor.setImageHolder(holder);
		}
	}
	
	public void mouseMoved(MouseEvent e){
		
	}

}

