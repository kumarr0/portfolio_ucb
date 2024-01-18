package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.io.Serializable;


public class World implements Serializable {
    private final TETile[][] tiles;
    private final int width;
    private final int height;

    public TETile avatar;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new TETile[width][height];
        fillTiles(Tileset.NOTHING);
        this.avatar = Tileset.AVATAR;

    }
    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private void fillTiles(TETile tile) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = tile;
            }
        }
    }
    public void setTiles(TETile[][] newTiles) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = newTiles[x][y];
            }
        }
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public void setTile(int x, int y, TETile tile) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = tile;
        }
    }

    public TETile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[x][y];
        } else {
            return null;
        }
    }
    public boolean isInBounds(int x, int y) {
        int width = tiles.length;
        int height = tiles[0].length;

        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        return true;
    }


    public void updateAvatarTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            avatar = tiles[x][y];
        }
    }





}