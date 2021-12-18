package edu.mit.ira.fuzzy.io;

import java.util.ArrayList;
import java.util.HashMap;

import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.model.Morph;
import edu.mit.ira.fuzzy.model.Point;
import edu.mit.ira.fuzzy.model.Polygon;
import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.model.VoxelArray;
import edu.mit.ira.fuzzy.setting.Setting;

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
	public Development build(Setting root) {
		
		Development fuzzy = new Development();
		
		try {
			Setting height 		= root.settings.get(0);
			Setting cantilever 	= root.settings.get(1);
			Setting plots 		= root.settings.get(2);
			Setting towers 		= root.settings.get(3);
			Setting openAreas 	= root.settings.get(4);
			
			// Global Settings
			float voxelHeight 			= Float.parseFloat(height.value.get(0));
			float cantileverAllowance 	= Float.parseFloat(cantilever.value.get(0)) / 100f;
			
			// Pre-Populate Open Area Polygons
			ArrayList<Polygon> openShapes = new ArrayList<Polygon>();
			for (int i=0; i<openAreas.settings.size(); i++) {
				
				// Read from SettingSchema
				Setting openArea = openAreas.settings.get(i);
				Setting vertices = openArea.settings.get(0);
				
				Polygon openShape = this.parsePolygon(vertices);
				openShapes.add(openShape);
				fuzzy.allShapes.add(openShape);
			}
			
			// Pre-Populate Tower Polygons
			ArrayList<Polygon> towerShapes = new ArrayList<Polygon>();
			HashMap<Polygon, Setting> towerSettingsMap = new HashMap<Polygon, Setting>();
			for (int i=0; i<towers.settings.size(); i++) {
				
				// Read from SettingSchema
				Setting tower = (Setting) towers.settings.get(i);
				
				Polygon towerShape = this.towerShape(tower);
				towerSettingsMap.put(towerShape, tower);
				towerShapes.add(towerShape);
				fuzzy.allShapes.add(towerShape);
			}
			
			// Populate Plots and Build
			ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();
			for (int i=0; i<plots.settings.size(); i++) {
				
				// Read from SettingSchema
				Setting plot = plots.settings.get(i);
				Setting vert = plot.settings.get(0);
				Setting gSiz = plot.settings.get(1);
				Setting gRot = plot.settings.get(2);
				Setting pods = plot.settings.get(3);
				
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
					float gridSize = Integer.parseInt(gSiz.value.get(0));
					float gridRotation = (float) (2 * Math.PI * Integer.parseInt(gRot.value.get(0)) / 360f);
					Point gridTranslation = new Point();
					VoxelArray plotVoxels = this.morph.make(plotShape, gridSize, 0, gridRotation, gridTranslation);
					plotVoxels.setVoxelUse(Function.Unspecified);
					fuzzy.plotSite.put(plotShape, plotVoxels);
					fuzzy.site = this.morph.add(fuzzy.site, plotVoxels);

					// Initialize massing for this entire plot
					VoxelArray plotMassing = new VoxelArray();

					// Generate Podiums
					for (int j=0; j<pods.settings.size(); j++) {
						
						// Read from SettingSchema
						Setting podium 	= pods.settings.get(j);
						Setting setback = podium.settings.get(0);
						Setting zones 	= podium.settings.get(1);
						
						// Generate Podium Template
						float setbackDistance = Float.parseFloat(setback.value.get(0));
						VoxelArray podiumTemplate = morph.hardCloneVoxelArray(plotVoxels);
						podiumTemplate = morph.setback(podiumTemplate, setbackDistance);
						podiumTemplate.setVoxelHeight(voxelHeight);

						// Remove Open Area Polygons from Podium Template
						for (Polygon openShape : openShapes) {
							podiumTemplate = morph.cut(podiumTemplate, openShape);
						}

						// Generate Podium Zones
						for (int k = 0; k < zones.settings.size(); k++) {
							
							// Read from SettingSchema
							Setting zone	= zones.settings.get(k);
							Setting l		= zone.settings.get(0);
							Setting f		= zone.settings.get(1);
							
							// Podium Zone
							int levels = Integer.parseInt(l.value.get(0));
							Function function = this.parseUse(f.value.get(0));
							plotMassing = morph.makeAndDrop(podiumTemplate, plotMassing, levels, function,
									cantileverAllowance);
						}
					}
					fuzzy.openShapes.put(plotShape, openShapes);
					
					for(Polygon towerShape : towerShapes) {
						
						// Read from SettingSchema
						Setting zones = towerSettingsMap.get(towerShape).settings.get(4);
						
						if (plotShape.containsPolygon(towerShape) && !towerShape.intersectsPolygon(openShapes)) {
							
							// Generate Tower Template
							VoxelArray towerTemplate = morph.hardCloneVoxelArray(plotVoxels);
							towerTemplate.setVoxelHeight(voxelHeight);
							towerTemplate = morph.clip(towerTemplate, towerShape);

							// Generate Tower Zones
							for (int j = 0; j < zones.settings.size(); j++) {
								
								// Read from SettingSchema
								Setting zone	= zones.settings.get(j);
								Setting l		= zone.settings.get(0);
								Setting f		= zone.settings.get(1);
								
								int levels = Integer.parseInt(l.value.get(0));
								Function function = this.parseUse(f.value.get(0));
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
			fuzzy.error = "Server Error: Settings are not formatted correctly for FuzzyIO";
		}

		return fuzzy;
	}
	
	/**
	 * Generate a Tower Polygon from an appropriate Setting
	 * @param towerSettings
	 * @return
	 */
	public Polygon towerShape(Setting towerSettings) {
		
		// Read from SettingSchema
		Setting loc = towerSettings.settings.get(0);
		Setting rot = towerSettings.settings.get(1);
		Setting wid = towerSettings.settings.get(2);
		Setting dep = towerSettings.settings.get(3);
		
		Point towerLocation = this.parsePoint(loc);
		float towerRotation = (float) (2 * Math.PI * Float.parseFloat(rot.value.get(0)) / 360f);
		float towerWidth = Float.parseFloat(wid.value.get(0));
		float towerDepth = Float.parseFloat(dep.value.get(0));
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
		for (int i = 0; i < vertexGroup.settings.size(); i++) {
			Setting plotVertex = vertexGroup.settings.get(i);
			shape.addVertex(this.parsePoint(plotVertex));
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