import base.Game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class of the application.
 * Launches JavaFX and Game classes.
 */
public class EntryPoint extends Application {
	public void start(Stage primaryStage) throws Exception {
		Game game = new Game();

		primaryStage.setTitle("Dukes of the realm");
		primaryStage.setResizable(false);
		primaryStage.getIcons().add(new Image("/icons/application.png"));
		primaryStage.setScene(new Scene(game.getRoot()));
		primaryStage.show();

		game.run();
	}

	public static void main(String[] args) {
		launch(args);
	}
}