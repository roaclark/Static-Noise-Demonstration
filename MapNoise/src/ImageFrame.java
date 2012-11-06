import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ImageFrame extends JFrame {
	private ArrayList<Location> spots;
	private int size, width, height;
	private BufferedImage img;
	private JPanel imgPanel;
	
	public ImageFrame(File file, final MapNoiseFrame fr) {
		super();
		setSize(550, 625);
		setLocation(50, 50);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		imgPanel = new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.drawImage(img, null, (500 - img.getWidth()) / 2, (500 - img.getHeight()) / 2);
			}
		};
		imgPanel.setPreferredSize(new Dimension(500, 500));
		
		spots = new ArrayList<Location>();
		textToLocation(file);
		
		final JSlider size = new JSlider(1, Math.min(img.getWidth() / width, img.getHeight() / height));
		size.addChangeListener(new ChangeListener() { public void stateChanged(ChangeEvent e) {
				changeSize(size.getValue());
			}
		});
		changeSize(size.getValue());
		
		JButton done = new JButton("Done");
		done.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
				fr.setImage(img);
				fr.setVisible(true);
				dispose();
			}
		});
		
		setLayout(new BorderLayout());
		add(imgPanel, BorderLayout.NORTH);
		JPanel south = new JPanel();
		south.setPreferredSize(new Dimension(500, 75));
		south.add(size);
		south.add(done);
		add(south, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	public void textToLocation(File f) {
		Scanner in = null;
		try {
			in = new Scanner(f);
		} catch (Exception e) {
			return;
		}
		
		ArrayList<String> arr = new ArrayList<String>();
		while (in.hasNextLine())
			arr.add(in.nextLine());
		in.close();
		
		width = 0;
		height = 0;
		for (String s: arr) {
			if (s.length() > width)
				width = s.length();
			height++;
		}
		
		for (int a = 0; a < height; a++) {
			String s = arr.get(a);
			for (int b = 0; b < s.length(); b++)
				if (s.charAt(b) != ' ')
					spots.add(new Location(b, a));
		}
		
		double scale = 500.0 / (Math.max(width, height));
		img = new BufferedImage((int)(width * scale), (int)(height * scale), BufferedImage.TYPE_INT_ARGB);
	}
	
	public void changeSize(int newSize) {
		int max = Math.max(newSize, size);
		int dif = Math.abs(newSize - size);
		int[] pix = new int[max * max];
		if (newSize > size)
			for (int i = 0; i < pix.length; i++)
				pix[i] = 0xFF000000;
		else
			for (int a = dif; a < max - dif; a++)
				for (int b = dif; b < max - dif; b++)
					pix[a * max + b] = 0xFF000000;
		
		for (Location l: spots) {
			int x = (int)((l.x + 0.5) * img.getWidth() / width);
			int y = (int)((l.y + 0.5) * img.getHeight() / height);
			img.setRGB(x - max / 2, y - max/2, max, max, pix, 0, max);
			imgPanel.repaint();
		}
		
		size = newSize;
	}
}