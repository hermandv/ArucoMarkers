package common;

public class HCLColor {

	static final public double Y0 = 100;
	static final public double gamma = 3;
	static final public double Al = 1.4456;
	static final public double Ach_inc = 0.16;

	public static double[] rgb2hcl(int rgb) {
		return rgb2hcl((int) Utils.red(rgb), (int) Utils.green(rgb), (int) Utils.blue(rgb));
	}

	public static double[] rgb2hcl(int r, int g, int b) {
		double[] returnarray = new double[3];
		double min = Math.min(Math.min(r, g), b);
		double max = Math.max(Math.max(r, g), b);
		if (max == 0) {
			returnarray[0] = 0;
			returnarray[1] = 0;
			returnarray[2] = 0;
			return (returnarray);
		}

		double alpha = (min / max) / Y0;
		double Q = Math.exp(alpha * gamma);
		double rg = r - g;
		double gb = g - b;
		double br = b - r;
		double L = ((Q * max) + ((1 - Q) * min)) / 2;
		double C = Q * (Math.abs(rg) + Math.abs(gb) + Math.abs(br)) / 3;
		double H = Math.toDegrees(Math.atan2(gb, rg));

		/*
		 * //the formulae given in paper, don't work. if (rg >= 0 && gb >= 0) { H = 2 *
		 * H / 3; } else if (rg >= 0 && gb < 0) { H = 4 * H / 3; } else if (rg < 0 && gb
		 * >= 0) { H = 180 + 4 * H / 3; } else if (rg < 0 && gb < 0) { H = 2 * H / 3 -
		 * 180; } // 180 causes the parts to overlap (green == red) and it oddly
		 * crumples up bits of the hue for no good reason. 2/3H and 4/3H expanding and
		 * contracting quandrants.
		 */

//		if (rg >= 0 && gb >= 0) {
//			H = 2 * H / 3;
//		} else if (rg >= 0 && gb < 0) {
//			H = 4 * H / 3;
//		} else if (rg < 0 && gb >= 0) {
//			H = 180 + 4 * H / 3;
//		} else if (rg < 0 && gb < 0) {
//			H = 2 * H / 3 - 180;
//		}

//		if (rg < 0) {
//			if (gb >= 0)
//				H = 90 + H;
//			else {
//				H = H - 90;
//			}
//		} // works

		returnarray[0] = H;
		returnarray[1] = C;
		returnarray[2] = L;
		return (returnarray);

	}

	public double cycldistance(double[] hcl1, double[] hcl2) {
		double dL = hcl1[2] - hcl2[2];
		double dH = Math.abs(hcl1[0] - hcl2[0]);
		double C1 = hcl1[1];
		double C2 = hcl2[1];
		return Math.sqrt(dL * dL + C1 * C1 + C2 * C2 - 2 * C1 * C2 * Math.cos(Math.toRadians(dH)));
	}

	public double distance_hcl(double[] hcl1, double[] hcl2) {
		double c1 = hcl1[1];
		double c2 = hcl2[1];
		double Dh = Math.abs(hcl1[0] - hcl2[0]);
		if (Dh > 180)
			Dh = 360 - Dh;
		double Ach = Dh + Ach_inc;
		double AlDl = Al * Math.abs(hcl1[2] - hcl2[2]);
		return Math.sqrt(AlDl * AlDl + (c1 * c1 + c2 * c2 - 2 * c1 * c2 * Math.cos(Math.toRadians(Dh))));
		// return Math.sqrt(AlDl * AlDl + Ach * (c1 * c1 + c2 * c2 - 2 * c1 * c2 *
		// Math.cos(Math.toRadians(Dh))));
	}
}