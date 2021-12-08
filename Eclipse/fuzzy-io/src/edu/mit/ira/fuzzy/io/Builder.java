package edu.mit.ira.fuzzy.io;

import java.util.ArrayList;

import edu.mit.ira.fuzzy.model.Development;
import edu.mit.ira.fuzzy.model.Morph;
import edu.mit.ira.fuzzy.model.Point;
import edu.mit.ira.fuzzy.model.Polygon;
import edu.mit.ira.fuzzy.model.Function;
import edu.mit.ira.fuzzy.model.VoxelArray;
import edu.mit.ira.fuzzy.setting.SettingGroup;
import edu.mit.ira.fuzzy.setting.SettingValue;

/**
 * FuzzyBuilder generates a fuzzy massing according to settings that are passed
 * to it
 *
 * @author Ira Winder
 *
 */
public class Builder {

	private Morph morph;

	final private float DEFAULT_VOXEL_HEIGHT = 10;
	final private float DEFAULT_CANTILEVER_ALLOWANCE = 0.5f;

	public Builder() {
		this.morph = new Morph();
	}
	
	/**
	 * Build a mass of fuzzy voxels according to a fairly specific configuration of
	 * settings from the GUI
	 *
	 * @param settings
	 */
	public Development build(SettingGroup settings) {

		Development fuzzy = new Development();

		try {

			float voxelHeight, cantileverAllowance;
			if (settings.settingValues.size() >= 2) {
				voxelHeight = Float.parseFloat(settings.settingValues.get(0).value);
				cantileverAllowance = Float.parseFloat(settings.settingValues.get(1).value) / 100f;
			} else {
				voxelHeight = DEFAULT_VOXEL_HEIGHT;
				cantileverAllowance = DEFAULT_CANTILEVER_ALLOWANCE;
			}
			
			// Populate Open Area(s)
			ArrayList<Polygon> openShapes = new ArrayList<Polygon>();
			SettingGroup openGroup = settings.settingGroups.get(1);
			for (SettingGroup openSettings : openGroup.settingGroups) {
				SettingGroup openAreaVertices = openSettings.settingGroups.get(0);
				Polygon openArea = this.parsePolygon(openAreaVertices);
				openShapes.add(openArea);
				fuzzy.allShapes.add(openArea);
			}
			
			// Track plot polygons that have already been built
			ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();

			// Iterate through plots
			SettingGroup plots = settings.settingGroups.get(0);
			for (SettingGroup plotSettings : plots.settingGroups) {

				// Define Plot polygon
				SettingGroup vectorGroup = plotSettings.settingGroups.get(0);
				Polygon plotShape = this.parsePolygon(vectorGroup);
				String plotName = plotSettings.name;
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

					// Generate Flat Grid
					float gridSize = Integer.parseInt(plotSettings.settingValues.get(0).value);
					float gridRotation = (float) (2 * Math.PI
							* Integer.parseInt(plotSettings.settingValues.get(1).value) / 360f);
					Point gridTranslation = new Point();
					VoxelArray plot = this.morph.make(plotShape, gridSize, 0, gridRotation, gridTranslation);
					plot.setVoxelUse(Function.Unspecified);
					fuzzy.plotSite.put(plotShape, plot);
					fuzzy.site = this.morph.add(fuzzy.site, plot);

					// Initialize massing for this entire plot
					VoxelArray plotMassing = new VoxelArray();

					// Generate Podiums
					SettingGroup podiumGroup = plotSettings.settingGroups.get(1);
					for (SettingGroup podiumSettings : podiumGroup.settingGroups) {
						
						// Generate Podium Template
						float setbackDistance = Float.parseFloat(podiumSettings.settingValues.get(0).value);
						VoxelArray podiumTemplate = morph.hardCloneVoxelArray(plot);
						podiumTemplate = morph.setback(podiumTemplate, setbackDistance);
						podiumTemplate.setVoxelHeight(voxelHeight);

						// Remove Open Area Polygons from Podium Template
						for (Polygon openShape : openShapes) {
							podiumTemplate = morph.cut(podiumTemplate, openShape);
						}

						// Generate Podium Zones
						SettingGroup zoneGroup = podiumSettings.settingGroups.get(0);
						for (int i = 0; i < zoneGroup.settingGroups.size(); i++) {
							SettingGroup zone = zoneGroup.settingGroups.get(i);
							int levels = Integer.parseInt(zone.settingValues.get(0).value);
							Function function = this.parseUse(zone.settingValues.get(1).value);
							plotMassing = morph.makeAndDrop(podiumTemplate, plotMassing, levels, function,
									cantileverAllowance);
						}
					}
					fuzzy.openShapes.put(plotShape, openShapes);

					// Generate Towers
					SettingGroup towerGroup = plotSettings.settingGroups.get(2);
					ArrayList<Polygon> towerShapes = new ArrayList<Polygon>();
					for (SettingGroup towerSettings : towerGroup.settingGroups) {

						// Generate Tower Polygon (and check if its in the current plot)
						Polygon towerShape = this.towerShape(towerSettings);
						towerShapes.add(towerShape);
						fuzzy.allShapes.add(towerShape);
						
						if (plotShape.containsPolygon(towerShape) && !towerShape.intersectsPolygon(openShapes)) {

							// Generate Tower Template
							VoxelArray towerTemplate = morph.hardCloneVoxelArray(plot);
							towerTemplate.setVoxelHeight(voxelHeight);
							towerTemplate = morph.clip(towerTemplate, towerShape);

							// Generate Tower Zones
							SettingGroup zoneGroup = towerSettings.settingGroups.get(0);
							for (int i = 0; i < zoneGroup.settingGroups.size(); i++) {
								SettingGroup zone = zoneGroup.settingGroups.get(i);
								int levels = Integer.parseInt(zone.settingValues.get(0).value);
								Function function = this.parseUse(zone.settingValues.get(1).value);
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
			System.out.println("Settings are not formatted correctly for this build of FuzzyIO");
			return null;
		}

		return fuzzy;
	}
	
	/**
	 * Build a partial model with extrusion polygons but no voxels
	 *
	 * @param settings
	 */
	public Development basicBuild(SettingGroup settings) {
		
		Development fuzzy = new Development();

		try {

			// Iterate through plots
			SettingGroup plots = settings.settingGroups.get(0);
			for (SettingGroup plotSettings : plots.settingGroups) {

				// Define Plot polygon
				SettingGroup vectorGroup = plotSettings.settingGroups.get(0);
				Polygon plotShape = this.parsePolygon(vectorGroup);
				fuzzy.plotShapes.add(plotShape);
				fuzzy.allShapes.add(plotShape);
				
				// Generate Podiums
				ArrayList<Polygon> openShapes = new ArrayList<Polygon>();
				SettingGroup podiumGroup = plotSettings.settingGroups.get(1);
				for (SettingGroup podiumSettings : podiumGroup.settingGroups) {

					// Remove Open Area Polygons from Podium Template
					SettingGroup openGroup = podiumSettings.settingGroups.get(0);
					for (SettingGroup openSettings : openGroup.settingGroups) {
						SettingGroup openAreaVertices = openSettings.settingGroups.get(0);
						Polygon openArea = this.parsePolygon(openAreaVertices);
						openShapes.add(openArea);
						fuzzy.allShapes.add(openArea);
					}
				}
				fuzzy.openShapes.put(plotShape, openShapes);
				
				// Generate Towers
				SettingGroup towerGroup = plotSettings.settingGroups.get(2);
				ArrayList<Polygon> towerShapes = new ArrayList<Polygon>();
				for (SettingGroup towerSettings : towerGroup.settingGroups) {
					Polygon towerShape = this.towerShape(towerSettings);
					towerShapes.add(towerShape);
					fuzzy.allShapes.add(towerShape);
				}
				fuzzy.towerShapes.put(plotShape, towerShapes);
			}
		} catch (Exception e) {
			System.out.println("Settings are not formatted correctly for this build of FuzzyIO");
			return null;
		}

		return fuzzy;
	}
	
	/**
	 * Generate a Tower Polygon from an appropriate SettingGroup
	 * @param towerSettings
	 * @return
	 */
	public Polygon towerShape(SettingGroup towerSettings) {
		Point towerLocation = this.parsePoint(towerSettings.settingValues.get(0));
		float towerRotation = (float) (2 * Math.PI * Float.parseFloat(towerSettings.settingValues.get(1).value) / 360f);
		float towerWidth = Float.parseFloat(towerSettings.settingValues.get(2).value);
		float towerDepth = Float.parseFloat(towerSettings.settingValues.get(3).value);
		return morph.rectangle(towerLocation, towerWidth, towerDepth, towerRotation);
	}

	/**
	 * Parse a SettingGroup of points into a polygon (assumes that y and z are
	 * flipped)
	 *
	 * @param vertexGroup a list of 2D or 3D vectors
	 * @return a new polygon made from the vertices in the group
	 */
	private Polygon parsePolygon(SettingGroup vertexGroup) {
		Polygon shape = new Polygon();
		for (int i = 0; i < vertexGroup.settingValues.size(); i++) {
			SettingValue plotVertex = vertexGroup.settingValues.get(i);
			shape.addVertex(this.parsePoint(plotVertex));
		}
		return shape;
	}

	/**
	 * Parse a string into a Point object (assumes "x,y,z")
	 *
	 * @param a vertex
	 * @return a new point made from the SettingValue
	 */
	private Point parsePoint(SettingValue vector) {
		String[] coordString = vector.value.split(",");
		float[] coord = new float[coordString.length];
		for (int m = 0; m < coordString.length; m++) {
			coord[m] = Float.parseFloat(coordString[m]);
		}
		if (coord.length == 2) {
			return new Point(coord[0], coord[1]);
		} else if (coord.length == 3) {
			return new Point(coord[0], coord[2], coord[1]);
		} else {
			System.out.println("SettingValue must formatted as 'x,y' or 'x,y,z'");
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