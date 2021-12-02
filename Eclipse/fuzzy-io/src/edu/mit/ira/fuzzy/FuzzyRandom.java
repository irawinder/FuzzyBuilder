package edu.mit.ira.fuzzy;

import java.util.ArrayList;
import java.util.Random;

/**
 * FuzzyRandom is class for generating random fuzzy geometry
 *
 * @author Ira Winder
 *
 */
public class FuzzyRandom {

	final int NUM_VERTICES = 4;
	final float PLOT_X = 0;
	final float PLOT_Y = 0;
	final float PLOT_MIN_RADIUS = 300;
	final float PLOT_MAX_RADIUS = 1000;
	final float VOXEL_WIDTH = 25;
	final float VOXEL_HEIGHT = 10;
	final float VOXEL_ROTATION = (float) (0.15 * Math.PI);
	final Point VOXEL_TRANSLATE = new Point(5, 5);
	final float SETBACK_DISTANCE = 100;
	final float TOWER_WIDTH = 100;
	final float TOWER_DEPTH = 200;
	final float TOWER_ROTATION = (float) (0.25 * Math.PI);
	final int NUM_PLOTS = 1;
	final int NUM_TOWERS = 3;

	// How much a given VoxelArray is allowed to dangle into space (0 - 1)
	private float CANTILEVER_ALLOWANCE = 0.5f;

	/**
	 * Build a psuedo-random development without any user input
	 */
	public Development development() {
		int time = millis();

		FuzzyMorph morph = new FuzzyMorph();
		Development development = new Development();

		development.plotShapes.clear();
		development.towerShapes.clear();
		development.site = new VoxelArray();
		development.massing = new VoxelArray();

		for (int i = 0; i < NUM_PLOTS; i++) {

			// Random Plot Shape
			float p_x = PLOT_X; // + random(-1, 1) * PLOT_MAX_RADIUS;
			float p_y = PLOT_Y; // + random(-1, 1) * PLOT_MAX_RADIUS;
			Polygon plotShape = this.polygon(p_x, p_y, NUM_VERTICES, PLOT_MIN_RADIUS, PLOT_MAX_RADIUS);
			development.plotShapes.add(plotShape);

			// Random Tower Base
			ArrayList<Polygon> tShapes = new ArrayList<Polygon>();
			for (int j = 0; j < NUM_TOWERS; j++) {
				float t_x = p_x + random(-.5f, .5f) * PLOT_MIN_RADIUS;
				float t_y = p_y + random(-.1f, .1f) * PLOT_MIN_RADIUS;
				Polygon towerShape = morph.rectangle(new Point(t_x, t_y), TOWER_WIDTH, TOWER_DEPTH, j * TOWER_ROTATION);
				tShapes.add(towerShape);
			}
			development.towerShapes.put(plotShape, tShapes);
		}

		ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();
		for (Polygon plotShape : development.plotShapes) {

			boolean validPlot = true;
			;
			for (Polygon priorPlotShape : builtPlots) {
				if (plotShape.intersectsPolygon(priorPlotShape)) {
					validPlot = false;
					break;
				}
			}

			if (validPlot) {

				builtPlots.add(plotShape);

				time = millis();

				VoxelArray plot = morph.make(plotShape, VOXEL_WIDTH, 0, VOXEL_ROTATION, VOXEL_TRANSLATE);
				development.site = morph.add(development.site, plot);

				VoxelArray plotMassing = new VoxelArray();

				VoxelArray podiumTemplate = morph.hardCloneVoxelArray(plot);
				podiumTemplate = morph.setback(podiumTemplate, SETBACK_DISTANCE);
				podiumTemplate.setVoxelHeight(VOXEL_HEIGHT);

				int pZones = (int) random(1, 4);
				for (int i = 0; i < pZones; i++) {
					int levels = (int) random(1, 5);
					plotMassing = morph.makeAndDrop(podiumTemplate, plotMassing, levels, this.use(),
							CANTILEVER_ALLOWANCE);
				}

				for (Polygon towerShape : development.towerShapes.get(plotShape)) {
					if (plotShape.containsPolygon(towerShape)) {

						VoxelArray towerTemplate = morph.hardCloneVoxelArray(plot);
						towerTemplate.setVoxelHeight(VOXEL_HEIGHT);
						towerTemplate = morph.clip(towerTemplate, towerShape);

						int tZones = (int) random(1, 5);
						for (int i = 0; i < tZones; i++) {
							int levels = (int) random(1, 10);
							plotMassing = morph.makeAndDrop(towerTemplate, plotMassing, levels, this.use(),
									CANTILEVER_ALLOWANCE);
						}
					}
				}

				development.massing = morph.add(development.massing, plotMassing);
			}
		}
		System.out.println("Time to generate: " + (millis() - time) / 1000.0 / (1 / 60.0) + " frames at 60fps");

		return development;
	}

