package edu.mit.ira.fuzzy;

import java.util.ArrayList;

/**
 * FuzzyMorph is a class dedicated to functional transformations of geometry
 *
 * @author Ira Winder
 *
 */
class FuzzyMorph {

	// geographic height of ground
	private float GROUND_Z = 0;

	// local w (vertical) coordinate of ground
	private int GROUND_W = 0;

	/**
	 * Inherit the Voxels from a input VoxelArray (child voxels are NOT cloned)
	 * 
	 * @param input A parent VoxelArray
	 */
	public VoxelArray cloneVoxelArray(VoxelArray input) {
		VoxelArray arrayCopy = new VoxelArray();
		for (Voxel t : input.voxelList) {
			arrayCopy.addVoxel(t);
		}
		return arrayCopy;
	}

	/**
	 * Inherit the Voxels from a input VoxelArray (child voxels ARE cloned)
	 * 
	 * @param input A parent VoxelArray
	 */
	public VoxelArray hardCloneVoxelArray(VoxelArray input) {
		VoxelArray arrayCopy = new VoxelArray();
		for (Voxel t : input.voxelList) {
			Voxel clone = this.cloneVoxel(t);
			arrayCopy.addVoxel(clone);
		}
		return arrayCopy;
	}

	/**
	 * Clone a voxel with a new UUID
	 * 
	 * @param input A parent Voxel
	 */
	public Voxel cloneVoxel(Voxel input) {
		Voxel copy = new Voxel();
		copy.setLocation(input.location.x, input.location.y, input.location.z);
		copy.setCoordinates(input.u, input.v, input.w);
		copy.setRotation(input.rotation);
		copy.setSize(input.width, input.height);
		copy.setUse(input.type);
		return copy;
	}

	/**
	 * Populate a grid of voxels that fits within an exact vector boundary that
	 * defines site, but projected to ground plane;
	 * 
	 * @param boundary    Polygon that defines boundary of site
	 * @param voxelWidth  Width of a square voxel
	 * @param voxelHeight Height of a voxel
	 * @param units       Friendly units of a voxel (e.g. "meters")
	 * @param rotation    Rotation of Voxel Grid
	 * @param translation Translation Vector of Entire Grid
	 */
	public VoxelArray make(Polygon boundary, float voxelWidth, float voxelHeight, float rotation, Point translation) {

		VoxelArray result = new VoxelArray();

		// Create a field of grid points that is certain
		// to uniformly saturate polygon boundary

		// Polygon origin and rectangular bounding box extents
		float origin_x = (float) (0.5 * (boundary.xMax() + boundary.xMin()));
		float origin_y = (float) (0.5 * (boundary.yMax() + boundary.yMin()));
		float boundary_w = boundary.xMax() - boundary.xMin();
		float boundary_h = boundary.yMax() - boundary.yMin();
		float bounds = (boundary_w > boundary_h) ? boundary_w : boundary_h;

		// maximum additional bounding box dimensions if polygon is rotated 45 degrees
		float easement = (float) (Math.max(boundary_w, boundary_h) * (Math.sqrt(2) - 1));
		bounds += easement;

		int U = (int) ((bounds / voxelWidth) + 1);
		int V = U;
		float t_x = translation.x % voxelWidth;
		float t_y = translation.y % voxelWidth;

		for (int u = 0; u < U; u++) {
			for (int v = 0; v < V; v++) {

				// grid coordinates before rotation is applied
				float x_0 = (float) (origin_x - 0.5 * bounds + u * voxelWidth);
				float y_0 = (float) (origin_y - 0.5 * bounds + v * voxelWidth);

				// rotate, then translate
				Point location = rotateXY(new Point(x_0, y_0), new Point(origin_x, origin_y), rotation);
				location.x += t_x;
				location.y += t_y;

				// Test which points are in the polygon boundary
				// and add them to voxel set
				//
				if (boundary.containsPoint(location)) {
					Voxel t = new Voxel();
					t.setLocation(location.x, location.y, GROUND_Z);
					t.setCoordinates(u, v, GROUND_W);
					t.setRotation(rotation);
					t.setSize(voxelWidth, voxelHeight);
					result.addVoxel(t);
				}
			}
		}

		return result;
	}

