package mnist;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Imagette {
    private int[][] pixels;
    private int label; // Étiquette associée à l'image

    public Imagette(int rows, int cols, int label) {
        this.pixels = new int[rows][cols];
        this.label = label;
    }

    public void setPixel(int row, int col, int value) {
        pixels[row][col] = value;
    }

    public double[] getInput() {
        // Convertir la matrice de pixels en un tableau 1D (0 si pixel blanc, 1 sinon)
        double[] pixels1D = new double[pixels.length * pixels[0].length];
        int index = 0;

        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                pixels1D[index++] = pixels[row][col] == 0 ? 0 : 1;
            }
        }

        return pixels1D;
    }

    public double[] getOutput() {
        double[] output = new double[10];
        output[label] = 1;
        return output;
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
}