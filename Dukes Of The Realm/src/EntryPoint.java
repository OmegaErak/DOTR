import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import base.DOTR;

public class EntryPoint extends Application {	
	public void start(Stage primaryStage) throws Exception {
		DOTR game = new DOTR();

		primaryStage.setScene(new Scene(game.createContent()));
		primaryStage.show();
	}
	 	
	public static void main(String[] args) {
		launch(args);
	}
}

    
