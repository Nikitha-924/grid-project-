package Project;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class GridImageAnalyzer {

    public static void main(String[] args) throws Exception {
    	

        // ----------- SETTINGS ------------
        String imagePath = "Grid image.jpg";  // Change to your image file
        int rows = 4;                     // Number of grid rows
        int cols = 4;                     // Number of grid cols
        int edgeThreshold = 40;          // Simple edge detection threshold
        // ---------------------------------

        BufferedImage img = ImageIO.read(new File(imagePath));
        int width = img.getWidth();
        int height = img.getHeight();
        int cellW = width / cols;
        int cellH = height / rows;

        System.out.println("Image Loaded: " + width + "x" + height);
        System.out.println("Analyzing " + rows + "x" + cols + " grid...\n");

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                int x0 = c * cellW;
                int y0 = r * cellH;
                int wC = (c == cols - 1) ? width - x0 : cellW;
                int hC = (r == rows - 1) ? height - y0 : cellH;

                long sumR = 0, sumG = 0, sumB = 0;
                long sumLum = 0;
                int edgeCount = 0;
                int pixelCount = wC * hC;

                for (int y = y0; y < y0 + hC; y++) {
                    for (int x = x0; x < x0 + wC; x++) {
                        int rgb = img.getRGB(x, y);

                        int rpx = (rgb >> 16) & 0xFF;
                        int gpx = (rgb >> 8) & 0xFF;
                        int bpx = rgb & 0xFF;

                        // Summations
                        sumR += rpx;
                        sumG += gpx;
                        sumB += bpx;

                        // Luminance (Rec. 709)
                        int lum = (int)(0.2126 * rpx + 0.7152 * gpx + 0.0722 * bpx);
                        sumLum += lum;

                        // Edge detection with simple gradient
                        if (x > 0 && y > 0 && x < width - 1 && y < height - 1) {
                            int rgbLeft = img.getRGB(x - 1, y) & 0xFF;
                            int rgbRight = img.getRGB(x + 1, y) & 0xFF;
                            int rgbUp = img.getRGB(x, y - 1) & 0xFF;
                            int rgbDown = img.getRGB(x, y + 1) & 0xFF;

                            int gx = Math.abs(rgbRight - rgbLeft);
                            int gy = Math.abs(rgbDown - rgbUp);

                            if (Math.sqrt(gx * gx + gy * gy) > edgeThreshold) {
                                edgeCount++;
                            }
                        }
                    }
                }

                int avgR = (int)(sumR / pixelCount);
                int avgG = (int)(sumG / pixelCount);
                int avgB = (int)(sumB / pixelCount);
                int avgLum = (int)(sumLum / pixelCount);
                double edgePercent = (edgeCount * 100.0) / pixelCount;

                // -------- Print result ----------
                System.out.printf("Cell (%d, %d):\n", r, c);
                System.out.printf("  Avg RGB      = (%d, %d, %d)\n", avgR, avgG, avgB);
                System.out.printf("  Luminance    = %d\n", avgLum);
                System.out.printf("  Edge %%        = %.2f%%\n", edgePercent);
                System.out.printf("  Pixels       = %d\n\n", pixelCount);
            }
        }
    }
}