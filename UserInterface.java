import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UserInterface extends Application {
	
	private Stage window;
	// private static final int WINDOW_WIDTH = 801;
	// private static final int WINDOW_HEIGHT = 601;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Image image = new Image(
                "http://cliparts.co/cliparts/5iR/XL5/5iRXL5AoT.png",
                200,
                0,
                true,
                true
        );
		
		// convert image to greyscale
		ImageView imageView = new ImageView(image);
		ColorAdjust desaturate = new ColorAdjust();
		desaturate.setSaturation(-1);
		imageView.setEffect(desaturate);
		
		this.window = primaryStage;
		window.setTitle("Circle detection");
		
		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(imageView);
		Scene scene = new Scene(borderPane, image.getWidth(), image.getHeight());
		window.setScene(scene);
		window.show();
	}
}
