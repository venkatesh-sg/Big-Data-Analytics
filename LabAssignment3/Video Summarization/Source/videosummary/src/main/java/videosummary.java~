
/**
 * Created by venkatesh on 2/6/17.
 */
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import okhttp3.OkHttpClient;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.model.fit.RANSAC;
import org.openimaj.video.Video;
import org.openimaj.image.MBFImage;
import org.openimaj.video.xuggle.XuggleVideo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;

public class videosummary {

    static Video<MBFImage> Input_video;        //For video that is being summarized
    //static List<MBFImage> Frames_list =new ArrayList<MBFImage>();
    static List<Double> Keypoints = new ArrayList<Double>();
    public static String model=" ";

    public static void main(String args[]) throws Exception {
        //Frames();
        //ainFrames();
        Annotations();

    }


    public static void Frames(){
        Input_video =new XuggleVideo(new File("input/crosswalk.mkv"));

        int i=0;
        for(MBFImage frame : Input_video){

            BufferedImage Bframe= ImageUtilities.createBufferedImageForDisplay(frame);
            File output = new File("output/frames/"+i+".jpg");

            try {
                ImageIO.write(Bframe,"jpg",output);
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
    }
    public static void MainFrames() throws IOException {
        File file = new File("output/frames");
        File[] files = file.listFiles();
        for (int i=0; i<files.length- 1; i++)
        {

            MBFImage image1 = ImageUtilities.readMBF(new File("output/frames/" +i+ ".jpg"));
            MBFImage image2 = ImageUtilities.readMBF(new File("output/frames/"+(i+1)+".jpg"));
            System.out.print(i);
            DoGSIFTEngine engine = new DoGSIFTEngine();
            LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(image1.flatten());
            LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(image2.flatten());
            RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(5.0, 1500,
                    new RANSAC.PercentageInliersStoppingCondition(0.5));
            LocalFeatureMatcher<Keypoint> matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                    new FastBasicKeypointMatcher<Keypoint>(8), modelFitter);
            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);
            double size = matcher.getMatches().size();
            Keypoints.add(size);
        }

        Double max = Collections.max(Keypoints);
        for(int i=0; i<Keypoints.size(); i++){
            if(((Keypoints.get(i))/max < 0.01) || i==0){
                Double name1 = Keypoints.get(i)/max;
                MBFImage image = ImageUtilities.readMBF(new File("output/frames/"+(i+1)+".jpg"));
                BufferedImage bufferedFrame = ImageUtilities.createBufferedImageForDisplay(image);
                String name = "output/mainframes/" + i + "_" + name1.toString() + ".jpg";
                File outputFile = new File(name);
                try {
                    ImageIO.write(bufferedFrame, "jpg", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void Annotations()throws Exception{
        final ClarifaiClient client = new ClarifaiBuilder("Ofr6hOnpopDamQ5fWhmargWwhJLxqFFIN5RipraE", "CzEnwp18RhGqHx_tYMq-whw_36gD1fZOqBcD8V-x")
                .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                .buildSync(); // or use .build() to get a Future<ClarifaiClient>
        client.getToken();
        FileWriter Summary=new FileWriter("output/summary.txt");
        PrintWriter writer = new PrintWriter(Summary);

        File file = new File("output/mainframes");
        File[] files = file.listFiles();
        for (int i=0; i<files.length;i++){
            ClarifaiResponse response = client.getDefaultModels().generalModel().predict()
                    .withInputs(
                            ClarifaiInput.forImage(ClarifaiImage.of(files[i]))
                    )
                    .executeSync();
            List<ClarifaiOutput<Concept>> predictions = (List<ClarifaiOutput<Concept>>) response.get();
            MBFImage image = ImageUtilities.readMBF(files[i]);
            int x = image.getWidth();
            int y = image.getHeight();
            writer.println("\nMain Frame " +(i+1)+" is about:" );

            System.out.println("*************" + files[i] + "***********");
            List<Concept> data = predictions.get(0).data();
            for (int j = 0; j < data.size(); j++) {
                System.out.println(data.get(j).name() + " - " + data.get(j).value());
                if(j<5){
                    writer.println(data.get(j).name());
                }
                image.drawText(data.get(j).name(), (int)Math.floor(Math.random()*x), (int) Math.floor(Math.random()*y), HersheyFont.ASTROLOGY, 20, RGBColour.RED);
            }
            DisplayUtilities.displayName(image, "image" + i);
        }
        writer.close();

    }
}
