/**
 * FuzzyBuilder generates a fuzzy massing according to settings that are passed to it
 *
 * @author Ira Winder
 *
 */
class FuzzyBuilder {
  
  // Non-user settings
  float VOXEL_HEIGHT = 10;
  float CANTILEVER_ALLOWANCE = 0.5;
    
  private FuzzyMorph morph;
  
  public FuzzyBuilder() {
    this.morph = new FuzzyMorph();
  }
  
  /**
   * Build a mass of fuzzy voxels according to a fairly specific configuration of settings from the GUI
   *
   * @param settings
   */
  public Development build(SettingGroup settings) {
    
    Development fuzzy = new Development();
    
    // Track plot polygons that have already been built
    ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();
    
    // Iterate through plots
    SettingGroup plots = settings.settingGroups.get(0);
    for (SettingGroup plotSettings : plots.settingGroups) {
      
      // Define Plot polygon
      SettingGroup vectorGroup = plotSettings.settingGroups.get(0);
      Polygon plotShape = this.parsePolygon(vectorGroup);
      fuzzy.plotShapes.add(plotShape);
      
      // Determine if Plot Polygon is valid for fuzzification
      boolean validPlot = true;
      for (Polygon priorPlotShape : builtPlots) {
        if (plotShape.intersectsPolygon(priorPlotShape)) {
          validPlot = false;
          break;
        }
      }
      if(validPlot) {
        builtPlots.add(plotShape);
        
        // Generate Flat Grid
        float gridSize = parseInt(plotSettings.settingValues.get(0).value);
        float gridRotation = 2 * PI * parseInt(plotSettings.settingValues.get(1).value) / 360f;
        Point gridTranslation = new Point();
        VoxelArray plot = this.morph.make(plotShape, gridSize, 0, gridRotation, gridTranslation);
        fuzzy.site = this.morph.add(fuzzy.site, plot);
        
        // Initialize massing for this entire plot
        VoxelArray plotMassing = new VoxelArray();
        
        // Generate Podiums
        SettingGroup podiumGroup = plotSettings.settingGroups.get(1);
        for (SettingGroup podiumSettings : podiumGroup.settingGroups) {
          
          // Generate Podium Template
          float setbackDistance = parseFloat(podiumSettings.settingValues.get(0).value);
          VoxelArray podiumTemplate = morph.hardCloneVoxelArray(plot);
          podiumTemplate = morph.setback(podiumTemplate, setbackDistance);
          podiumTemplate.setVoxelHeight(VOXEL_HEIGHT);
          
          // Remove Open Area Polygons from Podium Template
          SettingGroup openGroup = podiumSettings.settingGroups.get(0);
          ArrayList<Polygon> openShapes = new ArrayList<Polygon>();
          for(int i=0; i<openGroup.settingGroups.size(); i++) {
            SettingGroup openAreaVertices = openGroup.settingGroups.get(i).settingGroups.get(0);
            Polygon openArea = this.parsePolygon(openAreaVertices);
            openShapes.add(openArea);
            podiumTemplate = morph.cut(podiumTemplate, openArea);
          }
          fuzzy.openShapes.put(plotShape, openShapes);
          
          // Generate Podium Zones
          SettingGroup zoneGroup = podiumSettings.settingGroups.get(1);
          for (int i=0; i<zoneGroup.settingGroups.size(); i++) {
            SettingGroup zone = zoneGroup.settingGroups.get(i);
            int levels = parseInt(zone.settingValues.get(0).value);
            Use use = this.parseUse(zone.settingValues.get(1).value);
            plotMassing = morph.makeAndDrop(podiumTemplate, plotMassing, levels, use, CANTILEVER_ALLOWANCE);
          }
        }
        
        // Generate Towers
        SettingGroup towerGroup = plotSettings.settingGroups.get(2);
        for (SettingGroup towerSettings : towerGroup.settingGroups) {
          
        }
        
        fuzzy.massing = morph.add(fuzzy.massing, plotMassing);
      }
    }
    
    return fuzzy;
  }
  
  /**
   * Parse a SettingGroup of points into a polygon (assumes that y and z are flipped)
   *
   * @param vertexGroup a list of 2D or 3D vectors
   * @return a new polygon made from the vertices in the group
   */
  private Polygon parsePolygon(SettingGroup vertexGroup) {
    ArrayList<float[]> coords = new ArrayList<float[]>();
    for (int l=0; l<vertexGroup.settingValues.size(); l++) {
      SettingValue plotVertex = vertexGroup.settingValues.get(l);
      String[] coordString = plotVertex.value.split(",");
      float[] coord = new float[coordString.length];
      for (int m=0; m<coordString.length; m++) {
        coord[m] = parseFloat(coordString[m]);
      }
      coords.add(coord);
    }
    Polygon shape = new Polygon();
    for (float[] coord : coords) {
      if (coord.length == 2) {
        shape.addVertex(new Point(coord[0], coord[1]));
      } else if (coord.length == 3) {
        shape.addVertex(new Point(coord[0], coord[2], coord[1]));
      }
    }
    return shape;
  }
  
  /**
   * Parse a string of use to the enum Use
   *
   * @param use a string of the use
   * @return an enum of type Use
   */
  private Use parseUse(String use) {
    use.toLowerCase();
    if (use.equals("Retail")) {
      return Use.Retail;
    } else if (use.equals("Community")) {
      return Use.Community;
    } else if (use.equals("Residential")) {
      return Use.Residential;
    } else if (use.equals("Office")) {
      return Use.Office;
    } else if (use.equals("Government")) {
      return Use.Government;
    } else if (use.equals("Convention")) {
      return Use.Convention;
    } else if (use.equals("Carpark")) {
      return Use.Carpark;
    } else if (use.equals("Landscape")) {
      return Use.Landscape;
    } else if (use.equals("Hotel")) {
      return Use.Hotel;
    } else {
      return Use.Unspecified;
    }
  }
}
