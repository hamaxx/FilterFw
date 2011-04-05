package filters.edge;

public class EdgeMatrix {
	private double[][] mat;
	private int d;
	private double[][][] cache;
	private double sum = -1;
	private double sens;
	
	private EdgeMatrix smallMat;
	
	public EdgeMatrix(int r, int w, int h, double s) {
		this(r, w, h, s, false);
	}
	
	protected void finalize() {
		cache = null;
		mat = null;
		smallMat = null;
	}
	
	public EdgeMatrix(int r, int w, int h, double s, boolean small) {
		d = 2 * r + 1;
		mat = new double[d][d];
		sens = s;
		fillMat();
		cache = new double[4][w][h];
		
		sum = sum();
		
		if (!small && r >= 4) {
			smallMat = new EdgeMatrix(r / 2, w, h, s, true);
		}
	}
	
	public double sum() {
		if (sum > 0) return sum;
		sum = 0;
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				sum += mat[i][j];
			}
		}
		return sum;
	}
	
	private double gauss(double x) {
		double o = 1;
		
		double g = 1.0 / Math.sqrt(2 * Math.PI * Math.pow(o, 2));
		g *= Math.pow(Math.E, -1 * Math.pow(x, 2));
		
		return g;
	}
	
	private void fillMat() {
		int s = d / 2;
		double maxd = Math.sqrt(Math.pow(s, 2) + Math.pow(s, 2));
		
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				double g = Math.sqrt(Math.pow(i - s, 2) + Math.pow(j - s, 2));
				g = g / maxd * 3;
				mat[i][j] = g == 0 ? 0 : gauss(g);
			}
		}
	}
	
	private boolean isIn(int x, int y, int type, boolean reverse) {
		boolean ok = false;
		if (type == 0) {	//hor
			ok = reverse ? x > d / 2 : x < d / 2;
		} else if (type == 1) {	//ver
			ok = reverse ? y > d / 2 : y < d / 2;
		} else if (type == 2) {	//bottom right
			ok = reverse ? x > y : x < y;
		} else if (type == 3) {	//top left
			ok = reverse ? d - 1 - x > y : d - 1 - x < y;
		}
		
		return ok;
	}
	
	public double diff(int[][] img, int ox, int oy, int offset, int type) {
		if (type == 0) {		//hor
			ox += offset;
			oy += 1;
		} else if (type == 1) {	//ver
			ox += 1;
			oy += offset;
		} else if (type == 2) {	//bottom right
			ox += offset;
			oy += offset;
		} else if (type == 3) {	//top left
			ox += offset;
			oy += 2 - offset;
		}
		
		if (cache[type][ox][oy] == 0) {
			cache[type][ox][oy] = Math.abs(score(img, ox, oy, type, false) - 
					score(img, ox, oy, type, true));
		}
		
		if (smallMat != null) {
			double small = smallMat.diff(img, ox, oy, offset, type);
			if (small < smallMat.sum() * sens / 10) return -1;
			//return -1;
		}
		
		return cache[type][ox][oy];
	}
	
	private double score(int[][] img, int ox, int oy, int type, boolean reverse) {
		double score = 0;
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				if (isIn(i, j, type, reverse)) {
					score += img[i + ox][j + oy] * mat[i][j];
				}
			}
		}
		return score;
	}
	
	public void print() {
		double sum = 0;
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				sum += mat[i][j];
				System.out.printf("%.3f ", mat[i][j]);
			}
			System.out.println();
		}
		System.out.println(sum);
	}
	
}
