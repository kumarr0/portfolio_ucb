package byow.Core;

import byow.TileEngine.*;

public class Hallway {
    public Position p1;
    public Position p2;

    public Hallway(Position p1, Position p2) {
        if (!isHorizontal(p1, p2) && !isVertical(p1, p2)) {
            throw new IllegalArgumentException("Hallway must be horizontal or vertical.");
        }
        this.p1 = p1;
        this.p2 = p2;
    }

    public boolean isHorizontal() {
        return isHorizontal(p1, p2);
    }

    public boolean isVertical() {
        return isVertical(p1, p2);
    }

    public void draw(TETile[][] world, TETile floor, TETile wall) {
        int x1 = p1.getX();
        int y1 = p1.getY();
        int x2 = p2.getX();
        int y2 = p2.getY();
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);

        if (isHorizontal()) {
            for (int x = minX; x <= maxX; x++) {
                world[x][minY] = floor;
                if (world[x][minY - 1] != Tileset.FLOOR) {
                    world[x][minY - 1] = wall;
                }
                if (world[x][minY + 1] != Tileset.FLOOR) {
                    world[x][minY + 1] = wall;
                }
            }
        } else {
            for (int y = minY; y <= maxY; y++) {
                world[minX][y] = floor;
                if (world[minX - 1][y] != Tileset.FLOOR) {
                    world[minX - 1][y] = wall;
                }
                if (world[minX + 1][y] != Tileset.FLOOR) {
                    world[minX + 1][y] = wall;
                }
            }
        }
    }

    private static boolean isHorizontal(Position p1, Position p2) {
        return p1.getY() == p2.getY();
    }

    private static boolean isVertical(Position p1, Position p2) {
        return p1.getX() == p2.getX();
    }
}