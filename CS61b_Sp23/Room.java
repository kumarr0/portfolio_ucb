package byow.Core;

import byow.TileEngine.*;
import java.util.*;

public class Room {
    private final int width;
    private final int height;
    private Position position;
    private final List<Position> tiles;
    private Position lowerLeft;

    public Room(int width, int height, Position position) {
        if (width < 2 || height < 2) {
            throw new IllegalArgumentException("Room is too small.");
        }

        this.width = width;
        this.height = height;
        this.position = position;
        this.tiles = generateTiles();
        this.lowerLeft = position;
    }

    public int getLength() {
        return Math.max(width, height);
    }

    private List<Position> generateTiles() {
        List<Position> result = new ArrayList<>();

        for (int x = position.getX(); x < position.getX() + width; x++) {
            for (int y = position.getY(); y < position.getY() + height; y++) {
                result.add(new Position(x, y));
            }
        }

        return result;
    }

    public static List<Room> generateRooms(int numRooms, int minSize, int maxSize, TETile[][] world, TETile floor, TETile wall, long seed) {
        List<Room> rooms = new ArrayList<>();
        List<Light> lights = new ArrayList<>();

        Random random = new Random(seed);

        for (int i = 0; i < numRooms; i++) {
            int roomWidth = random.nextInt(maxSize - minSize + 1) + minSize;
            int roomHeight = random.nextInt(maxSize - minSize + 1) + minSize;
            int x = random.nextInt(world.length - roomWidth - 1) + 1;
            int y = random.nextInt(world[0].length - roomHeight - 1) + 1;
            Room room = new Room(roomWidth, roomHeight, new Position(x, y));
            boolean overlap = room.isOverlap(rooms);

            if (!overlap) {
                room.addRoomToWorld(world, floor, wall);
                rooms.add(room);

                // Add a light to the room
                Light light = new Light(room.getCenter(), 5);
                light.turnOn();
                lights.add(light);
            }
        }


        // Apply the light effect to the world
        applyLightsToWorld(world, lights);

        return rooms;
    }

    public static TETile[][] applyLightsToWorld(TETile[][] world, List<Light> lights) {
        for (Light light : lights) {
            world = light.applyLight(world);
            light.lightRadius(light.position, light.radius, world);
        }
        return world;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Position getPosition() {
        return position;
    }

    public void addRoomToWorld(TETile[][] world, TETile floorTile, TETile wallTile) {
        for (int x = position.getX(); x < position.getX() + width; x++) {
            for (int y = position.getY(); y < position.getY() + height; y++) {
                if (x == position.getX() || x == position.getX() + width - 1
                        || y == position.getY() || y == position.getY() + height - 1) {
                    world[x][y] = wallTile;
                } else {
                    world[x][y] = floorTile;
                }
            }
        }
    }

    public boolean isOverlap(List<Room> rooms) {
        for (Room other : rooms) {
            if (this == other) {
                continue;
            }
            if (this.position.getX() + this.width > other.position.getX()
                    && other.position.getX() + other.width > this.position.getX()
                    && this.position.getY() + this.height > other.position.getY()
                    && other.position.getY() + other.height > this.position.getY()) {
                return true;
            }
        }
        return false;
    }

    public Position getCenter() {
        int centerX = position.getX() + width / 2;
        int centerY = position.getY() + height / 2;
        return new Position(centerX, centerY);
    }

    public Position[] getClosestPoints(Room other) {
        Position[] closestPoints = new Position[2];

        Position startPoint = new Position(getCenter().x, getCenter().y);
        Position endPoint = new Position(other.getCenter().x, other.getCenter().y);

        if (getCenter().x >= other.getPosition().x && getCenter().x <= other.getPosition().x + other.getWidth() - 1) {
            startPoint.x = getCenter().x;
            endPoint.x = getCenter().x;
        } else if (other.getCenter().x >= getPosition().x && other.getCenter().x <= getPosition().x + getWidth() - 1) {
            startPoint.x = other.getCenter().x;
            endPoint.x = other.getCenter().x;
        }

        if (getCenter().y >= other.getPosition().y && getCenter().y <= other.getPosition().y + other.getHeight() - 1) {
            startPoint.y = getCenter().y;
            endPoint.y = getCenter().y;
        } else if (other.getCenter().y >= getPosition().y && other.getCenter().y <= getPosition().y + getHeight() - 1) {
            startPoint.y = other.getCenter().y;
            endPoint.y = other.getCenter().y;
        }

        closestPoints[0] = startPoint;
        closestPoints[1] = endPoint;

        return closestPoints;
    }



}
