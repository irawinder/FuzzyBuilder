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
        
        // Generate Grid
        float gridSize = parseInt(plotSettings.settingValues.get(0).value);
        float gridRotation = 2 * PI * parseInt(plotSettings.settingValues.get(1).value) / 360f;
        Point gridTranslation = new Point();
        VoxelArray plot = this.morph.make(plotShape, gridSize, 0, gridRotation, gridTranslation);
        fuzzy.site = this.morph.add(fuzzy.site, plot);
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
  public Polygon parsePolygon(SettingGroup vertexGroup) {
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
}
