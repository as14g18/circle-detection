import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PixelModifier {
	private WritableImage image;
	private final int THRESHOLD = 500;
	
	public PixelModifier(WritableImage image) {
		this.image = image;
	}
	
	public Image getImage() {
		return image;
	}
	
	public void convertToGreyscale() {
		PixelWriter pixelWriter = image.getPixelWriter();
		PixelReader pixelReader = image.getPixelReader();
		for (int readY = 0; readY < image.getHeight(); readY++) {
            for (int readX = 0; readX < image.getWidth(); readX++) {
                Color color = pixelReader.getColor(readX, readY);
                int greyscaleColour = (int) Math.round(((color.getRed() + color.getBlue() + color.getGreen()) / 3) * 255);
                pixelWriter.setColor(readX, readY, Color.rgb(greyscaleColour, greyscaleColour, greyscaleColour));
            }
        }
	}
	
	public void doSobelOperator() {
		PixelWriter pixelWriter = image.getPixelWriter();
		PixelReader pixelReader = image.getPixelReader();
		int[][] edgeColors = new int[(int) image.getWidth()][(int) image.getHeight()];
        int maxGradient = -1;
        
        // First pass used to determine scale
		for (int readY = 1; readY < image.getHeight() - 1; readY++) {
            for (int readX = 1; readX < image.getWidth() - 1; readX++) {
                int val00 = (int) (pixelReader.getColor(readX - 1, readY - 1).getRed() * 255);
                int val01 = (int) (pixelReader.getColor(readX, readY - 1).getRed() * 255);
                int val02 = (int) (pixelReader.getColor(readX + 1, readY - 1).getRed() * 255);

                int val10 = (int) (pixelReader.getColor(readX - 1, readY).getRed() * 255);
                int val11 = (int) (pixelReader.getColor(readX, readY).getRed() * 255);
                int val12 = (int) (pixelReader.getColor(readX + 1, readY).getRed() * 255);

                int val20 = (int) (pixelReader.getColor(readX - 1, readY + 1).getRed() * 255);
                int val21 = (int) (pixelReader.getColor(readX, readY + 1).getRed() * 255);
                int val22 = (int) (pixelReader.getColor(readX + 1, readY + 1).getRed() * 255);
                
                int gx =  ((-1 * val00) + (0 * val01) + (1 * val02)) 
                        + ((-2 * val10) + (0 * val11) + (2 * val12))
                        + ((-1 * val20) + (0 * val21) + (1 * val22));

                int gy =  ((-1 * val00) + (-2 * val01) + (-1 * val02))
                        + ((0 * val10) + (0 * val11) + (0 * val12))
                        + ((1 * val20) + (2 * val21) + (1 * val22));
                
                double gval = Math.sqrt((gx * gx) + (gy * gy));
                int g = (int) gval;
                if(maxGradient < g) {
                    maxGradient = g;
                }
                
                edgeColors[readX][readY] = g;
                
                
                
                /*
                if (g < THRESHOLD) {
                	pixelWriter.setColor(readX, readY, Color.WHITE);
                } else {
                	pixelWriter.setColor(readX, readY, Color.BLACK);
                }
                */
            }
        }
		
		//Second pass used to draw
		double scale = 255.0 / maxGradient;

		for (int readY = 1; readY < image.getHeight() - 1; readY++) {
            for (int readX = 1; readX < image.getWidth() - 1; readX++) {
                int g = edgeColors[readX][readY];
                g = (int)(g * scale);
                // g = 0xff000000 | (g << 16) | (g << 8) | g;

                pixelWriter.setColor(readX, readY, Color.rgb(g, g, g));
            }
        }
	}

}
