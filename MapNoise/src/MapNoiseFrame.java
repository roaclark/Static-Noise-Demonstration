import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;


public class MapNoiseFrame extends JFrame implements ActionListener
{
	JSlider density, speed, distance, size;
	JButton toggle, pickImage;
	MapNoisePanel imagePanel;
	JFileChooser cho;
	
	public MapNoiseFrame()
	{
		super("Noise Demonstration");
		setSize(700, 600);
		setLocation(50, 50);

		int md = MapNoisePanel.WIDTH * MapNoisePanel.HEIGHT;
		imagePanel = new MapNoisePanel(null, (md - 1000) * 4 / 5, 5, 70);
		
		cho = new JFileChooser();
		
		cho.setAcceptAllFileFilterUsed(false);		
		cho.addChoosableFileFilter(new FileFilter() {
			public String getDescription() {
				return "JPG, JPEG, and PNG Images";
			}
			
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				
				String ex = getExtension(f);
				if (ex.equals("jpg") || ex.equals("jpeg") || ex.equals("png"))
					return true;
				return false;
			}
		});
		cho.addChoosableFileFilter(new FileFilter() {
			public String getDescription() {
				return "TXT Files";
			}
			
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				
				String ex = getExtension(f);
				if (ex.equals("txt"))
					return true;
				return false;
			}
		});
		
		density = new JSlider(0, md - 1000, (md - 1000) * 4 / 5);
		density.addChangeListener(new ChangeListener() { public void stateChanged(ChangeEvent e) {
				if (imagePanel != null)
					imagePanel.changeDots(density.getValue());
			}
		});
		speed = new JSlider(0, 15, 5);
		speed.addChangeListener(new ChangeListener() { public void stateChanged(ChangeEvent e) {
				if (imagePanel != null)
					imagePanel.setSpeed(speed.getValue());
			}
		});
		distance = new JSlider(0, 100, 70);
		distance.addChangeListener(new ChangeListener() { public void stateChanged(ChangeEvent e) {
			if (imagePanel != null)
				imagePanel.setDistance(distance.getValue());
			}
		});
		size = new JSlider(1, 5, 2);
		size.addChangeListener(new ChangeListener() { public void stateChanged(ChangeEvent e) {
				if (imagePanel != null)
					imagePanel.setSize(size.getValue());
			}
		});
		
		JPanel settings = new JPanel();
		settings.setPreferredSize(new Dimension(600, 100));
		
		settings.setLayout(new GridLayout(0, 5));
		settings.setPreferredSize(new Dimension(600, 50));

		settings.add(density);
		settings.add(new JLabel("Density"));
		settings.add(size);
		settings.add(new JLabel("Size"));

		pickImage = new JButton("Image");
		pickImage.addActionListener(this);
		settings.add(pickImage);
		
		settings.add(distance);
		settings.add(new JLabel("Distance"));
		settings.add(speed);
		settings.add(new JLabel("Speed"));

		toggle = new JButton("Start");
		toggle.addActionListener(this);
		settings.add(toggle);
		
		setLayout(new BorderLayout());
		add(imagePanel, BorderLayout.NORTH);
		add(settings, BorderLayout.SOUTH);
		
		repaint();
		
		selectImage();
	}
	
	public void selectImage() {
		BufferedImage img = null;
		if (cho.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
			try {
				File f = cho.getSelectedFile();
				if (getExtension(f).equals("txt")) {
					setVisible(false);
					new ImageFrame(f, this);
				}
				else {
					img = ImageIO.read(f);
					setVisible(true);
				}
			} catch (IOException e) {
				setVisible(true);
				System.out.println(e);
				System.out.println("Image error.");
			}
		
		if (img != null)
			imagePanel.setImage(img);
	}
	
	public void setImage(BufferedImage img) {
		imagePanel.setScaledImage(img);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == toggle) {
			imagePanel.toggle();
			if (toggle.getText().equals("Start"))
				toggle.setText("Stop");
			else
				toggle.setText("Start");
		}
		else if (e.getSource() == pickImage) {
			selectImage();
		}
	}
	
	public String getExtension(File f) {
		String n = f.getName();
		int i = n.lastIndexOf('.');
		String s = "";
		if (i > 0 && i < n.length() - 1)
			s = n.substring(i + 1).toLowerCase();
		return s;
	}
}
