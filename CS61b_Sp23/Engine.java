package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private static final String SAVE_FILE_PATH = "saveFile.txt";


    public World world; // Add a world field
    private TETile[][] finalWorld; // Add this line


    public NewGame game;
    public TERenderer renderer = new TERenderer();
    private Light light;

    public Engine() {
        this.world = new World(WIDTH, HEIGHT);
        light = new Light(new Position (WIDTH/2, HEIGHT/2), 4);
    }


    // @source got help from lab + read spec about what actually needs to happen
    public TETile[][] interactWithInputString(String input) {
        input = input.toUpperCase();
        int qIndex = input.indexOf(":Q");

        if (qIndex != -1) {
            String firstPart = input.substring(0, qIndex);
            TETile[][] world = interactWithInputString(firstPart);
            saveGame(game); // Add the saveGame call here
            String secondPart = input.substring(qIndex + 2);
            interactWithInputString("L" + secondPart);

            return world;
        }

        // The rest of the original interactWithInputString code goes here
        NewGame game = null;
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] finalWorld = null;
        boolean saveAndQuitSequence = false;
        long seed = 0;

        for (int i = 0; i < input.length(); i++) {
            char inputChar = input.charAt(i);

            if (inputChar == ':') {
                saveAndQuitSequence = true;
                continue;
            }

            if (inputChar == 'N') {
                int sIndex = input.indexOf('S', i);
                if (sIndex == -1) {
                    System.out.println("Error: 'S' not found in the input string.");
                    return new TETile[WIDTH][HEIGHT];
                }
                seed = Long.parseLong(input.substring(i + 1, sIndex));
                finalWorld = Generator.generateWorld(seed);
                game = new NewGame(finalWorld, seed, 5, 5, true);
                i = sIndex;
            } else if (!saveAndQuitSequence) {
                if (inputChar == 'L') {
                    game = loadGame();
                    if (game == null) {
                        System.out.println("Error: No saved game found.");
                        return new TETile[WIDTH][HEIGHT];
                    }
                    finalWorld = game.getWorld().getTiles();
                } else {
                    if (finalWorld == null) {
                        System.out.println("Error: The world could not be initialized.");
                        return new TETile[WIDTH][HEIGHT];
                    } else {
                        if (game == null) {
                            game = new NewGame(finalWorld, seed, 5, 5, true);
                        }
                        typingInputHelperString(inputChar, game.getWorld().getTiles(), game, saveAndQuitSequence);
                    }
                }
            }

            renderer.renderFrame(game.getWorld().getTiles());

            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();
            displayHUD(mouseX, mouseY);
        }

        return finalWorld;
    }





    private void typingInputHelperString(char c, TETile[][] world, NewGame game, boolean saveAndQuitSequence) {
        if (saveAndQuitSequence) {
            if (c == 'W' || c == 'A' || c == 'S' || c == 'D') {
                moveAvatar(c, world, game);
            }
        } else {
            if (c == 'W' || c == 'A' || c == 'S' || c == 'D') {
                moveAvatar(c, world, game);
            }
        }
    }







    public static void main(String[] args) {
        Engine engine = new Engine();
        TETile[][] finalWorld = engine.interactWithInputString("N543SWWWWWWW:Q");
    }

    public void moveAvatar(char direction, TETile[][] world, NewGame game) {
        int newX = game.xPos;
        int newY = game.yPos;

        switch (direction) {
            case 'W':
                newY++;
                break;
            case 'A':
                newX--;
                break;
            case 'S':
                newY--;
                break;
            case 'D':
                newX++;
                break;
            default:
                return;
        }

        // Check if the new position is valid
        if (world[newX][newY].equals(Tileset.FLOOR)) {
            world[newX][newY] = Tileset.AVATAR;
            world[game.xPos][game.yPos] = Tileset.FLOOR;
            game.updateAvatarPosition(newX, newY); // Update the position of the avatar in the game object
        }
    }


    private void displayMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.black);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.BOLD, 30));
        StdDraw.text(WIDTH / 2.0, HEIGHT * 3.0 / 4.0, "Welcome to My World!");
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "New World (N)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 2, "Load (L)");
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 4, "Quit (Q)");
        StdDraw.show();

    }

    // New method to display the HUD with the tile type
    private void displayHUD(int mouseX, int mouseY) {
        String tileType = getTileTypeAt(mouseX, mouseY);

        // Clear the area where the HUD will be displayed
        StdDraw.setPenColor(Color.black);
        StdDraw.filledRectangle(WIDTH - 4, HEIGHT - 1, 4, 0.5);

        // Display the HUD with the tile type
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
        StdDraw.text(WIDTH - 4, HEIGHT - 1, tileType);

        StdDraw.show();
    }


    public String getTileTypeAt(int x, int y) {
        if (game == null || game.getWorld() == null) {
            return "Out of bounds";
        }
        TETile[][] world = game.getWorld().getTiles();
        if (x < 0 || x >= world.length || y < 0 || y >= world[0].length) {
            return "Out of bounds";
        }
        TETile tileType = world[x][y];
        if (tileType != null) {
            return tileType.description();
        } else {
            return "Out of bounds";
        }
    }


    private long promptForSeed() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(new Font("Arial", Font.PLAIN, 20));
        StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "Enter seed (followed by 'S'): ");
        StdDraw.show();

        StringBuilder seedBuilder = new StringBuilder();
        while (true) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char k = StdDraw.nextKeyTyped();
            if (k == 's' || k == 'S') {
                break;
            } else {
                seedBuilder.append(k);
                StdDraw.clear(StdDraw.BLACK);
                StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0, "Enter seed (followed by 'S'): ");
                StdDraw.text(WIDTH / 2.0, HEIGHT / 2.0 - 2, seedBuilder.toString());
                StdDraw.show();
            }
        }
        String seedStr = seedBuilder.toString();
        return seedStr.hashCode(); // Use the hashCode of the string as the seed
    }


    private char waitForMenuChoice() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (input == 'N' || input == 'L' || input == 'Q') {
                    return input;
                }
            }
        }
    }


    public void interactWithKeyboard() {
        displayMenu();
        char menuChoice = waitForMenuChoice();

        NewGame game = null;


        switch (menuChoice) {
            case 'N':
                long seed = promptForSeed();
                TETile[][] finalWorld = Generator.generateWorld(seed);
                game = new NewGame(finalWorld, seed, 5, 5, true);
                break;
            case 'L':
                game = loadGame();
                if (game == null) {
                    System.out.println("Error: No saved game found.");
                    System.exit(0);
                } else {
                    this.game = game; // Add this line to update the game object in the Engine class
                    break;
                }
            case 'Q':
                saveGame(game); // save the game before quitting
                System.exit(0);
                break;
            default:
                System.out.println("Invalid menu choice.");
                System.exit(0);
                break;
        }

        // Initialize game here
        this.game = game;
        ter.initialize(WIDTH, HEIGHT);

        // Use the loaded world instead of creating a new one if a saved game exists
        // Use the loaded world instead of creating a new one if a saved game exists
        if (game == null || game.getWorld() == null) {
            System.out.println("Error: The world could not be initialized.");
            System.exit(0);
        } else {
            this.finalWorld = game.getWorld().getTiles(); // Update finalWorld
            renderer.renderFrame(game.getWorld().getTiles());
        }


        boolean saveAndQuitSequence = false;


        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = Character.toUpperCase(StdDraw.nextKeyTyped());

                if (input == ':') {
                    saveAndQuitSequence = true;
                } else if (saveAndQuitSequence && input == 'Q') {
                    saveGame(game);
                    System.exit(0);
                } else {
                    saveAndQuitSequence = false;
                    typingInputHelper(input, game.getWorld().getTiles());
                    renderer.renderFrame(game.getWorld().getTiles());
                }
            }

            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();
            displayHUD(mouseX, mouseY);
        }
    }


    public void typingInputHelper(char input, TETile[][] world) {
        switch (input) {
            case 'W':
            case 'A':
            case 'S':
            case 'D':
                moveAvatar(input, world, game);
                break;
        }
    }

    //code partially generated with AI
    public static void saveGame(NewGame game) {
        if (game == null) {
            return;
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(SAVE_FILE_PATH);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(game);
            out.writeInt(game.xPos); // Save the current x position of the avatar
            out.writeInt(game.yPos); // Save the current y position of the avatar
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private NewGame loadGame() {
        try {
            FileInputStream fileIn = new FileInputStream(SAVE_FILE_PATH);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            NewGame savedGame = (NewGame) in.readObject();
            in.close();
            fileIn.close();

            // Create a new NewGame object with the saved world and seed
            NewGame game = new NewGame(savedGame.getWorld().getTiles(), savedGame.seed, savedGame.xPos, savedGame.yPos, true);

            // Replace the default avatar with a floor tile
            TETile[][] worldTiles = game.getWorld().getTiles();
            for (int x = 0; x < worldTiles.length; x++) {
                for (int y = 0; y < worldTiles[0].length; y++) {
                    if (worldTiles[x][y] == Tileset.AVATAR && (x != savedGame.xPos || y != savedGame.yPos)) {
                        worldTiles[x][y] = Tileset.FLOOR;
                    }
                }
            }

            // Update the position of the avatar in the loaded game
            game.updateAvatarPosition(savedGame.xPos, savedGame.yPos);

            return game;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }




    //syntax from lab 13
    public void drawFrame (String s){
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(this.WIDTH / 2, this.HEIGHT / 2, s);
    }
    public void startGameFrame () {
        String displayTxt = "type a number and press S to begin your game";
        drawFrame(displayTxt);

    }

    public void initInteractWithKeyboard(NewGame game) {
        // Initialize game here
        this.game = game;
        ter.initialize(WIDTH, HEIGHT);

        // Use the loaded world instead of creating a new one if a saved game exists
        if (game == null || game.getWorld() == null) {
            System.out.println("Error: The world could not be initialized.");
            System.exit(0);
        } else {
            renderer.renderFrame(game.getWorld().getTiles());
        }

        boolean saveAndQuitSequence = false;

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (input == 'I') {
                    light.turnOn();
                    light.addLightToWorld(finalWorld, Tileset.LIGHT);
                    finalWorld = light.applyLight(finalWorld);
                    game.updateWorld();
                    renderer.renderFrame(game.getWorld().getTiles());

                }
                if (input == 'O') {
                    light.turnOff();
                    game.updateWorld();
                    renderer.renderFrame(game.getWorld().getTiles());

                }

                if (input == ':') {
                    saveAndQuitSequence = true;
                } else if (saveAndQuitSequence && input == 'Q') {
                    saveGame(game);
                    System.exit(0);
                } else {
                    saveAndQuitSequence = false;
                    typingInputHelper(input, game.getWorld().getTiles());
                    renderer.renderFrame(game.getWorld().getTiles());
                }
            }

            int mouseX = (int) StdDraw.mouseX();
            int mouseY = (int) StdDraw.mouseY();
            displayHUD(mouseX, mouseY);
        }
    }
    public class NoSavedGameFoundException extends RuntimeException {
        public NoSavedGameFoundException(String message) {
            super(message);
        }
    }



}