	/**
	 * Extrude a VoxelArray from a template, Set its use, and drop it onto a of a
	 * base massing all in one method
	 *
	 * @param template New zone will have the same 2D outline as the template
	 *                 VoxelArray
	 * @param base     the zone will be dropped on top of existing mass contained in
	 *                 base
	 * @param levels   the zone will have this many levels (floors)
	 * @param type     the use of the zone
	 * @return a new Voxel Array with the new zone added to the base massing
	 */
	VoxelArray makeAndDrop(VoxelArray template, VoxelArray base, int levels, Use type, float cantileverAllowance) {
		VoxelArray zone = this.extrude(template, levels - 1);
		zone = this.drop(zone, base, cantileverAllowance);
		zone.setVoxelUse(type);
		return this.add(base, zone);
	}

	/**
	 * Clip a portion of a VoxelArray contained within a Polygon
	 * 
	 * @param input    VoxelArray to clip from
	 * @param boundary Polygon that defines boundary of area to clip
	 * @result subset of ipput VoxelArray bounded by polygon
	 */
	public VoxelArray clip(VoxelArray input, Polygon boundary) {
		VoxelArray result = new VoxelArray();
		for (Voxel t : input.voxelList) {
			if (boundary.containsPoint(t.location)) {
				result.addVoxel(t);
			}
		}
		return result;
	}

	/**
	 * Remove a portion of a VoxelArray contained within a Polygon
	 * 
	 * @param input    VoxelArray to cut
	 * @param boundary Polygon that defines boundary of area to cut
	 * @result subset of input VoxelArray not bounded by boundary Polygon
	 */
	public VoxelArray cut(VoxelArray input, Polygon boundary) {
		VoxelArray result = new VoxelArray();
		for (Voxel t : input.voxelList) {
			if (!boundary.containsPoint(t.location)) {
				result.addVoxel(t);
			}
		}
		return result;
	}

	/**
	 * Drops an input VoxelArray from the sky so it rests on the target VoxelArray
	 * without any overlap
	 * 
	 * @param input               VoxelArray to drop from sky
	 * @param target              VoxelArray to drop the input on top of
	 * @param cantileverAllowance a number between 0 and 1 (0 => no allowance; 1 =>
	 *                            allowed to hover in mid air)
	 * @result the input is vertically shifted so that it rests on either the target
	 *         or the ground
	 */
	public VoxelArray drop(VoxelArray input, VoxelArray target, float cantileverAllowance) {

		// Determine vertical coordinate at which the input array will land
		VoxelArray inputBase = this.bottomLayer(input);
		int inputMinW = inputBase.minW();
		int targetLocalMaxW = GROUND_W - 1;
		int targetGlobalMaxW = target.maxW();
		for (Voxel t : inputBase.voxelList) {
			for (int w = GROUND_W; w <= targetGlobalMaxW; w++) {
				String keyCoord = this.coordKey(t.u, t.v, w);
				if (target.voxelMap.containsKey(keyCoord)) {
					if (targetLocalMaxW < w) {
						targetLocalMaxW = w;
					}
				}
			}
		}

		// Drop the input array as a rigid body onto the target array
		VoxelArray result = this.translate(input, 0, 0, 1 + targetLocalMaxW - inputMinW);

		// Check for valid cantilever
		if (this.cantileverFeasible(result, target, cantileverAllowance)) {
			return result;
		} else {
			return new VoxelArray();
		}
	}

	/**
	 * test to see whether an input VoxelArray is cantilevered off of a base
	 * VoxelArray within a specified limit
	 *
	 * @param input               VoxelArray to Cantilever
	 * @param base                VoxelArray that input should be resting upon
	 * @param cantileverAllowance a number between 0 and 1 (0 => no allowance; 1 =>
	 *                            allowed to hover in mid air)
	 * @return true if feasible
	 */
	public boolean cantileverFeasible(VoxelArray input, VoxelArray base, float cantileverAllowance) {
		VoxelArray inputBase = this.bottomLayer(input);
		int inputBaseCount = inputBase.voxelList.size();
		float cantileverCount = 0;
		for (Voxel t : inputBase.voxelList) {
			if (this.getNeighborBottom(t, base) == null && t.w != 0) {
				cantileverCount++;
			}
		}
		return cantileverCount / inputBaseCount <= cantileverAllowance;
	}

