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
  final float SETBACK_DISTANCE = 100;
  final int PODIUM_EXTRUSION = 4;
  final int TOWER_EXTRUSION = 14;
  final float TOWER_X = 0;
  final float TOWER_Y = -1000;
  final float TOWER_WIDTH = 100;
  final float TOWER_DEPTH = 200;
  final float TOWER_ROTATION = 0.25 * PI;
  
  private FuzzyRandom random;
  private FuzzyMorph morph;
  
  public Polygon plotShape, towerShape;
  public VoxelArray plot, massing, podium, tower;
    
  public Test() {
    this.random = new FuzzyRandom();
    this.morph = new FuzzyMorph();
    
    plotShape = this.random.polygon(PLOT_X, PLOT_Y, NUM_VERTICES, PLOT_MIN_RADIUS, PLOT_MAX_RADIUS);
    plot = this.morph.make(plotShape, VOXEL_WIDTH, 0, VOXEL_ROTATION, VOXEL_TRANSLATE);
    podium = this.morph.hardCloneVoxelArray(plot);
    podium.setVoxelHeight(VOXEL_HEIGHT);
    podium = this.morph.setback(podium, SETBACK_DISTANCE);
    podium = this.morph.extrude(podium, PODIUM_EXTRUSION);
    towerShape = this.morph.rectangle(new Point(TOWER_X, TOWER_Y), TOWER_WIDTH, TOWER_DEPTH, TOWER_ROTATION);
    tower = this.morph.hardCloneVoxelArray(plot);
    tower.setVoxelHeight(VOXEL_HEIGHT);
    tower = this.morph.clip(tower, towerShape);
    tower = this.morph.extrude(tower, TOWER_EXTRUSION);
    tower = this.morph.drop(tower, podium);
    massing = this.morph.add(podium, tower);
  }
}
