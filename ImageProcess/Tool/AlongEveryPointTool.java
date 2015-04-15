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
import javax.swing.BorderFactory;
import ImageProcess.ImageHolder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;

public class AlongEveryPointTool extends ImageTool implements ChangeListener{
	Graphics2D graphics = null;
	int size = 1;
	boolean antialias = false;
	int oldX, oldY;
	JSlider sizeSlider;
	
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
		sizeSlider.setBorder(BorderFactory.createTitledBorder("Size"));
		panel.add(sizeSlider);
		sizeSlider.addChangeListener(new EveryPointSizeListener());
		panel.add(tabs);
		
		tabs.add("Primary Color",chooser1);
		tabs.add("Secondary Color",chooser2);
			
		return panel;
	}
	
	private class EveryPointSizeListener implements ChangeListener{
		public void stateChanged(ChangeEvent e) {
			size = sizeSlider.getValue();
		}
	}
	
	public void mousePressed(MouseEvent e){
		e.translatePoint(-2,-2);
		oldX = e.getX();
		oldY = e.getY();
		doAtPixel(e,e.getX(),e.getY());
		
	}

	public void mouseReleased(MouseEvent e){
		e.translatePoint(-2,-2);
		doAtPixel(e,e.getX(),e.getY());
	}
	
	public void mouseDragged(MouseEvent e){
		e.translatePoint(-2,-2);
		if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) !=0){
			graphics.setColor(color1);
			brz(e,oldX,oldY,e.getX(),e.getY());
			//graphics.drawLine(oldX,oldY,e.getX(),e.getY());
			oldX = e.getX();
			oldY = e.getY();			
			//graphics.fillRect(e.getX(),e.getY(),size,size);
			processor.setImageHolder(holder);
		} else if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) !=0){
			graphics.setColor(color2);
			brz(e,oldX,oldY,e.getX(),e.getY());
			//graphics.drawLine(oldX,oldY,e.getX(),e.getY());
			oldX = e.getX();
			oldY = e.getY();
			//graphics.fillRect(e.getX(),e.getY(),size,size);
			processor.setImageHolder(holder);
		}
	}
	
	public void brz(MouseEvent event, int x0, int y0, int x1, int y1){
		//non-dashed line
		if(x0-x1 == 0){
			if(y0-y1 ==0){
				doAtPixel(event,x0,y0);
				return;
			} else if(y0 > y1){
				for(int i = y0; i >= y1; i--){
					doAtPixel(event,x0,i);
				}
				return;
			} else {
				for(int i = y0; i <= y1; i++){
					doAtPixel(event,x0,i);
				}
				return;
			}
		} else if((y1-y0)*(y1-y0)<(x1-x0)*(x1-x0)){
			
			if(x0 > x1){
				int temp = y0;
				y0 = y1;
				y1 = temp;
				temp = x0;
				x0 = x1;
				x1 = temp;
			}
			//Bresenham's Algorithm for -1<m<1
			
			int deltY = y1-y0;
			int deltX = x1-x0;
			int increment = 1;
			if(y1-y0<0){
				increment = -1;
				deltY = -deltY;
			}
			int e = 2 * deltY - deltX;
			int inc1 = 2 * deltY;
			int inc2 = 2 * (deltY - deltX);
			int y = y0;
			int x = x0;
			
			do{
				doAtPixel(event,x,y);
				if(e < 0)
				{
					e = e + inc1;
				} else {
					y = y + increment;
					e = e + inc2;
				}
				x++;
			}while (x < x1);
			
		} 
		else 
		{
			//bresenham's for m<-1 or m>1
			
			if(y0 > y1)
			{
				int temp = y0;
				y0 = y1;
				y1 = temp;
				temp = x0;
				x0 = x1;
				x1 = temp;
			}
			
			int deltY = y1-y0;
			int deltX = x1-x0;
			int increment = 1;
			if(x1-x0<0){
				increment = -1;
				deltX = -deltX;
			}
			int e = 2 * deltY - deltX;
			int inc1 = 2 * deltX;
			int inc2 = 2 * (deltX - deltY);
			int y = y0;
			int x = x0;
			do
			{
				doAtPixel(event,x,y);
				if(e < 0)
				{
					e = e + inc1;
				} else {
					x = x + increment;
					e = e + inc2;
				}
				y++;
			}while (y < y1);
		}
	}
	
	public void doAtPixel(MouseEvent e, int x, int y){
		if((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) !=0){
			graphics.setColor(color1);
			graphics.fillRect(x,y,size,size);
			processor.setImageHolder(holder);
		} else if((e.getModifiersEx() & MouseEvent.BUTTON3_DOWN_MASK) !=0){
			graphics.setColor(color2);
			graphics.fillRect(x,y,size,size);
			processor.setImageHolder(holder);
		}
	}

}

