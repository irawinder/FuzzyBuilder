class FuzzyRandom {
  
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
