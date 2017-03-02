import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Venkatesh on 3/1/2017.
 */
public class FeatureExtraction {
    public FeatureExtraction(String name){
        MBFImage mbfImage = null;
        try {
            mbfImage = ImageUtilities.readMBF(new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DoGSIFTEngine doGSIFTEngine = new DoGSIFTEngine();
        LocalFeatureList<Keypoint> features = doGSIFTEngine.findFeatures(mbfImage.flatten());
        File file = new File("Data/TestFeatures/Features.txt");

        try{
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int x = 0; x < features.size(); x++) {
                double c[] = features.get(x).getFeatureVector().asDoubleVector();
                bw.write( "0,");
                for (int j = 0; j < c.length; j++) {
                    bw.write(c[j] + " ");
                }
                bw.newLine();
            }
            bw.close();
        }catch (IOException e){
            System.out.print(e.getMessage());
        }
    }
}
