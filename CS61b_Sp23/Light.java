package byow.Core;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.TileEngine.TERenderer;
import java.awt.*;

public class Light {
    public static Position position;
    public int radius;
    private boolean isOn;

    public Light(Position position, int radius) {
        this.position = position;
        this.radius = radius;
        this.isOn = false;
    }

    public void turnOn() {
        this.isOn = true;
    }

    public void turnOff() {
        this.isOn = false;
    }

    public boolean isOn() {
        return this.isOn;
    }

    public TETile[][] applyLight(TETile[][] world) {
        TETile[][] newWorld = new TETile[world.length][world[0].length];

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                if (isInBounds(x, y)) {
                    newWorld[x][y] = applyTileLight(world[x][y]);
                } else {
                    newWorld[x][y] = Tileset.NOTHING;
                }
            }
        }

        return newWorld;
    }

    public  void addLightToWorld(TETile[][] world, TETile lightTile) {
        if (isInWorldBounds(position.getX(), position.getY(), world)) {
            world[position.getX()][position.getY()] = lightTile;
        }
    }

    private boolean isInBounds(int x, int y) {
        int distX = Math.abs(position.getX() - x);
        int distY = Math.abs(position.getY() - y);
        return (Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)) <= radius);
    }

    private TETile applyTileLight(TETile tile) {
        if (tile.equals(Tileset.FLOOR)) {
            return Tileset.LIGHT;
        } else if (tile.equals(Tileset.WALL)) {
            return Tileset.LIGHT;
        } else {
            return tile;
        }
    }
    private static boolean isInWorldBounds(int x, int y, TETile[][] world) {
        return (x >= 0 && x < world.length && y >= 0 && y < world[0].length);
    }

    public void lightRadius(Position pos, int radius, TETile[][] world) {
        for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
            for (int y = pos.getY() - radius; y <= pos.getY() + radius; y++) {
                // Update this condition to also check if the point is within the world bounds
                if (isInBounds(x, y) && isInWorldBounds(x, y, world)) {
                    // ... (existing code inside the loop)
                }
            }
        }
    }
    public static void setTilesInRadius(int x, int y, int radius, TETile[][] world, Color color1, Color color2, Color color3) {
        int minX = Math.max(x - radius, 0);
        int maxX = Math.min(x + radius, world.length - 1);
        int minY = Math.max(y - radius, 0);
        int maxY = Math.min(y + radius, world[0].length - 1);
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                double distance = Math.sqrt((i - x) * (i - x) + (j - y) * (j - y));
                if (distance <= 1) {
                    world[i][j] = new TETile(world[i][j].character(), color1, world[i][j].getBackgroundColor(), world[i][j].description());
                } else if (distance <= 2) {
                    world[i][j] = new TETile(world[i][j].character(), color2, world[i][j].getBackgroundColor(), world[i][j].description());
                } else if (distance <= 3) {
                    world[i][j] = new TETile(world[i][j].character(), color3, world[i][j].getBackgroundColor(), world[i][j].description());
                }
            }
        }
    }








    public TETile getTile(int x, int y, TETile[][] world) {
        return world[x][y];
    }

    public void setTile(int x, int y, TETile tile, TETile[][] world) {
        world[x][y] = tile;
    }

}

