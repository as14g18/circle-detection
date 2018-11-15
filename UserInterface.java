import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
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
                "http://blog.hellonico.info/detect/circles.jpg",
                200,
                0,
                true,
                true
        );
		
		// Create a writable image from image
		WritableImage wImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
		
		PixelModifier pixelMod = new PixelModifier(wImage);
		// convert image to greyscale
		pixelMod.convertToGreyscale();
		
		// apply sobel operator
		pixelMod.doSobelOperator();
		
		
		this.window = primaryStage;
		window.setTitle("Circle detection");
		
		BorderPane borderPane = new BorderPane();
		ImageView imageView = new ImageView((Image) pixelMod.getImage());
		borderPane.setCenter(imageView);
		Scene scene = new Scene(borderPane, image.getWidth(), image.getHeight());
		window.setScene(scene);
		window.show();
	}
}
