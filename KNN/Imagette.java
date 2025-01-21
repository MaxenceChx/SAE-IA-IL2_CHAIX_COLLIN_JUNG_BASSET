import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Imagette {
    private int[][] pixels;
    private int label; // Étiquette associée à l'image

    public Imagette(int rows, int cols, int label) {
        this.pixels = new int[rows][cols];
        this.label = label;
    }

    public void setPixel(int row, int col, int value) {
        pixels[row][col] = value;
    }

    public int getLabel() {
        return label;
    }

    public void saveAsImage(String fileName) {
        int rows = pixels.length;
        int cols = pixels[0].length;

        BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int gray = pixels[row][col];
                int rgb = (gray << 16) | (gray << 8) | gray;
                image.setRGB(col, row, rgb);
            }
        }

        try {
            ImageIO.write(image, "png", new File(fileName));
            System.out.println("Image saved: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double distance(Imagette other) {
        double sum = 0.0;
        int rows = pixels.length;
        int cols = pixels[0].length;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int diff = pixels[row][col] - other.pixels[row][col];
                sum += diff * diff;
            }
        }

        return Math.sqrt(sum);
    }
}