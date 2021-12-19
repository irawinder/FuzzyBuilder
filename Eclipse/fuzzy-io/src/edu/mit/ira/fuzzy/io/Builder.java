package edu.mit.ira.fuzzy.io;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.model.Morph;
import edu.mit.ira.fuzzy.model.Point;
import edu.mit.ira.fuzzy.model.Polygon;
import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.model.VoxelArray;
import edu.mit.ira.opensui.setting.Configuration;
import edu.mit.ira.opensui.setting.Setting;

/**
 * FuzzyBuilder generates a fuzzy massing according to settings that are passed
 * to it
 *
 * @author Ira Winder
 *
 */
public class Builder {

	private Morph morph;

	public Builder() {
		this.morph = new Morph();
	}
	
	/**
	 * Build a mass of fuzzy voxels according to a fairly specific configuration of
	 * settings from the GUI
	 *
	 * @param settings
	 */
	public Development build(Configuration root, Schema schema) {
		Development fuzzy = new Development();
		
		try {
			Setting height 		= root.find(schema.FLOOR_HEIGHT);
			Setting cantilever 	= root.find(schema.CANTILEVER);
			Setting plots 		= root.find(schema.PARCELS);
			Setting towers 		= root.find(schema.TOWER_VOLUMES);
			Setting openAreas 	= root.find(schema.AREAS);
			
			// Global Settings
			float voxelHeight 			= height.getFloat();
			float cantileverAllowance 	= cantilever.getFloat() / 100f;
			
			ArrayList<Polygon> openShapes = new ArrayList<Polygon>();
			ArrayList<Polygon> towerShapes = new ArrayList<Polygon>();
			HashMap<Polygon, Setting> towerSettingsMap = new HashMap<Polygon, Setting>();
			ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();
			
			// Pre-Populate Open Area Polygons
			for (Setting openArea : openAreas.settings) {
				Setting vertices = openArea.find(schema.VERTICES);
				Polygon openShape = this.parsePolygon(vertices);
				openShapes.add(openShape);
				fuzzy.allShapes.add(openShape);
			}
			
			// Pre-Populate Tower Polygons
			for (Setting tower : towers.settings) {
				Polygon towerShape = this.towerShape(tower, schema);
				towerSettingsMap.put(towerShape, tower);
				towerShapes.add(towerShape);
				fuzzy.allShapes.add(towerShape);
			}
			
			// Populate Plots and Build
			for (Setting plot : plots.settings) {
				
				// Read from SettingSchema
				Setting vert = plot.find(schema.VERTICES);
				Setting gSiz = plot.find(schema.GRID_SIZE);
				Setting gRot = plot.find(schema.GRID_ROTATION);
				Setting pods = plot.find(schema.PODIUM_VOLUMES);
				
				// Define Plot polygon
				Polygon plotShape = this.parsePolygon(vert);
				String plotName = plot.label;
				fuzzy.plotShapes.add(plotShape);
				fuzzy.allShapes.add(plotShape);
				fuzzy.plotNames.put(plotShape, plotName);

				// Determine if Plot Polygon is valid for fuzzification
				boolean validPlot = true;
				for (Polygon priorPlotShape : builtPlots) {
					if (plotShape.intersectsPolygon(priorPlotShape)) {
						validPlot = false;
						break;
					}
				}
				if (validPlot) {
					builtPlots.add(plotShape);
					
					// Initialize parcel
					float gridSize = gSiz.getInt();
					float gridRotation = (float) (2 * Math.PI * gRot.getInt() / 360f);
					Point gridTranslation = new Point();
					VoxelArray plotVoxels = this.morph.make(plotShape, gridSize, 0, gridRotation, gridTranslation);
					plotVoxels.setVoxelUse(Function.Unspecified);
					fuzzy.plotSite.put(plotShape, plotVoxels);
					fuzzy.site = this.morph.add(fuzzy.site, plotVoxels);

					// Initialize massing for this entire plot
					VoxelArray plotMassing = new VoxelArray();

					// Generate Podiums
					for (Setting podium : pods.settings) {
						
						// Read from SettingSchema
						Setting setback = podium.find(schema.SETBACK);
						Setting zones 	= podium.find(schema.ZONES);
						
						// Generate Podium Template
						float setbackDistance = setback.getFloat();
						VoxelArray podiumTemplate = morph.hardCloneVoxelArray(plotVoxels);
						podiumTemplate = morph.setback(podiumTemplate, setbackDistance);
						podiumTemplate.setVoxelHeight(voxelHeight);

						// Remove Open Area Polygons from Podium Template
						for (Polygon openShape : openShapes) {
							podiumTemplate = morph.cut(podiumTemplate, openShape);
						}

						// Generate Podium Zones
						for (Setting zone : zones.settings) {
							
							// Read from SettingSchema
							Setting l = zone.find(schema.FLOORS);
							Setting f = zone.find(schema.FUNCTION);
							
							// Podium Zone
							int levels = l.getInt();
							Function function = this.parseUse(f.getString());
							plotMassing = morph.makeAndDrop(podiumTemplate, plotMassing, levels, function,
									cantileverAllowance);
						}
					}
					fuzzy.openShapes.put(plotShape, openShapes);
					
					for(Polygon towerShape : towerShapes) {
						
						// Read from SettingSchema
						Setting zones = towerSettingsMap.get(towerShape).find(schema.ZONES);
						
						if (plotShape.containsPolygon(towerShape) && !towerShape.intersectsPolygon(openShapes)) {
							
							// Generate Tower Template
							VoxelArray towerTemplate = morph.hardCloneVoxelArray(plotVoxels);
							towerTemplate.setVoxelHeight(voxelHeight);
							towerTemplate = morph.clip(towerTemplate, towerShape);

							// Generate Tower Zones
							for (Setting zone : zones.settings) {
								
								// Read from SettingSchema
								Setting l = zone.find(schema.FLOORS);
								Setting f = zone.find(schema.FUNCTION);
								
								// Podium Zone
								int levels = l.getInt();
								Function function = this.parseUse(f.getString());
								plotMassing = morph.makeAndDrop(towerTemplate, plotMassing, levels, function,
										cantileverAllowance);
							}
						}
					}
					fuzzy.towerShapes.put(plotShape, towerShapes);

					// Add the current plot's massing to the overall result
					fuzzy.plotMassing.put(plotShape, plotMassing);
					fuzzy.massing = morph.add(fuzzy.massing, plotMassing);
				}
				// Combine flat site tiles on ground plane with massing
				fuzzy.allVoxels = morph.add(fuzzy.site, fuzzy.massing);
				fuzzy.hollowed = morph.hollow(fuzzy.allVoxels);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fuzzy.error = "Server Error: Settings are not formatted correctly for FuzzyIO";
		}

		return fuzzy;
	}
	
	/**
	 * Generate a Tower Polygon from an appropriate Setting
	 * @param towerSettings
	 * @return
	 */
	public Polygon towerShape(Setting towerSettings, Schema schema) {
		
		// Read from SettingSchema
		Setting loc = towerSettings.find(schema.LOCATION);
		Setting rot = towerSettings.find(schema.ROTATION);
		Setting wid = towerSettings.find(schema.WIDTH);
		Setting dep = towerSettings.find(schema.DEPTH);
		
		Point towerLocation = this.parsePoint(loc);
		float towerRotation = (float) (2 * Math.PI * rot.getFloat() / 360f);
		float towerWidth = wid.getFloat();
		float towerDepth = dep.getFloat();
		return morph.rectangle(towerLocation, towerWidth, towerDepth, towerRotation);
	}

	/**
	 * Parse a SettingSchema of points into a polygon (assumes that y and z are
	 * flipped)
	 *
	 * @param vertexGroup a list of 2D or 3D vectors
	 * @return a new polygon made from the vertices in the group
	 */
	private Polygon parsePolygon(Setting vertexGroup) {
		Polygon shape = new Polygon();
		for (Setting vertex : vertexGroup.settings) {
			shape.addVertex(this.parsePoint(vertex));
		}
		return shape;
	}

	/**
	 * Parse a string list into a Point object
	 *
	 * @param a vertex
	 * @return a new point made from the Setting
	 */
	private Point parsePoint(Setting vector) {
		float[] coord = vector.getVector();
		if (coord.length == 2) {
			return new Point(coord[0], coord[1]);
		} else if (coord.length == 3) {
			return new Point(coord[0], coord[2], coord[1]);
		} else {
			System.out.println("SettingSchema not formatted correctly");
			return new Point();
		}
	}

	/**
	 * Parse a string of function to the enum Use
	 *
	 * @param function a string of the function
	 * @return an enum of type Use
	 */
	private Function parseUse(String function) {
		try	{
			return Function.valueOf(function);
		} catch (Exception e) {
			return Function.Unspecified;
		}
	}
}