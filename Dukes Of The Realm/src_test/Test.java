import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Test extends Application {
    @Override
    public void start(Stage stage) {
        final Spinner<Integer> spinner = new Spinner<>();

        final int initialValue = 3;

        // Value factory.
        final SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, initialValue);

        spinner.setValueFactory(valueFactory);

        Pane root = new Pane();

        root.getChildren().addAll(spinner);

        Scene scene = new Scene(root, 400, 200);

        stage.setTitle("JavaFX Spinner (o7planning.org)");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}