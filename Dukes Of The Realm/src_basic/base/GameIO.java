package base;

import buildings.Castle;

import troops.Knight;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class GameIO {
    /**
     * Loads a game from a file
     * @param game The game to be changed
     * @param filepath The path to the save file
     */
    public static void loadGame(Game game, String filepath, Pane rendererLayer) {
        try {
            File file = new File(filepath);

            Scanner scanner = new Scanner(file);
            int currentDay = Integer.parseInt(scanner.nextLine());
            game.setCurrentDay(currentDay);

            int nbCastles = Integer.parseInt(scanner.nextLine());

            ArrayList<Castle> gameCastles = new ArrayList<>();
            for (int i = 0; i < nbCastles; ++i) {
                int owner = Integer.parseInt(scanner.nextLine());
                String ownerName = scanner.nextLine();

                String[] positionStr = scanner.nextLine().split(" ");
                Point2D position = new Point2D(Integer.parseInt(positionStr[0]), Integer.parseInt(positionStr[1]));

                Castle castle = new Castle(rendererLayer, position);
                castle.setOwner(owner);
                castle.setOwnerName(ownerName);

                int nbKnights = Integer.parseInt(scanner.nextLine());

                ArrayList<Knight> castleKnights = new ArrayList<>();
                for (int j = 0; j < nbKnights; ++j) {
                    Knight knight = new Knight(rendererLayer, castle);
                    knight.setHP(Integer.parseInt(scanner.nextLine()));

                    castleKnights.add(knight);
                }

                castle.setKnights(castleKnights);
                gameCastles.add(castle);
            }

            game.setCastles(gameCastles);

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a game into a file
     * @param game The game to save
     * @param filepath The path to the save file
     */
    public static void saveGame(Game game, String filepath) {
        try {
            File file = new File(filepath);
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(game.getCurrentDay() + "\n");
            writer.write(game.getNbCastles() + "\n");

            for (Castle castle : game.getCastles()) {
                writer.write(castle.getOwner() + "\n");
                writer.write(castle.getOwnerName() + "\n");
                writer.write((int)castle.getPosition().getX() + " " + (int)castle.getPosition().getY() + "\n");

                writer.write(castle.getNbKnights() + "\n");
                for (Knight knight : castle.getKnights()) {
                    writer.write(knight.getHP() + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
