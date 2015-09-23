import java.awt.Color;

public class SeamCarver {

	private Picture picture;
	private int width;
	private int height;
	private double [][]pictureEnergy;
	private Color [][]pictureColor;
	private int [][]path;
	private double [][]SP;
	
	private boolean vertical;

	public SeamCarver(Picture picture) {
		this.picture = picture;
		this.width = picture.width();
		this.height = picture.height();
		vertical = true;
		pictureEnergy = new double[height][width];
		pictureColor = new Color[height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//pictureEnergy[i][j] = findEnergy(j, i);
				pictureColor[i][j] = this.picture.get(j,  i);
			}
		}
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				pictureEnergy[i][j] = findEnergy(j, i);
				//pictureColor[i][j] = this.picture.get(j,  i);
			}
		}
		
	}

	public Picture picture() {                       // current picture
		return picture;
	}

	public int width()  {
		// width  of current picture
		return width;
	}

	public int height() {
		// height of current picture
		return height;
	}
	
	public double energy(int x, int y) {
		// energy of pixel at column x and row y in current picture
		if (x < 0 || x >= width || y < 0 || y >= height) 
			throw new java.lang.IndexOutOfBoundsException();
		return pictureEnergy[y][x];
	}

	private double findEnergy(int x, int y) {
		// energy of pixel at column x and row y in current picture
		// if the pixel is on the border, return 195075
		if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return 195075;

		else {
			Color left = pictureColor[y][x - 1]; //picture.get(x - 1, y);
			Color right = pictureColor[y][x + 1]; //picture.get(x + 1, y);
			Color above = pictureColor[y + 1][x]; //picture.get(x,  y + 1);
			Color below = pictureColor[y - 1][x]; //picture.get(x, y - 1);

			double deltaXSquare = Math.pow((right.getRed() - left.getRed()), 2) 
					+ Math.pow((right.getBlue() - left.getBlue()), 2) 
					+ Math.pow((right.getGreen() - left.getGreen()), 2);

			double deltaYSquare = Math.pow((above.getRed() - below.getRed()), 2) 
					+ Math.pow((above.getBlue() - below.getBlue()), 2) 
					+ Math.pow((above.getGreen() - below.getGreen()), 2);

			return deltaXSquare + deltaYSquare;
		}
	}

	public int[] findVerticalSeam() {
		// sequence of indices for vertical seam in current picture	
		if (!vertical) transpose();
		return findSeam();
	}
	
	public int[] findHorizontalSeam() {
		// sequence of indices for horizontal seam in current picture
		if (vertical) transpose();
		return findSeam();
	}

	private int[] findSeam() {
		
		path = new int[height][width];
		SP = new double[height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				//pictureEnergy[i][j] = energy(j, i);
				SP[i][j] = Double.POSITIVE_INFINITY;
			}
		}

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (i == 0) {
					SP[i][j] = pictureEnergy[i][j];
					//SP[i][j] = energy(j, i);
					path[i][j] = j;
				}
				else {
					findSP(j, i);
				}
			}
		}

		int minIndex = 0;
		double minWeight = Double.POSITIVE_INFINITY;
		for (int i = 0; i < width; i++) {
			if (SP[height - 1][i] < minWeight) {
				minWeight = SP[height - 1][i];
				minIndex = i;
			}
		}

		return buildPath(minIndex);
	}

	private void findSP(int x, int y) {
		// calculate minimum sum of energy from the top to pixel at column x and row y
		if (x == 0) {
			double weight0 = SP[y - 1][x] + pictureEnergy[y][x];
			double weight1 = SP[y - 1][x + 1] + pictureEnergy[y][x];
			if (weight0 < weight1) { 
				SP[y][x] = weight0; 
				path[y][x] = x; 
			}

			else {
				SP[y][x] =  weight1; 
				path[y][x] = x + 1;
			}
		}

		else if (x == width - 1) {
			double weight0 = SP[y - 1][x] + pictureEnergy[y][x];
			double weight1 = SP[y - 1][x - 1] + pictureEnergy[y][x];
			if (weight0 < weight1) {
				SP[y][x] = weight0;
				path[y][x] = x;
			}
			else { 
				SP[y][x] = weight1;
				path[y][x] = x - 1;
			}
		}

		else {
			double weight0 = SP[y - 1][x - 1] + pictureEnergy[y][x];
			double weight1 = SP[y - 1][x] + pictureEnergy[y][x];
			double weight2 = SP[y - 1][x + 1] + pictureEnergy[y][x];
			double []weight = {weight0, weight1, weight2};
			int []index = {-1, 0, 1};
			for (int i = 0; i < 3; i++) {
				if (weight[i] < SP[y][x]) {
					SP[y][x] = weight[i];
					path[y][x] = x + index[i];
				}
			}

		}
	}

	private int[] buildPath(int minIndex) {
		int []seam = new int[height];
		int i = height - 1;
		while (i >= 0) {
			seam[i] = minIndex;
			minIndex = path[i][minIndex];
			i--;
		}
		return seam;
	}
	
	private void transpose() {
		if (vertical) vertical = false;
		else vertical = true;
		int temp = width;
		width = height;
		height = temp;
		
		double [][] pictureEnergyT = new double[height][width];
		Color [][] pictureColorT = new Color[height][width];
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				pictureEnergyT[i][j] = pictureEnergy[j][i];
				pictureColorT[i][j] = pictureColor[j][i];
			}
		}
		
		pictureEnergy = pictureEnergyT;
		pictureColor = pictureColorT;
	}


	public void removeHorizontalSeam(int[] a) {
		// remove horizontal seam from current picture

		if (vertical) transpose();
		removeSeam(a);
		transpose();
		Picture pictureN = new Picture(width, height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				pictureN.set(j, i, pictureColor[i][j]);
			}
		}
		picture = pictureN;

	}

	public void removeVerticalSeam(int[] a) {
		// remove vertical   seam from current picture
		
		if (!vertical) transpose();
		removeSeam(a);
		Picture pictureN = new Picture(width, height);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				pictureN.set(j, i, pictureColor[i][j]);
			}
		}

		picture = pictureN;
	}
	
	private boolean checkValidity(int[] a) {
		//boolean valid = true;
		if (a.length > height) return false;
		
		for (int i = 1; i < a.length; i++) {
			if (a[i] - a[i - 1] != -1 && a[i] - a[i - 1] != 0 && a[i] - a[i - 1] != 1)
				return false;
			if (a[i] < 0 || a[i] >= width) return false;
		}
		
		return true;
	}
	
	private void removeSeam(int[] a) {
		
		if (!checkValidity(a))
			throw new java.lang.IllegalArgumentException();
		
		Color [][]pictureColorN = new Color[height][width - 1];
		double [][]pictureEnergyN = new double[height][width - 1];
		
		for (int i = 0; i < height; i++) {
			System.arraycopy(pictureColor[i], 0, pictureColorN[i], 0, a[i] - 0);
			System.arraycopy(pictureColor[i], a[i] + 1, pictureColorN[i], a[i], width - 1 - a[i]);
			System.arraycopy(pictureEnergy[i], 0, pictureEnergyN[i], 0, a[i] - 0);
			System.arraycopy(pictureEnergy[i], a[i] + 1, pictureEnergy[i], a[i], width - 1 - a[i]);
		}
		
		pictureColor = pictureColorN;
		pictureEnergy = pictureEnergyN;
		width--;
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				pictureEnergy[i][j] = findEnergy(j, i);
			}
		}
		
		/*
		for (int i = 1; i < height - 1; i++) {
			if (a[i] < width && a[i] > 0) {
				pictureEnergy[i][a[i]] = findEnergy(a[i], i);
				pictureEnergy[i][a[i] - 1] = findEnergy(a[i] - 1, i);
				//pictureEnergy[i + 1][a[i]] = findEnergy(a[i], i + 1);
				//pictureEnergy[i - 1][a[i]] = findEnergy(a[i], i - 1);
			}
			else if ( a[i] == width) {
				pictureEnergy[i][a[i] - 1] = findEnergy(a[i] - 1, i);
				//pictureEnergy[i + 1][a[i] - 1] = findEnergy(a[i] - 1, i + 1);
				//pictureEnergy[i - 1][a[i] - 1] = findEnergy(a[i] - 1, i - 1);
			}
			
			else {
				pictureEnergy[i][a[i]] = findEnergy(a[i], i);
				//pictureEnergy[i][a[i] - 1] = findEnergy(a[i] - 1, i);
				//pictureEnergy[i + 1][a[i]] = findEnergy(a[i], i + 1);
				//pictureEnergy[i - 1][a[i]] = findEnergy(a[i], i - 1);
			
			}
			
		}
		*/	
	}

	public static void main(String []args) {
		Picture a = new Picture("12x10.png");
		SeamCarver sc = new SeamCarver(a);
		
		int []seam = sc.findVerticalSeam();
		
		sc.picture.show();
		
		for (int i = 0; i < 2; i++) {
			seam = sc.findVerticalSeam();
			sc.removeVerticalSeam(seam);
		}
		
		sc.picture.show();

	}
}