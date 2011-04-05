package filters.edge;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import filters.InputImage;
import filters.PluginHelper;

public class FindEdge {
	private BufferedImage source;
	private BufferedImage edge;
	private EdgeMatrix matrix;
	private int[][] grayImg;
	
	private int range;
	private double sens;

	private boolean single;
	private int iterations;
	
	public FindEdge(InputImage img, PluginHelper h, int se, int r, int iter, boolean sin, boolean hasbg) {
		source = img.getSourceImage();
		edge = new BufferedImage(source.getWidth(),source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		single = sin;
		iterations = iter;
		//edge = new BufferedImage(source.getWidth(),source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		grayImg = new int[source.getWidth()][source.getHeight()];
		fillGray();
		
		float d = (float)(r - 2) / (iter - 1);
		for (int i = 0; i < iter; i++) {
			System.out.println((i + 1) + " / " + iter + ": range " + r);
			run(r, se, i + 1);
			r -= d;
			r = r > 2 ? r : 2;
		}
		
		BufferedImage bg = h.newImage(source.getWidth(), source.getHeight()).getSourceImage();
		Graphics2D graphics = bg.createGraphics();
		Color bgc;
		if (hasbg) {
			bgc = new Color(255, 255, 255, 255);
		} else {
			bgc = new Color(255, 255, 255, 0);
		}
		graphics.drawImage(edge, 0, 0, bgc, null);
	}
	
	private void run(int r, int se, int k) {
		System.gc();
		range = r;
		matrix = new EdgeMatrix(range, source.getWidth(), source.getHeight(), se * Math.sqrt(k));
		sens = se * Math.sqrt(k) * matrix.sum();
		
		detect();	
	}
	
	private void detect() {
		int all = source.getWidth() - range - 1;
		int remain = all;
		int procPre = -1;
		
		for (int i = range + 1; i < source.getWidth() - range - 1; i += 1) {
			for (int j = range + 1; j < source.getHeight() - range - 1; j += 1) {			
				int alpha = diff(i, j);
				if (alpha != 0) {
					edge.setRGB(i, j, alpha);
				}
			}
			
			remain--;
			int proc = 100 - ((100 * remain) / all);
			if (proc % 10 == 0 && procPre != proc && proc != 100) {
				System.out.print(proc + "% ");
				procPre = proc;
			}
			
		}
		System.out.println("100%");
	}
	
	private int diff(int x, int y) {
		x -= range + 1;
		y -= range + 1;
		
		double angles[] = new double[4];
		double max = -1;
		boolean print = false;
		
		for (int type = 0; type < 4; type++) {
			double d1 = matrix.diff(grayImg, x, y, 1, type);
			double d2 = 1;
			double d3 = 1;
			if (single) {
				d2 = matrix.diff(grayImg, x, y, 0, type);
				d3 = matrix.diff(grayImg, x, y, 2, type);
			}
			
			angles[type] = d1;
			
			if (d1 > 0 && d2 > 0 && d3 > 0 && d1 > d2 && d1 > d3 && d1 > sens) {
				if (d1 > max) max = d1;
				print = true;
			}
		}
		if (!print || max <= 0) return 0;

		int alpha = getAlpha(max, x, y);
		int color = getColor(angles);
		
		return alpha << 24 | color;
	}
	
	private int getColor(double angles[]) {
		int color = (int)(Math.atan(angles[0] / angles[1]) * 180 / Math.PI * 1.3);
		if (color < 0) color *= -1;
		
		if (color < 0 || color > 255)
			System.out.println(color + " " + angles[0] + " " + angles[1]);
		
		if (angles[2] < angles[3]) color <<= 16;
			
		return color;
	}
	
	private int getAlpha(double max, int x, int y) {
		int alpha = (int)((1 - sens / max) * 255) * 2 + 50;
		if (!single) alpha /= iterations * ((float)range / 10);
		alpha += edge.getRGB(x, y) >> 24 & 0xFF;
		alpha = alpha > 255 ? 255 : alpha;
		return alpha;	
	}
	
	private void fillGray() {
		for (int i = 0; i < source.getWidth(); i += 1) {
			for (int j = 0; j < source.getHeight(); j += 1) {
				grayImg[i][j] = gray(source, i, j);
			}
		}
	}
	
	private int gray(BufferedImage s, int x, int y) {
		//System.out.println(x + " " + y);
		int rgb = s.getRGB(x, y);
		int gray = rgb & 0xFF;
		rgb >>= 8;
		gray += rgb & 0xFF;
		rgb >>= 8;
		gray += rgb & 0xFF;

		return gray / 3;
	}
	
	
	
	public void save(String fn) {
		try {
			ImageIO.write(edge, "png", new File(fn));
			System.out.println(fn + " saved");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
