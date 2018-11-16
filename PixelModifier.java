import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class PixelModifier {
	private WritableImage image;
	private final int THETA_INCREMENT = 2; // Minimum 1. Higher value = more accurate hough transform
	private final int GREYSCALE_THRESHOLD = 50;
	private final int VOTING_THRESHOLD = 116;
	private final int MINIMUM_CIRCLE_RADIUS = 10;
	// 40 --> 9 max
	
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
            }
        }
		
		//Second pass used to draw
		double scale = 255.0 / maxGradient;

		for (int readY = 1; readY < image.getHeight() - 1; readY++) {
            for (int readX = 1; readX < image.getWidth() - 1; readX++) {
                int g = edgeColors[readX][readY];
                g = (int)(g * scale);

                pixelWriter.setColor(readX, readY, Color.rgb(g, g, g));
            }
        }
	}
	
	public void doHoughTransform() {
		PixelReader pixelReader = image.getPixelReader();
		int maxRadius = (int) (Math.min(image.getHeight(), image.getWidth()));
		int voting[][][] = new int[(int) (image.getHeight() - 1)][(int) (image.getWidth() - 1)][maxRadius];
		
		for (int readY = 1; readY < image.getHeight() - 1; readY++) {
			System.out.println("Hough transform: " + readY + "/" + ((int) image.getHeight() - 1));
            for (int readX = 1; readX < image.getWidth() - 1; readX++) {
            	if ((int) (pixelReader.getColor(readX, readY).getRed() * 255) > GREYSCALE_THRESHOLD) {
	            	for (int radius = MINIMUM_CIRCLE_RADIUS; radius < maxRadius; radius++) {
	            		for (int theta = 0; theta < 360; theta+=THETA_INCREMENT) {
	            			int a = (int) (readX - radius * Math.cos(theta * Math.PI / 180));
	            			int b = (int) (readY - radius * Math.sin(theta * Math.PI / 180));	
	            			
	            			if (b < image.getHeight() - 1 && a < image.getWidth() - 1 && b >= 0 && a >= 0) {
	            				// System.out.println((int) (pixelReader.getColor(a, b).getRed() * 255));
	            				voting[a][b][radius] += 1;
	            			}
	            		}
	        		}
	            }
            }
		}
		
		int maxVote = 0;
		PixelWriter pixelWriter = image.getPixelWriter();
		for (int readY = 1; readY < image.getHeight() - 1; readY++) {
			System.out.println("Drawing circle: " + readY + "/" + ((int) image.getHeight() - 1));
            for (int readX = 1; readX < image.getWidth() - 1; readX++) {
            	for (int radius = MINIMUM_CIRCLE_RADIUS; radius < maxRadius; radius++) {
            		if (voting[readX][readY][radius] > 1) {
            			// System.out.println(voting[readX][readY][radius]);
            		}
            		if (voting[readX][readY][radius] > VOTING_THRESHOLD) {
            			if (voting[readX][readY][radius] > maxVote) {
            				maxVote = voting[readX][readY][radius];
            			}
                		for (int theta = 0; theta < 360; theta+=1) {
                			int a = (int) (readX - radius * Math.cos(theta * Math.PI / 180));
                			int b = (int) (readY - radius * Math.sin(theta * Math.PI / 180));
                			
                			if (b < image.getHeight() - 1 && a < image.getWidth() - 1 && b >= 0 && a >= 0) {
                				pixelWriter.setColor(a, b, Color.LIGHTGREEN);
                			}
                		}
            		}
            	}
            }
		}
		
		System.out.println("MAX VOTE: " + maxVote);
	}
}
