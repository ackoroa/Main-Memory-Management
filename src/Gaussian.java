public class Gaussian {
	/* used for uniform random variable (0.1) */
	final static double A = 16807.0; /* multiplier */
	final static double M = 2147483647.0; /* modulus */
	static double r_seed = 1.0; /* seed number */
	static double t = 0.0;

	/************************************************************
	 * setSeed -- set the seed *
	 ************************************************************/
	public void setSeed(double s) {
		r_seed = s % M;
	}

	/************************************************************
	 * rnd -- generates a uniform random variable (0, 1) *
	 ************************************************************/
	public double rnd() {
		r_seed = (A * r_seed) % M;
		return (r_seed * 4.656612875e-10);
	}

	/************************************************************
	 * gaussian -- generates a Gaussian random variable * with mean and standard
	 * deviation d *
	 ************************************************************/
	public double gaussian(double a, double d) {
		double x, v1, v2, r;

		if (t == 0.0) {
			do {
				v1 = 2.0 * rnd() - 1.0;
				v2 = 2.0 * rnd() - 1.0;
				r = v1 * v1 + v2 * v2;
			} while (r >= 1.0);
			r = Math.sqrt((-2.0 * Math.log(r)) / r);
			t = v2 * r;
			return (a + v1 * r * d);
		} else {
			x = t;
			t = 0.0;
			return (a + x * d);
		}
	}

}
