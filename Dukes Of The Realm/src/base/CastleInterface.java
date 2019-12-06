package base;

import buildings.Castle;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class CastleInterface extends Application {
	
	Stage window;
	Button button;

	public void start(Stage stage , Castle castle) throws Exception {
		window = stage;
		window.setTitle("Castle stats");

		button = new Button("click me");
		
		//button.setOnAction(e -> );
		
		window.show();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
