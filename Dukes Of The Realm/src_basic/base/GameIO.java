package base;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GameIO {
    public GameIO() {
    }

    public void loadGame(Game game, String filepath) {
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

    public void saveGame(Game game, String filepath) {
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