	/**
	 * Translates an input VoxelArray using discrete coordinate system
	 * 
	 * @param input VoxelArray to drop from sky
	 * @param dU    amount of voxel units to shift in the u direction
	 * @param dV    amount of voxel units to shift in the v direction
	 * @param dW    amount of voxel units to shift in the W direction
	 * @result the translated VoxelArray
	 */
	public VoxelArray translate(VoxelArray input, int dU, int dV, int dW) {
		if (input.voxelList.size() > 0) {
			VoxelArray result = new VoxelArray();
			for (Voxel t : input.voxelList) {
				t.u += dU;
				t.v += dV;
				t.w += dW;
				t.setLocation(t.location.x + t.width * dU, t.location.y + t.width * dV, t.location.z + t.height * dW);
				result.addVoxel(t);
			}
			return result;
		} else {
			return new VoxelArray();
		}
	}

	/**
	 * Generate a vertical extrusion of an existing Voxel Array
	 *
	 * @param input  VoxelArray to extrude
	 * @param levels number of vertical voxel layers to extrude upward
	 * @return extruded VoxelArray
	 * 
	 */
	public VoxelArray extrude(VoxelArray input, int levels) {
		VoxelArray inputClone = this.hardCloneVoxelArray(input);
		VoxelArray topLayer = this.topLayer(input);

		// Create the extruded voxels
		VoxelArray extruded = new VoxelArray();
		for (int i = 1; i <= levels; i++) {
			for (Voxel t : topLayer.voxelList) {
				Voxel verticalCopy = this.cloneVoxel(t);
				verticalCopy.setCoordinates(t.u, t.v, t.w + i);
				verticalCopy.setLocation(t.location.x, t.location.y, t.location.z + i * t.height);
				extruded.addVoxel(verticalCopy);
			}
		}

		// Add extruded voxels to input and return
		return this.add(inputClone, extruded);
	}

	/**
	 * Get just the voxels that are open to the sky
	 *
	 * @param input voxels to scrape the top off of
	 * @return top of the muffin, TO YOU!
	 */
	public VoxelArray topLayer(VoxelArray input) {
		VoxelArray result = new VoxelArray();
		for (Voxel t : input.voxelList) {
			if (this.getNeighborTop(t, input) == null) {
				result.addVoxel(t);
			}
		}
		return result;
	}

	/**
	 * Get just the voxels that are on the bottom
	 *
	 * @param input voxels to scrape the bottom off of
	 * @return muffin stump
	 */
	public VoxelArray bottomLayer(VoxelArray input) {
		VoxelArray result = new VoxelArray();
		for (Voxel t : input.voxelList) {
			if (this.getNeighborBottom(t, input) == null) {
				result.addVoxel(t);
			}
		}
		return result;
	}

	/**
	 * Given an input VoxelArray, returns a new VoxelArray with outer ring of voxels
	 * removed
	 * 
	 * @param input voxel array to apply setback to
	 * @return new VoxelArray with outer ring of voxels removed removed
	 */
	private VoxelArray setback(VoxelArray input) {
		VoxelArray result = new VoxelArray();
		for (Voxel t : input.voxelList) {
			// Tile surrounded on all sides has 8 neighbors
			if (this.getNeighborsUV(t, input).size() >= 7) {
				result.addVoxel(t);
			}
		}
		return result;
	}

	/**
	 * Given an input VoxelArray and offset distance, returns a new VoxelArray with
	 * outer ring of voxels removed
	 * 
	 * @param input           voxel array to apply setback to
	 * @param setbackDistance distance to apply setback from edge
	 * @return new VoxelArray with outer ring of voxels removed
	 */
	public VoxelArray setback(VoxelArray input, float setbackDistance) {

		// Get the width of the first voxel in the array
		float voxelWidth = input.voxelWidth();
		if (voxelWidth == 0) {
			return new VoxelArray();
		}

		// repeat offset as necessary to achieve desired distance
		VoxelArray result = input;
		int numSetbacks = (int) (0.5f + setbackDistance / voxelWidth);
		for (int i = 0; i < numSetbacks; i++) {
			result = this.setback(result);
		}
		return result;
	}

	/**
	 * Returns a new VoxelArray with child voxels subtracted from input
	 * 
	 * @param input VoxelArray to be subtracted from
	 * @param child VoxelArray to subtract from input
	 * @return New VoxelArray with child subtracted from this VoxelArray
	 */
	public VoxelArray sub(VoxelArray input, VoxelArray child) {
		VoxelArray result = new VoxelArray();
		for (Voxel t : input.voxelList) {
			if (!child.voxelList.contains(t)) {
				result.addVoxel(t);
			}
		}
		return result;
	}

