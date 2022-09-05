package edu.mit.ira.fuzzy.model.build;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.model.Orientation;
import edu.mit.ira.fuzzy.model.Point;
import edu.mit.ira.fuzzy.model.Polygon;
import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.model.VoxelArray;
import edu.mit.ira.fuzzy.model.schema.Schema;
import edu.mit.ira.opensui.setting.Configuration;
import edu.mit.ira.opensui.setting.Setting;

/**
 * FuzzyBuilder generates a fuzzy massing according to settings that are passed
 * to it
 *
 * @author Ira Winder
 *
 */
public class Build {
	
	/**
	 * Build a mass of fuzzy voxels according to a fairly specific configuration of
	 * settings from the GUI
	 *
	 * @param settings
	 */
	public static Development development(Configuration root) {
		Development fuzzy = new Development();
		
		try {
			Setting plots 		= root.find(Schema.PARCELS);
			Setting towers 		= root.find(Schema.TOWER_VOLUMES);
			Setting openAreas 	= root.find(Schema.AREAS);
			//Setting cantilever 	= root.find(schema.CANTILEVER);
			
			// Global Settings
			//float cantileverAllowance 	= cantilever.getFloat() / 100f;
			float cantileverAllowance 	= 1f;
			
			ArrayList<Polygon> openShapes = new ArrayList<Polygon>();
			ArrayList<Polygon> towerShapes = new ArrayList<Polygon>();
			HashMap<Polygon, Setting> towerSettingsMap = new HashMap<Polygon, Setting>();
			ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();
			
			// Pre-Populate Open Area Polygons
			for (Setting openArea : openAreas.settings) {
				Setting vertices = openArea.find(Schema.VERTICES);
				Polygon openShape = parsePolygon(vertices);
				openShape.setType("Open");
				openShapes.add(openShape);
				fuzzy.allShapes.add(openShape);
			}
			
			// Pre-Populate Tower Polygons
			for (Setting tower : towers.settings) {
				Polygon towerShape = towerShape(tower);
				towerShape.setType("Tower");
				towerSettingsMap.put(towerShape, tower);
				towerShapes.add(towerShape);
				fuzzy.allShapes.add(towerShape);
			}
			
			// Populate Plots and Build
			for (Setting plot : plots.settings) {
				
				// Read from SettingSchema
				Setting vert = plot.find(Schema.VERTICES);
				Setting gSiz = plot.find(Schema.GRID_SIZE);
				Setting gRot = plot.find(Schema.GRID_ROTATION);
				Setting gDx = plot.find(Schema.GRID_X_OFFSET);
				Setting gDy = plot.find(Schema.GRID_Y_OFFSET);
				Setting pods = plot.find(Schema.PODIUM_VOLUMES);
				
				// Define Plot polygon
				Polygon plotShape = parsePolygon(vert);
				plotShape.setType("Plot");
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
					float gridOffsetX = gridSize * gDx.getInt()/100f;
					float gridOffsetY = gridSize * gDy.getInt()/100f;
					Point gridTranslation = new Point(gridOffsetX, gridOffsetY);
					VoxelArray plotVoxels = Morph.make(plotShape, gridSize, 0, gridRotation, gridTranslation);
					plotVoxels.setVoxelUse(Function.Unspecified);
					fuzzy.plotSite.put(plotShape, plotVoxels);
					fuzzy.site = Morph.add(fuzzy.site, plotVoxels);

					// Initialize massing for this entire plot
					VoxelArray plotMassing = new VoxelArray();

					// Generate Podiums
					for (Setting podium : pods.settings) {
						
						// Read from SettingSchema
						Setting orient  = podium.find(Schema.ORIENTATION);
						Setting setback = podium.find(Schema.SETBACK);
						Setting zones 	= podium.find(Schema.ZONES);
						
						// Generate Podium Template
						float setbackDistance = setback.getFloat();
						VoxelArray podiumTemplate = Morph.hardCloneVoxelArray(plotVoxels);
						podiumTemplate = Morph.setback(podiumTemplate, setbackDistance);

						// Remove Open Area Polygons from Podium Template
						for (Polygon openShape : openShapes) {
							podiumTemplate = Morph.cut(podiumTemplate, openShape);
						}

						// Generate Podium Zones
						for (Setting zone : zones.settings) {
							
							// Read from SettingSchema
							Setting f = zone.find(Schema.FUNCTION);
							Setting l = zone.find(Schema.FLOORS);
							Setting h = zone.find(Schema.FLOOR_HEIGHT);
							
							// Podium Zone
							int levels = l.getInt();
							float height = h.getFloat();
							Function function = parseUse(f.getString());
							Orientation orientation = parseOrientation(orient.getString());
							
							podiumTemplate.setVoxelHeight(height);
							if (orientation == Orientation.Above_Ground) {
								plotMassing = Morph.makeAndDrop(podiumTemplate, plotMassing, levels, function, cantileverAllowance);
							} else {
								plotMassing = Morph.makeAndLift(podiumTemplate, plotMassing, levels, function);
							}
						}
					}
					fuzzy.openShapes.put(plotShape, openShapes);
					
					for(Polygon towerShape : towerShapes) {
						
						// Read from SettingSchema
						Setting zones = towerSettingsMap.get(towerShape).find(Schema.ZONES);
						
						if (plotShape.containsPolygon(towerShape)) {
							
							// Generate Tower Template
							VoxelArray towerTemplate = Morph.hardCloneVoxelArray(plotVoxels);
							towerTemplate = Morph.clip(towerTemplate, towerShape);

							// Generate Tower Zones
							for (Setting zone : zones.settings) {
								
								// Read from SettingSchema
								Setting f = zone.find(Schema.FUNCTION);
								Setting l = zone.find(Schema.FLOORS);
								Setting h = zone.find(Schema.FLOOR_HEIGHT);
								
								// Podium Zone
								int levels = l.getInt();
								float height = h.getFloat();
								Function function = parseUse(f.getString());
								towerTemplate.setVoxelHeight(height);
								plotMassing = Morph.makeAndDrop(towerTemplate, plotMassing, levels, function,
										cantileverAllowance);
							}
						}
					}
					fuzzy.towerShapes.put(plotShape, towerShapes);

					// Add the current plot's massing to the overall result
					fuzzy.plotMassing.put(plotShape, plotMassing);
					fuzzy.massing = Morph.add(fuzzy.massing, plotMassing);
					
					// Add add voxels to a special group that only contains unsurround voxels
					VoxelArray plotMassingHollowed = Morph.hollow(plotMassing);
					fuzzy.plotMassingHollowed.put(plotShape, plotMassingHollowed);
					fuzzy.massingHollowed = Morph.add(fuzzy.massingHollowed, plotMassingHollowed);
					
				}
				// Combine flat site tiles on ground plane with massing
				fuzzy.allVoxels = Morph.add(fuzzy.site, fuzzy.massing);
				fuzzy.allVoxelsHollowed = Morph.add(fuzzy.site, fuzzy.massingHollowed);
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
	private static Polygon towerShape(Setting towerSettings) {
		
		// Read from SettingSchema
		Setting loc = towerSettings.find(Schema.LOCATION);
		Setting rot = towerSettings.find(Schema.ROTATION);
		Setting wid = towerSettings.find(Schema.WIDTH);
		Setting dep = towerSettings.find(Schema.DEPTH);
		
		Point towerLocation = parsePoint(loc);
		float towerRotation = (float) (2 * Math.PI * rot.getFloat() / 360f);
		float towerWidth = wid.getFloat();
		float towerDepth = dep.getFloat();
		return Morph.rectangle(towerLocation, towerWidth, towerDepth, towerRotation);
	}

	/**
	 * Parse a SettingSchema of points into a polygon (assumes that y and z are
	 * flipped)
	 *
	 * @param vertexGroup a list of 2D or 3D vectors
	 * @return a new polygon made from the vertices in the group
	 */
	private static Polygon parsePolygon(Setting vertexGroup) {
		Polygon shape = new Polygon();
		for (Setting vertex : vertexGroup.settings) {
			shape.addVertex(parsePoint(vertex));
		}
		return shape;
	}

	/**
	 * Parse a string list into a Point object
	 *
	 * @param a vertex
	 * @return a new point made from the Setting
	 */
	private static Point parsePoint(Setting vector) {
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
	private static Function parseUse(String function) {
		try	{
			return Function.valueOf(function);
		} catch (Exception e) {
			return Function.Unspecified;
		}
	}
	
	/**
	 * Parse a string of orientation to the enum Use
	 *
	 * @param orientation a string of the function
	 * @return an enum of type Use
	 */
	private static Orientation parseOrientation(String orientation) {
		String value = orientation.replace(" ", "_");
		try	{
			return Orientation.valueOf(value);
		} catch (Exception e) {
			return Orientation.Above_Ground;
		}
	}
}