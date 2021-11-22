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
  final int NUM_PLOTS = 1;
  final int NUM_TOWERS = 3;
  
  // local w (vertical) coordinate of ground
  private float CANTILEVER_ALLOWANCE = 0.5;
  
  private FuzzyRandom random;
  private FuzzyMorph morph;
  
  public ArrayList<Polygon> plotShapes;
  public HashMap<Polygon, ArrayList<Polygon>> towerShapes;
  public VoxelArray site, massing;
    
  public Test() {
    this.random = new FuzzyRandom();
    this.morph = new FuzzyMorph();
    this.plotShapes = new ArrayList<Polygon>();
    this.towerShapes = new HashMap<Polygon, ArrayList<Polygon>>();
    this.site = new VoxelArray();
    this.massing = new VoxelArray();
    for (int i=0; i<NUM_PLOTS; i++) {
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
      ArrayList<Polygon> tShapes = new ArrayList<Polygon>();
      for (int j=0; j<NUM_TOWERS; j++) {
        float t_x = p_x + random(-.1, .1) * PLOT_MIN_RADIUS;
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
        
        VoxelArray podium = this.morph.hardCloneVoxelArray(plot);
        podium = this.morph.setback(podium, SETBACK_DISTANCE);
        podium.setVoxelHeight(VOXEL_HEIGHT);
        
        int pZones = (int) random(4);
        for (int i=0; i<pZones; i++) {
          int levels = (int) random(1, 5);
          VoxelArray zone = this.morph.extrude(podium, levels - 1);
          zone = this.morph.drop(zone, massing, CANTILEVER_ALLOWANCE);
          zone.setVoxelUse(this.random.use());
          this.massing = this.morph.add(this.massing, zone);
        }
        
        for(Polygon towerShape : this.towerShapes.get(plotShape)) {
          if(plotShape.containsPolygon(towerShape)) {
            
            VoxelArray tower = this.morph.hardCloneVoxelArray(plot);
            tower.setVoxelHeight(VOXEL_HEIGHT);
            tower = this.morph.clip(tower, towerShape);
            
            int tZones = (int) random(5);
            for (int i=0; i<tZones; i++) {
              int levels = (int) random(1, 10);
              VoxelArray zone = this.morph.extrude(tower, levels - 1);
              zone = this.morph.drop(zone, massing, CANTILEVER_ALLOWANCE);
              zone.setVoxelUse(this.random.use());
              this.massing = this.morph.add(this.massing, zone);
            }
          }
        }
      }
    }
  }
}
