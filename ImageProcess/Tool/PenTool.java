package ImageProcess.Tool;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.BoxLayout;
import ImageProcess.ImageHolder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Line2D.Float;

public class PenTool extends ImageTool implements ChangeListener{
	Graphics2D graphics = null;
	int size = 1;
	boolean antialias = false;
	int oldX, oldY;
	JSlider sizeSlider;
	
	public PenTool(){
		super();
	}
	
	public void setImageHolder(ImageHolder iH){
		holder = iH;
		graphics = holder.getImage().createGraphics();
		graphics.setColor(color1);
	}
	
	public JPanel createOptionsPanel(){
		JPanel panel = new JPanel();
		JTabbedPane tabs = new JTabbedPane();
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		
		sizeSlider = new JSlider(1,100,1);
		panel.add(sizeSlider);
		sizeSlider.addChangeListener(new PenSizeListener());
		panel.add(tabs);
		
		tabs.add("Primary Color",chooser1);
		tabs.add("Secondary Color",chooser2);
			
		return panel;
	}
	
	private class PenSizeListener implements ChangeListener{
		public void stateChanged(ChangeEvent e) {
			size = sizeSlider.getValue();
			graphics.setStroke(new BasicStroke(size));
		}
	}
	
	public void mousePressed(MouseEvent e){
		e.translatePoint(-2,-2);
		oldX = e.getX();
		oldY = e.getY();
		if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) !=0){
			graphics.setColor(color1);
			graphics.fillRect(e.getX(),e.getY(),size,size);
			processor.setImageHolder(holder);
		} else if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) !=0){
			graphics.setColor(color2);
			graphics.fillRect(e.getX(),e.getY(),size,size);
			processor.setImageHolder(holder);
		}
	}

	public void mouseReleased(MouseEvent e){
		e.translatePoint(-2,-2);
		if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) !=0){
			graphics.setColor(color1);
			graphics.fillRect(e.getX(),e.getY(),size,size);
			processor.setImageHolder(holder);
		} else if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) !=0){
			graphics.setColor(color2);
			graphics.fillRect(e.getX(),e.getY(),size,size);
			processor.setImageHolder(holder);
		}
	}
	
	public void mouseExited(MouseEvent e){
		
	}
	
	public void mouseEntered(MouseEvent e){
		
	}
	
	public void mouseClicked(MouseEvent e){
		
	}
	
	public void mouseDragged(MouseEvent e){
		e.translatePoint(-2,-2);
		if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) !=0){
			graphics.setColor(color1);
			graphics.drawLine(oldX,oldY,e.getX(),e.getY());
			oldX = e.getX();
			oldY = e.getY();			
			//graphics.fillRect(e.getX(),e.getY(),size,size);
			processor.setImageHolder(holder);
		} else if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) !=0){
			graphics.setColor(color2);
			graphics.drawLine(oldX,oldY,e.getX(),e.getY());
			oldX = e.getX();
			oldY = e.getY();
			//graphics.fillRect(e.getX(),e.getY(),size,size);
			processor.setImageHolder(holder);
		}
	}
	
	public void mouseMoved(MouseEvent e){
		
	}

}

