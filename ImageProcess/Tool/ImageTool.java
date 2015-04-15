package ImageProcess.Tool;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JPopupMenu;
import javax.swing.JPanel;
import javax.swing.JColorChooser;
import javax.swing.BorderFactory;
import ImageProcess.ImageHolder;
import java.awt.Color;
import javax.swing.JTextArea;
import ImageProcess.Processor;

public class ImageTool implements MouseListener, MouseMotionListener, ChangeListener{
	boolean enabled = true;
	ImageHolder holder = null;
	public static Processor processor = null;
	static Color color1 = Color.BLACK;
	static Color color2 = Color.RED;
	
	JColorChooser chooser1 = null;
	JColorChooser chooser2 = null;
	
	public static JPopupMenu contextMenu;
	public static JTextArea debugConsole;
	
	public ImageTool(){
		chooser1 = new JColorChooser(color1);
		chooser1.getSelectionModel().addChangeListener(this);
		chooser1.setBorder(BorderFactory.createTitledBorder("Primary Color"));
		
		chooser2 = new JColorChooser(color2);
		chooser2.getSelectionModel().addChangeListener(this);
		chooser2.setBorder(BorderFactory.createTitledBorder("Secondary Color"));
	}
	
	public static void setContextMenu(JPopupMenu menu){
		contextMenu = menu;
	}
	
	public static void debugPrint(String s){
		debugConsole.append(s);
	}
	
	public static void debugPrintln(String s){
		debugConsole.append(s);
		debugConsole.append("\n");
	}
	
	public void setImageHolder(ImageHolder imageHolder){
		holder = imageHolder;
	}
	
	public void enable(){
		enabled = true;
	}
	
	public void disable(){
		enabled = false;
	}
	
	public JPanel createOptionsPanel(){
		return null;
	}
	
	public void stateChanged(ChangeEvent e) {
		color1 = chooser1.getColor();
		color2 = chooser2.getColor();
	}
	
	public void mousePressed(MouseEvent e){
		showContextMenu(e);
	}

	public void mouseReleased(MouseEvent e){
		showContextMenu(e);
	}
	
	public void mouseExited(MouseEvent e){
		
	}
	
	public void mouseEntered(MouseEvent e){
		
	}
	
	public void mouseClicked(MouseEvent e){
		
	}
	
	public void mouseDragged(MouseEvent e){
		
	}
	
	public void mouseMoved(MouseEvent e){
		
	}

	private boolean showContextMenu(MouseEvent e){
		if(enabled && e.isPopupTrigger()){
			contextMenu.show(e.getComponent(), e.getX(), e.getY());
			return true;
		}
		return false;
	}
}