	/**
	 * generates a random polygon shape
	 * 
	 * @param num_pts    number of corners to include in new random polygon
	 * @param x_center   x-coordinate or polygon center
	 * @param y_center   y-coordinate or polygon center
	 * @param min_radius min distance of corner from center point
	 * @param max_radius max distance of corner from center point
	 * @return random polygon
	 */
	public Polygon polygon(float x_center, float y_center, int num_pts, float min_radius, float max_radius) {

		Polygon polygon = new Polygon();
		ArrayList<Float> angle, radius;
		Random rand;
		float total;

		if (num_pts > 2) {

			// Initialize
			angle = new ArrayList<Float>();
			radius = new ArrayList<Float>();
			rand = new Random();
			total = 0;
			polygon.clear();

			for (int i = 0; i < num_pts; i++) {

				// Generate random numbers relatively proportional to angle size
				float random_number = rand.nextFloat();
				total += random_number;
				angle.add(random_number);

				// Generate random radius values
				float variance = (max_radius - min_radius) * rand.nextFloat();
				float random_radius = min_radius + variance;
				radius.add(random_radius);
			}

			// Fit angle size to radian value
			for (int i = 0; i < angle.size(); i++) {
				float mag = angle.get(i);
				angle.set(i, mag * 2 * (float) Math.PI / total);
			}

			// generate each point around a circle
			float a = 0;
			for (int i = 0; i < num_pts; i++) {
				a += angle.get(i);
				float r = radius.get(i);
				float x = (float) (r * Math.cos(a));
				float y = (float) (r * Math.sin(a));
				Point p = new Point(x, y);
				polygon.addVertex(p);
			}

			// shift polygon's coordinate system
			polygon.translate(x_center, y_center);

		} else {
			System.out.print("Not enough points to make polygon");
		}

		return polygon;
	}

	/**
	 * Get a random use
	 */
	public Use use() {
		int index = (int) random(Use.values().length);
		return Use.values()[index];
	}

	// RANDOM NUMBERS
	Random internalRandom;

	/**
	 *
	 */
	public final float random(float high) {
		// avoid an infinite loop when 0 or NaN are passed in
		if (high == 0 || high != high) {
			return 0;
		}

		if (internalRandom == null) {
			internalRandom = new Random();
		}

		// for some reason (rounding error?) Math.random() * 3
		// can sometimes return '3' (once in ~30 million tries)
		// so a check was added to avoid the inclusion of 'howbig'
		float value = 0;
		do {
			value = internalRandom.nextFloat() * high;
		} while (value == high);
		return value;
	}

	/**
	 * ( begin auto-generated from random.xml )
	 *
	 * Generates random numbers. Each time the <b>random()</b> function is called,
	 * it returns an unexpected value within the specified range. If one parameter
	 * is passed to the function it will return a <b>float</b> between zero and the
	 * value of the <b>high</b> parameter. The function call <b>random(5)</b>
	 * returns values between 0 and 5 (starting at zero, up to but not including 5).
	 * If two parameters are passed, it will return a <b>float</b> with a value
	 * between the the parameters. The function call <b>random(-5, 10.2)</b> returns
	 * values starting at -5 up to (but not including) 10.2. To convert a
	 * floating-point random number to an integer, use the <b>int()</b> function.
	 *
	 * ( end auto-generated )
	 * 
	 * @webref math:random
	 * @param low  lower limit
	 * @param high upper limit
	 * @see PApplet#randomSeed(long)
	 * @see PApplet#noise(float, float, float)
	 */
	public final float random(float low, float high) {
		if (low >= high)
			return low;
		float diff = high - low;
		float value = 0;
		// because of rounding error, can't just add low, otherwise it may hit high
		// https://github.com/processing/processing/issues/4551
		do {
			value = random(diff) + low;
		} while (value == high);
		return value;
	}

	long millisOffset = System.currentTimeMillis();

	public int millis() {
		return (int) (System.currentTimeMillis() - millisOffset);
	}
}
