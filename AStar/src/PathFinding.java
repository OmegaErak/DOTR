import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PathFinding extends Application{

	
	public static Pane root;


	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(createContent()));
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public Parent createContent() {
		Pane root = new Pane();
		root.setPrefSize(Settings.windowWidth, Settings.windowHeight);
		
		final int nbCasesHor = Settings.windowWidth / Settings.castleSize;
		final int nbCasesVer = Settings.windowHeight / Settings.castleSize;
		Tile tile;
		Path path = new Path(null , null);

		for(int i = 0; i < nbCasesHor; ++i) {
			for(int j = 0; j < nbCasesVer; ++j) {
				Point2D p = new Point2D (i  * Settings.castleSize ,j * Settings.castleSize);
				tile = new Tile(null, Settings.castleSize, Settings.castleSize, Color.BLACK,1 , true , p ,path , root);
				tile.setTranslateX(i * Settings.castleSize);
				tile.setTranslateY(j * Settings.castleSize);
				
					
				root.getChildren().add(tile);
			}
			
		}
		return root;
	}

}
