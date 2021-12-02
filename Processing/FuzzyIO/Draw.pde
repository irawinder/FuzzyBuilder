void drawGrids(float gridSize, int gridUnits, float z) {
  this.drawGrid(gridSize, gridUnits, -z);
  this.drawGrid(gridSize, gridUnits, +z);
}

void drawGrid(float gridSize, int gridUnits, float z) {
  stroke(DEFAULT_COLOR, 10);
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
  stroke(100);
  for(Line edge : p.edges) {
    line(edge.o.x, edge.o.z, edge.o.y, edge.f.x, edge.f.z, edge.f.y);
  }
}

void drawVoxels(VoxelArray voxelArray) {
  noStroke();
  for(Voxel voxel : voxelArray.voxelList) {
    int col = this.useColor(voxel.type);
    fill(col, 100);
    pushMatrix();
    translate(voxel.location.x, - voxel.location.z - 0.5 * voxel.height, voxel.location.y);
    rotateY(-voxel.rotation);
    box(0.9 * voxel.width, 0.9 * voxel.height, 0.9 * voxel.width);  
    popMatrix();
  }
}

void drawTiles(VoxelArray voxelArray) {
  noFill();
  rectMode(CENTER);
  for(Voxel voxel : voxelArray.voxelList) {
    int col = this.useColor(voxel.type);
    stroke(col, 100);
    pushMatrix();
    translate(voxel.location.x, - voxel.location.z - 0.5 * voxel.height, voxel.location.y);
    rotateY(-voxel.rotation);
    rotateX(0.5 * PI);
    rect(0, 0, 0.9 * voxel.width, 0.9 * voxel.width); 
    popMatrix();
  }
  rectMode(CORNER);
}

public int useColor(Use use) {
  if(use != null) {
    switch(use) {
      case Office:
        return #6666CC;
      case Residential:
        return #CCCC66;
      case Retail:
        return #CC66CC;
      case Landscape:
        return #66CC66;
      case Carpark:
        return #CCCCCC;
      case Government:
        return #66CCCC;
      case Hotel:
        return #6666CC;
      case Convention:
        return #CC6666;
      case Community:
        return #CC6666;
      default:
        return DEFAULT_COLOR;
    }
  } else {
    return DEFAULT_COLOR;
  }
}
