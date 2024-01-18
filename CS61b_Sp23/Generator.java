package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.List;
import java.util.Random;


public class Generator {
    private static final int MIN_ROOM_SIZE = 5;
    private static final int MAX_ROOM_SIZE = 15;
    private static final int NUM_ROOMS = 10;

    public static TETile[][] generateWorld(long seed) {
        int width = 80;
        int height = 30;
        World world = new World(width, height);
        Random random = new Random(seed);

        List<Room> rooms = Room.generateRooms(NUM_ROOMS, MIN_ROOM_SIZE, MAX_ROOM_SIZE, world.getTiles(), Tileset.FLOOR, Tileset.WALL, seed);
        connectRooms(world, rooms);

        return world.getTiles();
    }

    static void connectRooms(World world, List<Room> rooms) {
        for (int i = 0; i < rooms.size() - 1; i++) {
            Room r1 = rooms.get(i);
            Room r2 = rooms.get(i + 1);

            Position center1 = r1.getCenter();
            Position center2 = r2.getCenter();

            // Adjust hallway starting and ending points to avoid overlapping with room walls
            if (center1.x < r1.getPosition().x + 1) center1.x += 1;
            if (center1.y < r1.getPosition().y + 1) center1.y += 1;
            if (center1.x > r1.getPosition().x + r1.getWidth() - 2) center1.x -= 1;
            if (center1.y > r1.getPosition().y + r1.getHeight() - 2) center1.y -= 1;

            if (center2.x < r2.getPosition().x + 1) center2.x += 1;
            if (center2.y < r2.getPosition().y + 1) center2.y += 1;
            if (center2.x > r2.getPosition().x + r2.getWidth() - 2) center2.x -= 1;
            if (center2.y > r2.getPosition().y + r2.getHeight() - 2) center2.y -= 1;



            Position corner = new Position(center1.x, center2.y);
            Hallway h1 = new Hallway(center1, corner);
            Hallway h2 = new Hallway(corner, center2);


            h1.draw(world.getTiles(), Tileset.FLOOR, Tileset.WALL);
            h2.draw(world.getTiles(), Tileset.FLOOR, Tileset.WALL);
        }
    }


}