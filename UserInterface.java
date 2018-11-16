import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
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
                "file:///C:/Users/Akhilesh/Desktop/Capture.PNG",
                200,
                200,
                false,
                true
        );
		
		// Create a writable image from image
		WritableImage wImage = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
		PixelModifier pixelMod = new PixelModifier(wImage);
	
		// Perform steps for circle detection
		ImageView originalView = new ImageView(makeCopy(pixelMod.getImage()));
		pixelMod.convertToGreyscale(); System.out.println("[CONVERTED TO GREYSCALE]");
		ImageView greyScale = new ImageView(makeCopy(pixelMod.getImage()));
		pixelMod.doSobelOperator(); System.out.println("[APPLIED SOBEL OPERATOR]");
		ImageView sobelView = new ImageView(makeCopy(pixelMod.getImage()));
		pixelMod.doHoughTransform(); System.out.println("[APPLIED HOUGH TRANSFORM]");
		ImageView houghView = new ImageView(makeCopy(pixelMod.getImage()));
		
		
		
		this.window = primaryStage;
		window.setTitle("Circle detection");
		
		FlowPane flow = addFlowPane(new ImageView[] {
				originalView, greyScale, sobelView, houghView
		});
		Scene scene = new Scene(flow, image.getWidth() * 4.1, image.getHeight());
		window.setScene(scene);
		window.show();
	}
	
	public FlowPane addFlowPane(ImageView[] imageList) {
	    FlowPane flow = new FlowPane();
	    flow.setPadding(new Insets(5, 0, 5, 0));
	    flow.setVgap(4);
	    flow.setHgap(4);
	    flow.setPrefWrapLength(170); // preferred width allows for two columns
	    flow.setStyle("-fx-background-color: DAE6F3;");

	    ImageView pages[] = new ImageView[4];
	    for (int i=0; i<4; i++) {
	        pages[i] = imageList[i];
	        flow.getChildren().add(pages[i]);
	    }

	    return flow;
	}
	
	Image makeCopy(Image original) {
	    ImageView i = new ImageView(original);
	    i.setFitHeight(200); i.setFitWidth(200);

	    return i.snapshot(new SnapshotParameters(), new WritableImage(200, 200));
	}
}
