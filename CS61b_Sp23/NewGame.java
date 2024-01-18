package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.io.Serializable;
import java.util.*;


public class NewGame implements Serializable {
    public World worldObj;
    public int xPos;
    public int yPos;
    public long seed;

    public NewGame(TETile[][] world, long seed, int xPos, int yPos, boolean placeAvatar) {
        int width = world.length;
        int height = world[0].length;
        this.worldObj = new World(width, height);
        this.worldObj.setTiles(world);
        this.seed = seed;

        // Check if an avatar already exists in the saved game
        boolean avatarExists = false;
        boolean extraAvatarExists = false;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (world[x][y].equals(Tileset.AVATAR)) {
                    if (avatarExists) {
                        // Extra avatar found, replace it with a floor tile
                        this.worldObj.setTile(x, y, Tileset.FLOOR);
                        extraAvatarExists = true;
                    } else {
                        this.xPos = x;
                        this.yPos = y;
                        avatarExists = true;
                    }
                }
            }
        }

        // If an avatar doesn't exist and placeAvatar flag is true, generate a new one
        if (!avatarExists && placeAvatar) {
            putAvatar();
        } else {
            // Update the avatar position
            updateAvatarPosition(this.xPos, this.yPos);
        }

        updateWorld();
    }



    public void putAvatar() {
        List<Room> rooms = Room.generateRooms(10, 5, 15, worldObj.getTiles(), Tileset.FLOOR, Tileset.WALL, seed);

        // Call the connectRooms method to connect the rooms
        Generator.connectRooms(worldObj, rooms);

        Position center = null;
        do {
            Room randRm = rooms.get(new Random(seed).nextInt(rooms.size()));
            center = randRm.getCenter();
        } while (!worldObj.isInBounds(center.getX(), center.getY()) || !worldObj.getTile(center.getX(), center.getY()).equals(Tileset.FLOOR));

        // Generate a new avatar and replace an existing one if it exists
        TETile avatarTile = Tileset.AVATAR;
        boolean avatarExists = false;
        for (int x = 0; x < worldObj.getWidth(); x++) {
            for (int y = 0; y < worldObj.getHeight(); y++) {
                if (worldObj.getTile(x, y).equals(Tileset.AVATAR)) {
                    worldObj.setTile(x, y, Tileset.FLOOR);
                    avatarExists = true;
                    break;
                }
            }
            if (avatarExists) {
                break;
            }
        }
        worldObj.setTile(center.getX(), center.getY(), avatarTile);

        xPos = center.getX();
        yPos = center.getY();
    }

    public void updateWorld() {
        TETile[][] tiles = worldObj.getTiles();
        TETile[][] newTiles = new TETile[tiles.length][tiles[0].length];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                newTiles[x][y] = tiles[x][y];
            }
        }
        worldObj.setTiles(newTiles);
    }


    public World getWorld() {
        return worldObj;
    }

    public void updateAvatarPosition(int newX, int newY) {
        this.xPos = newX;
        this.yPos = newY;
    }
}