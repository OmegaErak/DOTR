package base;

import buildings.Castle;

import troops.Knight;
import troops.Onager;
import troops.Pikeman;
import troops.Troop;

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
    public static void loadGame(Game game, String filepath, Pane renderLayer) {
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

                int castleLevel = Integer.parseInt(scanner.nextLine());
                int castleTreasure = Integer.parseInt(scanner.nextLine());

                String[] positionStr = scanner.nextLine().split(" ");
                Point2D position = new Point2D(Integer.parseInt(positionStr[0]), Integer.parseInt(positionStr[1]));

                int hasWalls = Integer.parseInt(scanner.nextLine());
                int barrackLevel = Integer.parseInt(scanner.nextLine());

                Castle castle = new Castle(renderLayer, position);
                castle.setOwner(owner);
                castle.setOwnerName(ownerName);
                castle.setLevel(castleLevel);
                castle.setTreasure(castleTreasure);

                if (hasWalls == 0) {
                    castle.setHasWall(false);
                } else {
                    castle.setHasWall(true);
                }

                castle.setBarrackLevel(barrackLevel);

                int nbTroops = Integer.parseInt(scanner.nextLine());

                ArrayList<Troop> castleTroops = new ArrayList<>();
                for (int j = 0; j < nbTroops; ++j) {
                    int troopType = Integer.parseInt(scanner.nextLine());
                    Troop troop = null;
                    if (troopType == 0) {
                        troop = new Knight(renderLayer, castle);
                    } else if (troopType == 1) {
                        troop = new Onager(renderLayer, castle);
                    } else if (troopType == 2) {
                        troop = new Pikeman(renderLayer, castle);
                    } else {
                        throw new RuntimeException("Wrong troop type");
                    }

                    troop.setHP(Integer.parseInt(scanner.nextLine()));
                    castleTroops.add(troop);
                }

                castle.setTroops(castleTroops);
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
                writer.write(castle.getLevel() + "\n");
                writer.write(castle.getTreasure() + "\n");
                writer.write((int)castle.getPosition().getX() + " " + (int)castle.getPosition().getY() + "\n");

                if (castle.hasWall()) {
                    writer.write(Integer.toString(1));
                } else {
                    writer.write(Integer.toString(0));
                }
                writer.write("\n");

                writer.write(castle.getBarrackLevel() + "\n");

                writer.write(castle.getNbTroops() + "\n");
                for (Troop troop : castle.getTroops()) {
                    int troopType = -1;
                    if (troop.getClass() == Knight.class) {
                        troopType = 0;
                    } else if (troop.getClass() == Onager.class) {
                        troopType = 1;
                    } else if (troop.getClass() == Pikeman.class) {
                        troopType = 2;
                    }
                    writer.write(Integer.toString(troopType) + "\n");
                    writer.write(troop.getHP() + "\n");
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
