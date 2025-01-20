package mnist;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Donnees {
    private Imagette[] imagettes;

    public Donnees(String imageFilePath, String labelFilePath) {
        try (DataInputStream imageStream = new DataInputStream(new FileInputStream(imageFilePath));
             DataInputStream labelStream = new DataInputStream(new FileInputStream(labelFilePath))) {

            // Lire les métadonnées des fichiers
            int imageMagicNumber = imageStream.readInt();
            int numberOfImages = imageStream.readInt();
            int numberOfRows = imageStream.readInt();
            int numberOfCols = imageStream.readInt();

            int labelMagicNumber = labelStream.readInt();
            int numberOfLabels = labelStream.readInt();

            // Vérification des Magic Numbers
            if (imageMagicNumber != 2051 || labelMagicNumber != 2049 || numberOfImages != numberOfLabels) {
                throw new IllegalArgumentException("Les fichiers image et étiquette ne correspondent pas ou sont corrompus.");
            }

            // Charger les imagettes avec les étiquettes
            imagettes = new Imagette[numberOfImages];
            for (int i = 0; i < numberOfImages; i++) {
                int label = labelStream.readUnsignedByte();
                Imagette imagette = new Imagette(numberOfRows, numberOfCols, label);

                for (int row = 0; row < numberOfRows; row++) {
                    for (int col = 0; col < numberOfCols; col++) {
                        int pixelValue = imageStream.readUnsignedByte();
                        imagette.setPixel(row, col, pixelValue);
                    }
                }

                imagettes[i] = imagette;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Imagette[] getImagettes() {
        return imagettes;
    }

    public Imagette getImagette(int index) {
        return imagettes[index];
    }

    public int getTaille() {
        return imagettes.length;
    }

    public double[][] getInputs() {
        double[][] inputs = new double[imagettes.length][];
        for (int i = 0; i < imagettes.length; i++) {
            inputs[i] = imagettes[i].getInput();
        }
        return inputs;
    }

    public double[][] getOutputs() {
        double[][] outputs = new double[imagettes.length][10];
        for (int i = 0; i < imagettes.length; i++) {
            outputs[i] = imagettes[i].getOutput();
        }
        return outputs;
    }
}
