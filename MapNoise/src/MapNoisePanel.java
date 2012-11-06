import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;


public class MapNoisePanel extends JPanel {
	private BufferedImage mainImg, dotImg;
	private int numDots, mov, dist, size;
	private int posx, posy, movx, movy;
	private Timer timer;
	private ArrayList<Location> filled, not;
	public static int WIDTH = 700;
	public static int HEIGHT = 500;
	private static int MAXDIS = 250;
	
	public MapNoisePanel() {
		dotImg = new BufferedImage(WIDTH + MAXDIS, HEIGHT + MAXDIS, BufferedImage.TYPE_INT_ARGB);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		size = 2;
		filled = new ArrayList<Location>();
		not = new ArrayList<Location>();
		for (int a = 0; a < WIDTH + MAXDIS; a += size)
			for (int b = 0; b < HEIGHT + MAXDIS; b += size)
				not.add(new Location(a, b));
		dist = 20;
		timer = new Timer(1, new ActionListener() {
			public void actionPerformed(ActionEvent e) { move(); }
		});
		posx = 0;
		posy = 0;
		mov = 4;
		genMove();
		changeDots(10000);
	}
	
	public MapNoisePanel(BufferedImage img) {
		this();
		if (img != null)
			setImage(img);
	}
	
	public MapNoisePanel(BufferedImage img, int numDots) {
		this(img);
		changeDots(numDots);
	}
	
	public MapNoisePanel(BufferedImage img, int numDots, int speed, int maxDistance) {
		this(img, numDots);
		mov = speed;
		dist = maxDistance;
	}
	
	public void setDistance(int maxDistance) {
		dist = maxDistance;
	}
	
	public void setSpeed(int speed) {
		mov = speed;
	}
	
	public void setImage(Image img) {
		double scale = Math.min((double)(WIDTH - 50) / img.getWidth(this), (double)(HEIGHT - 50) / img.getHeight(this));
		int w = (int)(img.getWidth(this) * scale) ;
		int h = (int)(img.getHeight(this) * scale);
		Image i = img.getScaledInstance(w, h, Image.SCALE_DEFAULT);
		mainImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = mainImg.createGraphics();
		g.drawImage(i, 0, 0, w, h, null);
		for (int a = 0; a < w; a++)
			for (int b = 0; b < h; b++)
			{
				Color c = new Color(mainImg.getRGB(a, b));
				
				if (c.getRed() + c.getBlue() + c.getGreen() < 256 && c.getAlpha() > 200)
					mainImg.setRGB(a, b, 0xFF000000);
				else
					mainImg.setRGB(a, b, 0x00000000);
			}
		repaint();
	}
	
	public void setScaledImage(BufferedImage img) {
		mainImg = img;
	}
	
	public void setSize(int si) {
		if (si == size)
			return;
		int dots = size * size * numDots / (si * si);
		clearImage();
		size = si;
		filled = new ArrayList<Location>();
		not = new ArrayList<Location>();
		for (int a = 0; a <= WIDTH + MAXDIS - size; a += size)
			for (int b = 0; b <= HEIGHT + MAXDIS - size; b += size)
				not.add(new Location(a, b)); 
		changeDots(dots * size * size);
	}
	
	public void clearImage() {
		numDots = 0;
		dotImg = new BufferedImage(dotImg.getWidth(), dotImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
	}
	
	public void changeDots(int newDots) {
		newDots /= size * size;
		if (newDots > (WIDTH + MAXDIS) / size * (HEIGHT + MAXDIS) / size)
			return;
		
		if (numDots > newDots)
			while (numDots > newDots)
			{
				int in = (int)(Math.random() * filled.size());
				int[] pix = new int[size * size];
				for (int p = 0; p < size * size; p++)
					pix[p] = 0x00000000;
				dotImg.setRGB(filled.get(in).x, filled.get(in).y, size, size, pix, 0, size);
				repaint(new Rectangle(filled.get(in).x, filled.get(in).y, size, size));
				not.add(filled.get(in));
				filled.remove(in);
				numDots--;
			}
		else
		{
			while (numDots < newDots)
			{
				int in = (int)(Math.random() * not.size());
				int[] pix = new int[size * size];
				for (int p = 0; p < size * size; p++)
					pix[p] = 0xFF000000;
				dotImg.setRGB(not.get(in).x, not.get(in).y, size, size, pix, 0, size);
				repaint(new Rectangle(not.get(in).x, not.get(in).y, size, size));
				filled.add(not.get(in));
				not.remove(in);
				numDots++;
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (mainImg != null) {
			g2.drawImage(mainImg, null, (WIDTH - mainImg.getWidth()) / 2, (HEIGHT - mainImg.getHeight()) / 2);
		}
		g2.drawImage(dotImg, null, posx - (MAXDIS / 2), posy - (MAXDIS / 2));
	}
	
	public void move() {
		if (Math.abs(posx + movx) > dist || Math.abs(posy + movy) > dist)
			genMove();
		posx += movx;
		posy += movy;
		repaint();
	}
	
	public void genMove() {
		movx = (int)(Math.sqrt(Math.random() * mov * mov));
		movy = (int)(Math.sqrt(mov * mov - movx * movx));
		if (posx > dist)
			movx = -movx;
		if (posy > dist)
			movy = -movy;;
	}
	
	public void toggle() {
		if (timer.isRunning())
			timer.stop();
		else
			timer.restart();
	}
}
