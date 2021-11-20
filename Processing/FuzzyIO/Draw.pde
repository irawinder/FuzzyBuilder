void drawGrids(float gridSize, int gridUnits, float z) {
  this.drawGrid(gridSize, gridUnits, -z);
  this.drawGrid(gridSize, gridUnits, +z);
}

void drawGrid(float gridSize, int gridUnits, float z) {
  stroke(DEFAULT_COLOR, 20);
  float gridWidth = gridSize / gridUnits;
  for (int u=0; u<gridUnits; u++) {
    line(-0.5 * gridSize, z, -0.5 * gridSize + u * gridWidth, 0.5 * gridSize, z, -0.5 * gridSize + u * gridWidth);
    line(-0.5 * gridSize + u * gridWidth, z, -0.5 * gridSize, -0.5 * gridSize + u * gridWidth, z, 0.5 * gridSize);
  }
}

void drawCursor() {
  stroke(DEFAULT_COLOR, 200);
  pushMatrix(); translate(width/2, height/2);
  line(0, -20, 0, 20);
  line(-20, 0, 20, 0);
  popMatrix();
}

void drawShape(Polygon p) {
  noFill();
  stroke(255);
  for(Line edge : p.edge) {
    line(edge.o.x, edge.o.z, edge.o.y, edge.f.x, edge.f.z, edge.f.y);
  }
}

void drawVoxels(VoxelArray voxelArray) {
  fill(255, 100);
  noStroke();
  for(Voxel voxel : voxelArray.voxelList) {
    pushMatrix();
    translate(voxel.location.x, - voxel.location.z, voxel.location.y);
    rotateY(voxel.rotation);
    box(0.9 * voxel.width, 0.9 * voxel.height, 0.9 * voxel.width);  
    popMatrix();
  }
}
