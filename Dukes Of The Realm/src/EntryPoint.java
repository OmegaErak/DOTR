import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;

import base.Game;

public class EntryPoint extends Application {	
	public void start(Stage primaryStage) throws Exception {
		Game game = new Game();

		primaryStage.setTitle("Dukes of the realm");
		primaryStage.getIcons().add(new Image("resources/icons/application.png"));
		primaryStage.setScene(new Scene(game.getRoot()));
		primaryStage.show();

		game.run();
	}
	 	
	public static void main(String[] args) {
		launch(args);
	}
}