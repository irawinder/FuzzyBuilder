class Test {
  final int NUM_VERTICES = 4;
  final float PLOT_X = 0;
  final float PLOT_Y = -1000;
  final float PLOT_MIN_RADIUS = 200;
  final float PLOT_MAX_RADIUS = 1000;
  final float VOXEL_WIDTH = 25;
  final float VOXEL_HEIGHT = 10;
  final float VOXEL_ROTATION = 0.25 * PI;
  final Point VOXEL_TRANSLATE = new Point(5, 5);
  final float SETBACK_DISTANCE = 200;
  final int ZONE1_LEVELS = 3;
  final int ZONE2_LEVELS = 4;
  final int ZONE3_LEVELS = 15;
  final int ZONE4_LEVELS = 3;
  final float TOWER_X = 50;
  final float TOWER_Y = -1000;
  final float TOWER_WIDTH = 100;
  final float TOWER_DEPTH = 200;
  final float TOWER_ROTATION = 0.5 * PI;
  
  // local w (vertical) coordinate of ground
  private float CANTILEVER_ALLOWANCE = 1 / 3.0;
  
  private FuzzyRandom random;
  private FuzzyMorph morph;
  
  public Polygon plotShape, towerShape;
  public VoxelArray plot, massing, podium, zone1, zone2, tower, zone3, zone4;
    
  public Test() {
    this.random = new FuzzyRandom();
    this.morph = new FuzzyMorph();
    
    plotShape = this.random.polygon(PLOT_X, PLOT_Y, NUM_VERTICES, PLOT_MIN_RADIUS, PLOT_MAX_RADIUS);
    plot = this.morph.make(plotShape, VOXEL_WIDTH, 0, VOXEL_ROTATION, VOXEL_TRANSLATE);
    podium = this.morph.hardCloneVoxelArray(plot);
    podium = this.morph.setback(podium, SETBACK_DISTANCE);
    podium.setVoxelHeight(VOXEL_HEIGHT);
    zone1 = this.morph.extrude(podium, ZONE1_LEVELS - 1);
    zone1.setVoxelUse(this.random.use());
    massing = zone1;
    zone2 = this.morph.extrude(podium, ZONE2_LEVELS - 1);
    zone2 = this.morph.drop(zone2, massing, CANTILEVER_ALLOWANCE);
    zone2.setVoxelUse(this.random.use());
    towerShape = this.morph.rectangle(new Point(TOWER_X, TOWER_Y), TOWER_WIDTH, TOWER_DEPTH, TOWER_ROTATION);
    if(plotShape.containsPolygon(towerShape)) {
      tower = this.morph.hardCloneVoxelArray(plot);
      tower.setVoxelHeight(VOXEL_HEIGHT);
      tower = this.morph.clip(tower, towerShape);
      zone3 = this.morph.extrude(tower, ZONE3_LEVELS - 1);
      zone3 = this.morph.drop(zone3, massing, CANTILEVER_ALLOWANCE);
      zone3.setVoxelUse(this.random.use());
      massing = this.morph.add(massing, zone3);
      zone4 = this.morph.extrude(tower, ZONE4_LEVELS - 1);
      zone4 = this.morph.drop(zone4, massing, CANTILEVER_ALLOWANCE);
      zone4.setVoxelUse(this.random.use());
      massing = this.morph.add(massing, zone4);
    }
  }
}
