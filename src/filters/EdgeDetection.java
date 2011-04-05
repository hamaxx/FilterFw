package filters;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import filters.edge.FindEdge;


public class EdgeDetection implements filters.FilterPlugin, MouseListener {
	
	public static final int RANGE = 5;
	public static final int SENS = 5;
	public static final int ITER = 3;
	public static final boolean SINGLE = false;
	
	private InputImage image;
	private PluginHelper h;
	
	private JSlider range, sens, iter;
	private JCheckBox art, bg;
	private JButton start;
	
	@Override
	public String name() {
		return "Ham's edge detection";
	}

	@Override
	public void process(InputImage image, PluginHelper h) {
		this.image = image;
		this.h = h;
		setMenu();
	}
	
	private void setMenu() {
		System.out.println("menu");
		JFrame frame = new JFrame(name());
		
		JPanel pane = new JPanel(new GridBagLayout());
		pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		c.gridy = 0;
		pane.add(range(), c);
		
		c.gridy = 1;
		pane.add(sens(), c);
		
		c.gridy = 2;
		pane.add(iter(), c);
		
		c.gridy = 3;
		pane.add(art(), c);
		
		c.gridy = 4;
		pane.add(bg(), c);
		
		c.gridy = 5;
		pane.add(start(), c);
		
		frame.getContentPane().add(pane);
		frame.setVisible(true);
		frame.pack();
	}
	
	public void mouseReleased(MouseEvent e) {
		int se = sens.getValue();
		int ra = range.getValue();
		int it = iter.getValue();
		boolean sl = art.isSelected();
		boolean b = bg.isSelected();
		
		new FindEdge(image, h, se, ra, it, sl, b);		
	}
	
	private JComponent start() {
		start = new JButton("Go");
		start.addMouseListener(this);
		
		return start;
	}

	private JCheckBox art() {
		art = new JCheckBox("Single line");		
		return art;
	}
	
	private JCheckBox bg() {
		bg = new JCheckBox("Add white bg");
		bg.setSelected(true);
		return bg;
	}
	
	private JPanel range() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		p.add(new JLabel("Set radius:"), c);
				
		range = new JSlider(JSlider.HORIZONTAL, 2, 30, RANGE);
		range.setMajorTickSpacing(10);
		range.setPaintTicks(true);
		range.setPaintLabels(true);
		
		c.gridy = 1;
		p.add(range, c);
				
		return p;
	}
	
	private JPanel sens() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		p.add(new JLabel("Set treshold:"), c);
				
		sens = new JSlider(JSlider.HORIZONTAL, 1, 20, SENS);
		sens.setMajorTickSpacing(10);
		sens.setPaintTicks(true);
		sens.setPaintLabels(true);
		
		c.gridy = 1;
		p.add(sens, c);
				
		return p;
	}
	
	private JPanel iter() {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		
		p.add(new JLabel("Set iterations:"), c);
				
		iter = new JSlider(JSlider.HORIZONTAL, 2, 10, ITER);
		iter.setMajorTickSpacing(2);
		iter.setPaintTicks(true);
		iter.setPaintLabels(true);
		
		c.gridy = 1;
		p.add(iter, c);
				
		return p;
	}

	@Override
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
}
