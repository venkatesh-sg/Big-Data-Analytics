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
 * Created by Venkatesh on 2/24/2017.
 */
public class TestingFeatureExtraction {
    public TestingFeatureExtraction() throws IOException {
        String TESTIMAGES="Data/TestImages/";
        String TESTFEATURES ="Data/TestingFeatures/";
        String[] IMAGE_CATEGORIES = {"Button", "Crossing", "Signal"};
        for(int i=0;i<3;i++){
            MBFImage mbfImage = null;
            try {
                mbfImage = ImageUtilities.readMBF(new File(TESTIMAGES+i+".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            DoGSIFTEngine doGSIFTEngine = new DoGSIFTEngine();
            LocalFeatureList<Keypoint> features = doGSIFTEngine.findFeatures(mbfImage.flatten());
            File file = new File(TESTFEATURES+ IMAGE_CATEGORIES[i] + ".txt");
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            for (int x = 0; x < features.size(); x++) {
                double c[] = features.get(x).getFeatureVector().asDoubleVector();
                bw.write(i + ",");
                for (int j = 0; j < c.length; j++) {
                    bw.write(c[j] + " ");
                }
                bw.newLine();
            }
            bw.close();
        }
    }
}
