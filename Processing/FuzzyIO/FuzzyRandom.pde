class FuzzyRandom {
  
  final int NUM_VERTICES = 4;
  final float PLOT_X = 0;
  final float PLOT_Y = 0;
  final float PLOT_MIN_RADIUS = 300;
  final float PLOT_MAX_RADIUS = 1000;
  final float VOXEL_WIDTH = 25;
  final float VOXEL_HEIGHT = 10;
  final float VOXEL_ROTATION = 0.15 * PI;
  final Point VOXEL_TRANSLATE = new Point(5, 5);
  final float SETBACK_DISTANCE = 100;
  final float TOWER_WIDTH = 100;
  final float TOWER_DEPTH = 200;
  final float TOWER_ROTATION = 0.25 * PI;
  final int NUM_PLOTS = 1;
  final int NUM_TOWERS = 3;
  
  public FuzzyBuilder fuzzy() {
    
    FuzzyBuilder fuzzy = new FuzzyBuilder();
    
    int time = millis();
    
    fuzzy.plotShapes.clear();
    fuzzy.towerShapes.clear();
    fuzzy.site = new VoxelArray();
    fuzzy.massing = new VoxelArray();
    
    for (int i=0; i<NUM_PLOTS; i++) {
      
      // Random Plot Shape
      float p_x = PLOT_X; // + random(-1, 1) * PLOT_MAX_RADIUS;
      float p_y = PLOT_Y; // + random(-1, 1) * PLOT_MAX_RADIUS;
      Polygon plotShape = fuzzy.random.polygon(
        p_x, 
        p_y,
        NUM_VERTICES, 
        PLOT_MIN_RADIUS, 
        PLOT_MAX_RADIUS
      );
      fuzzy.plotShapes.add(plotShape);
      
      // Random Tower Base
      ArrayList<Polygon> tShapes = new ArrayList<Polygon>();
      for (int j=0; j<NUM_TOWERS; j++) {
        float t_x = p_x + random(-.5, .5) * PLOT_MIN_RADIUS;
        float t_y = p_y + random(-.1, .1) * PLOT_MIN_RADIUS;
        Polygon towerShape = fuzzy.morph.rectangle(
          new Point(t_x, t_y), 
          TOWER_WIDTH, 
          TOWER_DEPTH, 
          j*TOWER_ROTATION
        );
        tShapes.add(towerShape);
      }
      fuzzy.towerShapes.put(plotShape, tShapes);
    }
    
    ArrayList<Polygon> builtPlots = new ArrayList<Polygon>();
    for(Polygon plotShape : fuzzy.plotShapes) {
      
      boolean validPlot = true;;
      for (Polygon priorPlotShape : builtPlots) {
        if (plotShape.intersectsPolygon(priorPlotShape)) {
          validPlot = false;
          break;
        }
      }
      
      if (validPlot) {
        
        builtPlots.add(plotShape);
        
        time = millis();
        
        VoxelArray plot = fuzzy.morph.make(plotShape, VOXEL_WIDTH, 0, VOXEL_ROTATION, VOXEL_TRANSLATE);
        fuzzy.site = fuzzy.morph.add(fuzzy.site, plot);
        
        //println("Time to generate site: " +  (millis() - time)/1000.0/(1/60.0) + " frames at 60fps");
        //time = millis();
        
        VoxelArray plotMassing = new VoxelArray();
        
        VoxelArray podiumTemplate = fuzzy.morph.hardCloneVoxelArray(plot);
        podiumTemplate = fuzzy.morph.setback(podiumTemplate, SETBACK_DISTANCE);
        podiumTemplate.setVoxelHeight(VOXEL_HEIGHT);
        
        int pZones = (int) random(1, 4);
        for (int i=0; i<pZones; i++) {
          int levels = (int) random(1, 5);
          plotMassing = fuzzy.addZone(podiumTemplate, plotMassing, levels, fuzzy.random.use());
        }
        
        //println("Time to generate podiums: " +  (millis() - time)/1000.0/(1/60.0) + " frames at 60fps");
        //time = millis();
        
        for(Polygon towerShape : fuzzy.towerShapes.get(plotShape)) {
          if(plotShape.containsPolygon(towerShape)) {
            
            VoxelArray towerTemplate = fuzzy.morph.hardCloneVoxelArray(plot);
            towerTemplate.setVoxelHeight(VOXEL_HEIGHT);
            towerTemplate = fuzzy.morph.clip(towerTemplate, towerShape);
            
            int tZones = (int) random(1, 5);
            for (int i=0; i<tZones; i++) {
              int levels = (int) random(1, 10);
              plotMassing = fuzzy.addZone(towerTemplate, plotMassing, levels, fuzzy.random.use());
            }
          }
        }
        
        fuzzy.massing = fuzzy.morph.add(fuzzy.massing, plotMassing);
        //println("Time to generate towers: " +  (millis() - time)/1000.0/(1/60.0) + " frames at 60fps");
      }
    }
    println("Time to generate: " +  (millis() - time)/1000.0/(1/60.0) + " frames at 60fps");
    
    return fuzzy;
  }
  
  /**
   * generates a random polygon shape
   * 
   * @param num_pts    number of corners to include in new random polygon
   * @param x_center   x-coordinate or polygon center
   * @param y_center   y-coordinate or polygon center
   * @param min_radius min distance of corner from center point
   * @param max_radius max distance of corner from center point
   * @return random polygon 
   */
  public Polygon polygon(float x_center, float y_center, int num_pts, float min_radius, float max_radius) {
  
    Polygon polygon = new Polygon();
    ArrayList<Float> angle, radius;
    Random rand;
    float total;
  
    if (num_pts > 2) {
  
      // Initialize
      angle = new ArrayList<Float>();
      radius = new ArrayList<Float>();
      rand = new Random();
      total = 0;
      polygon.clear();
  
      for (int i = 0; i < num_pts; i++) {
  
        // Generate random numbers relatively proportional to angle size
        float random_number = rand.nextFloat();
        total += random_number;
        angle.add(random_number);
  
        // Generate random radius values
        float variance = (max_radius - min_radius) * rand.nextFloat();
        float random_radius = min_radius + variance;
        radius.add(random_radius);
      }
  
      // Fit angle size to radian value
      for (int i = 0; i < angle.size(); i++) {
        float mag = angle.get(i);
        angle.set(i, mag * 2 * (float) Math.PI / total);
      }
  
      // generate each point around a circle
      float a = 0;
      for (int i = 0; i < num_pts; i++) {
        a += angle.get(i);
        float r = radius.get(i);
        float x = (float) (r * Math.cos(a));
        float y = (float) (r * Math.sin(a));
        Point p = new Point(x, y);
        polygon.addVertex(p);
      }
  
      // shift polygon's coordinate system
      polygon.translate(x_center, y_center);
  
    } else {
      System.out.print("Not enough points to make polygon");
    }
    
    return polygon;
  }
  
  /**
   * Get a random use
   */
  public Use use() {
    int index = (int) random(Use.values().length);
    return Use.values()[index];
  }
}