	/**
	 * Returns a new VoxelArray with child voxels added to input
	 * 
	 * @param input VoxelArray to be added to
	 * @param child VoxelArray to add to input
	 * @return New VoxelArray with child subtracted from this VoxelArray
	 */
	public VoxelArray add(VoxelArray input, VoxelArray toAdd) {
		VoxelArray result = this.cloneVoxelArray(input);
		for (Voxel t : toAdd.voxelList) {
			if (!input.voxelList.contains(t)) {
				result.addVoxel(t);
			}
		}
		return result;
	}

	/**
	 * Get the horizontal neighboring Voxels of a specific Voxel
	 * 
	 * @param t      Voxel we wish to know the Neighbors of
	 * @param tArray that encapsulates t
	 * @return Adjacent Voxels that Exist within a VoxelArray
	 */
	public ArrayList<Voxel> getNeighborsUV(Voxel t, VoxelArray tArray) {
		ArrayList<Voxel> neighbors = new ArrayList<Voxel>();
		for (int dU = -1; dU <= +1; dU++) {
			for (int dV = -1; dV <= +1; dV++) {
				if (!(dU == 0 && dV == 0)) { // tile skips itself
					String coordKey = this.coordKey(t.u + dU, t.v + dV, t.w);
					if (tArray.voxelMap.containsKey(coordKey)) {
						Voxel adj = tArray.voxelMap.get(coordKey);
						neighbors.add(adj);
					}
				}
			}
		}
		return neighbors;
	}

	/**
	 * Get the Voxel directly above a specific Voxel; null if none
	 * 
	 * @param t      Voxel we wish to know the Neighbor of
	 * @param tArray that encapsulates t
	 * @return Voxel directly above a specific Voxel; null if none
	 */
	public Voxel getNeighborTop(Voxel t, VoxelArray tArray) {
		String coordKey = this.coordKey(t.u, t.v, t.w + 1);
		if (tArray.voxelMap.containsKey(coordKey)) {
			return tArray.voxelMap.get(coordKey);
		} else {
			return null;
		}
	}

	/**
	 * Get the Voxel directly below a specific Voxel; null if none
	 * 
	 * @param t      Voxel we wish to know the Neighbor of
	 * @param tArray that encapsulates t
	 * @return Voxel directly below a specific Voxel; null if none
	 */
	public Voxel getNeighborBottom(Voxel t, VoxelArray tArray) {
		String coordKey = this.coordKey(t.u, t.v, t.w - 1);
		if (tArray.voxelMap.containsKey(coordKey)) {
			return tArray.voxelMap.get(coordKey);
		} else {
			return null;
		}
	}

	public String coordKey(int u, int v, int w) {
		return u + "," + v + "," + w;
	}

	/**
	 * Generate a rectangular polygon on the ground plane rotated about an origin
	 *
	 * @param location center of rectangle
	 * @param width
	 * @param length
	 * @param rotation
	 * @return resulting rectangle
	 */
	public Polygon rectangle(Point origin, float width, float height, float rotation) {
		ArrayList<Point> corners = new ArrayList<Point>();
		corners.add(new Point(-0.5f, -0.5f));
		corners.add(new Point(+0.5f, -0.5f));
		corners.add(new Point(+0.5f, +0.5f));
		corners.add(new Point(-0.5f, +0.5f));

		Polygon rectangle = new Polygon();
		for (Point corner : corners) {
			corner.x = origin.x + corner.x * width;
			corner.y = origin.y + corner.y * height;
			corner = this.rotateXY(corner, origin, rotation);
			rectangle.addVertex(corner);
		}
		return rectangle;
	}

	/**
	 * Rotate a 2D point about a specified origin
	 *
	 * @param input  point to rotate
	 * @param origin frame of reference to rotate about
	 * @param amount to rotate in radians
	 * @return rotated point
	 */
	private Point rotateXY(Point input, Point origin, float rotation) {
		float sin = (float) Math.sin(rotation);
		float cos = (float) Math.cos(rotation);
		float x_f = +(input.x - origin.x) * cos - (input.y - origin.y) * sin + origin.x;
		float y_f = +(input.x - origin.x) * sin + (input.y - origin.y) * cos + origin.y;
		return new Point(x_f, y_f);
	}
}