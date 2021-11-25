class FuzzyBuilder {
  
  private float CANTILEVER_ALLOWANCE = 0.5;
  
  private FuzzyRandom random;
  private FuzzyMorph morph;
  
  public ArrayList<Polygon> plotShapes;
  public HashMap<Polygon, ArrayList<Polygon>> towerShapes;
  public VoxelArray site, massing;
  
  public FuzzyBuilder() {
    this.random = new FuzzyRandom();
    this.morph = new FuzzyMorph();
    this.plotShapes = new ArrayList<Polygon>();
    this.towerShapes = new HashMap<Polygon, ArrayList<Polygon>>();
    this.site = new VoxelArray();
    this.massing = new VoxelArray();
  }
  
  public void build(SettingGroup settings) {
    this.plotShapes.clear();
    this.towerShapes.clear();
    this.site = new VoxelArray();
    this.massing = new VoxelArray();
    
    // Track plot polygons that have already been built
    ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();
    
    // Iterate through plots
    SettingGroup plots = settings.settingGroups.get(0);
    for (SettingGroup plotSettings : plots.settingGroups) {
      
      // Define Plot polygon
      SettingGroup vectorGroup = plotSettings.settingGroups.get(0);
      Polygon plotShape = this.parsePolygon(vectorGroup);
      this.plotShapes.add(plotShape);
      
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
        this.site = fuzzy.morph.add(this.site, plot);
      }
    }
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
  
  /**
   * Convert a JSON string of model settings to the SettingGroup class
   *
   * @param data settings formatted as json string
   * @return settings formatted as SettingGRoup class
   */
  public SettingGroup parseSettingGroup(String data) {
    JSONObject settingGroupJSON = parseJSONObject(data);
    try {
      return this.serialize(settingGroupJSON);
    } catch (Exception e) {
      println("JSON is not formatted correctly");
      return new SettingGroup();
    }
  }
  
  /**
   * serialize a JSONObject to SettingGroup class
   *
   * @param settingGroup data formatted as JSON
   * @return data formatted as SettingGroup class
   */
  public SettingGroup serialize(JSONObject settingGroup) {
    SettingGroup group = new SettingGroup();
    
    // Check for type group
    if (settingGroup.getString("type").equals("group")) {
      group.type = settingGroup.getString("type");
      group.name = settingGroup.getString("name");
      
      // Add SettingValues Associated with group
      JSONArray settingValues = settingGroup.getJSONArray("settingValues");
      for (int i=0; i<settingValues.size(); i++) {
        JSONObject settingValue = settingValues.getJSONObject(i);
        SettingValue value = new SettingValue();
        value.name = settingValue.getString("name");
        value.type = settingValue.getString("type");
        value.value = settingValue.getString("value");
        group.settingValues.add(value);
      }
      
      // Add Child SettingGroups attached to Group
      JSONArray settingGroups = settingGroup.getJSONArray("settingGroups");
      for (int i=0; i<settingGroups.size(); i++) {
        JSONObject childSettingGroup = settingGroups.getJSONObject(i);
        SettingGroup childGroup = serialize(childSettingGroup);
        group.settingGroups.add(childGroup);
      }
    } else {
      println("type must be group");
    }
    return group;
  }
  
  /**
   * Add a new zone on top of a base massing
   *
   * @param template New zone will have the same 2D outline as the template VoxelArray
   * @param base the zone will be dropped on top of existing mass contained in base
   * @param levels the zone will have this many levels (floors)
   * @param type the use of the zone
   * @return a new Voxel Array with the new zone added to the base massing
   */
  VoxelArray addZone(VoxelArray template, VoxelArray base, int levels, Use type) {
    VoxelArray zone = this.morph.extrude(template, levels - 1);
    zone = this.morph.drop(zone, base, CANTILEVER_ALLOWANCE);
    zone.setVoxelUse(type);
    return this.morph.add(base, zone);
  }
}
