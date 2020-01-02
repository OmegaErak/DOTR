package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public abstract class GameIO {
    /**
     * Loads a game from a file
     * @param game The game to be changed
     * @param filepath The path to the save file
     */
    public static void loadGame(Game game, String filepath) {
        try {
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
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
            File myObj = new File(filepath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
