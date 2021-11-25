/**
 * FuzzyBuilder generates a fuzzy massing according to settings that are passed to it
 *
 * @author Ira Winder
 *
 */
class FuzzyBuilder {
    
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
    
    float voxelHeight = parseFloat(settings.settingValues.get(0).value);
    float cantileverAllowance = parseFloat(settings.settingValues.get(1).value) / 100f;
    
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
        ArrayList<Polygon> openShapes = new ArrayList<Polygon>();
        SettingGroup podiumGroup = plotSettings.settingGroups.get(1);
        for (SettingGroup podiumSettings : podiumGroup.settingGroups) {
          
          // Generate Podium Template
          float setbackDistance = parseFloat(podiumSettings.settingValues.get(0).value);
          VoxelArray podiumTemplate = morph.hardCloneVoxelArray(plot);
          podiumTemplate = morph.setback(podiumTemplate, setbackDistance);
          podiumTemplate.setVoxelHeight(voxelHeight);
          
          // Remove Open Area Polygons from Podium Template
          SettingGroup openGroup = podiumSettings.settingGroups.get(0);
          for(int i=0; i<openGroup.settingGroups.size(); i++) {
            SettingGroup openAreaVertices = openGroup.settingGroups.get(i).settingGroups.get(0);
            Polygon openArea = this.parsePolygon(openAreaVertices);
            openShapes.add(openArea);
            podiumTemplate = morph.cut(podiumTemplate, openArea);
          }
          
          // Generate Podium Zones
          SettingGroup zoneGroup = podiumSettings.settingGroups.get(1);
          for (int i=0; i<zoneGroup.settingGroups.size(); i++) {
            SettingGroup zone = zoneGroup.settingGroups.get(i);
            int levels = parseInt(zone.settingValues.get(0).value);
            Use use = this.parseUse(zone.settingValues.get(1).value);
            plotMassing = morph.makeAndDrop(podiumTemplate, plotMassing, levels, use, cantileverAllowance);
          }
        }
        fuzzy.openShapes.put(plotShape, openShapes);
        
        // Generate Towers
        ArrayList<Polygon> towerShapes = new ArrayList<Polygon>();
        SettingGroup towerGroup = plotSettings.settingGroups.get(2);
        for (SettingGroup towerSettings : towerGroup.settingGroups) {
          
          // Generate Tower Polygon (and check if its in the current plot)
          Point towerLocation = this.parsePoint(towerSettings.settingValues.get(0));
          float towerRotation = parseFloat(towerSettings.settingValues.get(1).value);
          float towerWidth = parseFloat(towerSettings.settingValues.get(2).value);
          float towerDepth = parseFloat(towerSettings.settingValues.get(3).value);
          Polygon towerShape = morph.rectangle(towerLocation, towerWidth, towerDepth, towerRotation);
          towerShapes.add(towerShape);
          if(plotShape.containsPolygon(towerShape)) {
            
            // Generate Tower Template
            VoxelArray towerTemplate = morph.hardCloneVoxelArray(plot);
            towerTemplate.setVoxelHeight(voxelHeight);
            towerTemplate = morph.clip(towerTemplate, towerShape);
              
            // Generate Tower Zones
            SettingGroup zoneGroup = towerSettings.settingGroups.get(0);
            for (int i=0; i<zoneGroup.settingGroups.size(); i++) {
              SettingGroup zone = zoneGroup.settingGroups.get(i);
              int levels = parseInt(zone.settingValues.get(0).value);
              Use use = this.parseUse(zone.settingValues.get(1).value);
              plotMassing = morph.makeAndDrop(towerTemplate, plotMassing, levels, use, cantileverAllowance);
            }
          }
        }
        fuzzy.towerShapes.put(plotShape, towerShapes);
        
        // Add the current plot's massing to the overall result
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
    Polygon shape = new Polygon();
    for (int i=0; i<vertexGroup.settingValues.size(); i++) {
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
    for (int m=0; m<coordString.length; m++) {
      coord[m] = parseFloat(coordString[m]);
    }
    if (coord.length == 2) {
      return new Point(coord[0], coord[1]);
    } else if (coord.length == 3) {
      return new Point(coord[0], coord[2], coord[1]);
    } else {
      println("SettingValue must formatted as 'x,y' or 'x,y,z'");
      return new Point();
    }
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
