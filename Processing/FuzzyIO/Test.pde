class Test {
  final int NUM_VERTICES = 4;
  final float PLOT_X = 0;
  final float PLOT_Y = -1000;
  final float PLOT_MIN_RADIUS = 300;
  final float PLOT_MAX_RADIUS = 1000;
  final float VOXEL_WIDTH = 25;
  final float VOXEL_HEIGHT = 10;
  final float VOXEL_ROTATION = 0.25 * PI;
  final Point VOXEL_TRANSLATE = new Point(5, 5);
  final float SETBACK_DISTANCE = 100;
  final float TOWER_WIDTH = 100;
  final float TOWER_DEPTH = 200;
  final float TOWER_ROTATION = 0.25 * PI;
  final int NUM_PLOTS = 3;
  final int NUM_TOWERS = 3;
  
  // local w (vertical) coordinate of ground
  private float CANTILEVER_ALLOWANCE = 0.5;
  
  private FuzzyRandom random;
  private FuzzyMorph morph;
  
  public ArrayList<Polygon> plotShapes;
  public HashMap<Polygon, ArrayList<Polygon>> towerShapes;
  public VoxelArray site, massing;
    
  public Test() {
    int time = millis();
    
    this.random = new FuzzyRandom();
    this.morph = new FuzzyMorph();
    this.plotShapes = new ArrayList<Polygon>();
    this.towerShapes = new HashMap<Polygon, ArrayList<Polygon>>();
    this.site = new VoxelArray();
    this.massing = new VoxelArray();
    
    for (int i=0; i<NUM_PLOTS; i++) {
      
      // Random Plot Shape
      float p_x = PLOT_X + random(-1, 1) * PLOT_MAX_RADIUS;
      float p_y = PLOT_Y + random(-1, 1) * PLOT_MAX_RADIUS;
      Polygon plotShape = this.random.polygon(
        p_x, 
        p_y,
        NUM_VERTICES, 
        PLOT_MIN_RADIUS, 
        PLOT_MAX_RADIUS
      );
      this.plotShapes.add(plotShape);
      
      // Random Tower Base
      ArrayList<Polygon> tShapes = new ArrayList<Polygon>();
      for (int j=0; j<NUM_TOWERS; j++) {
        float t_x = p_x + random(-.5, .5) * PLOT_MIN_RADIUS;
        float t_y = p_y + random(-.1, .1) * PLOT_MIN_RADIUS;
        Polygon towerShape = this.morph.rectangle(
          new Point(t_x, t_y), 
          TOWER_WIDTH, 
          TOWER_DEPTH, 
          j*TOWER_ROTATION
        );
        tShapes.add(towerShape);
      }
      this.towerShapes.put(plotShape, tShapes);
    }
    
    ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();
    for(Polygon plotShape : this.plotShapes) {
      
      boolean validPlot = true;;
      for (Polygon priorPlotShape : builtPlots) {
        if (plotShape.intersectsPolygon(priorPlotShape)) {
          validPlot = false;
          break;
        }
      }
      
      if (validPlot) {
        
        builtPlots.add(plotShape);
        
        VoxelArray plot = this.morph.make(plotShape, VOXEL_WIDTH, 0, VOXEL_ROTATION, VOXEL_TRANSLATE);
        this.site = this.morph.add(this.site, plot);
        
        VoxelArray plotMassing = new VoxelArray();
        
        VoxelArray podiumTemplate = this.morph.hardCloneVoxelArray(plot);
        podiumTemplate = this.morph.setback(podiumTemplate, SETBACK_DISTANCE);
        podiumTemplate.setVoxelHeight(VOXEL_HEIGHT);
        
        int pZones = (int) random(1, 4);
        for (int i=0; i<pZones; i++) {
          int levels = (int) random(1, 5);
          plotMassing = this.addZone(podiumTemplate, plotMassing, levels, this.random.use());
        }
        
        for(Polygon towerShape : this.towerShapes.get(plotShape)) {
          if(plotShape.containsPolygon(towerShape)) {
            
            VoxelArray towerTemplate = this.morph.hardCloneVoxelArray(plot);
            towerTemplate.setVoxelHeight(VOXEL_HEIGHT);
            towerTemplate = this.morph.clip(towerTemplate, towerShape);
            
            int tZones = (int) random(1, 5);
            for (int i=0; i<tZones; i++) {
              int levels = (int) random(1, 10);
              plotMassing = this.addZone(towerTemplate, plotMassing, levels, this.random.use());
            }
          }
        }
        
        this.massing = this.morph.add(this.massing, plotMassing);
      }
    }
    println("Time to generate site: " +  (millis() - time)/1000.0/(1/60.0) + " frames at 60fps");
  }
  
  VoxelArray addZone(VoxelArray template, VoxelArray base, int levels, Use type) {
    VoxelArray zone = this.morph.extrude(template, levels - 1);
    zone = this.morph.drop(zone, base, CANTILEVER_ALLOWANCE);
    zone.setVoxelUse(type);
    return this.morph.add(base, zone);
  }
}
