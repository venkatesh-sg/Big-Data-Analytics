import Jama.Matrix;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.renderer.MBFImageRenderer;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.transforms.HomographyModel;
import org.openimaj.math.geometry.transforms.HomographyRefinement;
import org.openimaj.math.geometry.transforms.MatrixTransformProvider;
import org.openimaj.math.geometry.transforms.check.TransformMatrixConditionCheck;
import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
import org.openimaj.math.model.fit.RANSAC;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Venkatesh on 2/24/2017.
 */

public class ObjectDetection {

    private ConsistentLocalFeatureMatcher2d<Keypoint> matcher1;
    private ConsistentLocalFeatureMatcher2d<Keypoint> matcher2;
    private ConsistentLocalFeatureMatcher2d<Keypoint> matcher3;
    private List<ConsistentLocalFeatureMatcher2d<Keypoint>> matchers = new ArrayList<ConsistentLocalFeatureMatcher2d<Keypoint>>();

    private MBFImage modelImage;
    private List<MBFImage> modelImages = new ArrayList<MBFImage>();

    final DoGSIFTEngine engine;


    public String REGGISTERED_OBJ ="Data/RegisteredImages/";
    public String INPUT_VIDEO = "Data/InputVideo/crosswalk.mkv";
    public String TRAINING_FEATURES = "Data/TrainingFeatures/Features.txt";

    public ObjectDetection() throws IOException {
        this.engine = new DoGSIFTEngine();
        this.engine.getOptions().setDoubleInitialImage(true);
        this.matcher1 = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                new FastBasicKeypointMatcher<Keypoint>(8));
        this.matcher2 = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                new FastBasicKeypointMatcher<Keypoint>(8));
        this.matcher3 = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                new FastBasicKeypointMatcher<Keypoint>(8));


        this.matchers.add(matcher1);
        this.matchers.add(matcher2);
        this.matchers.add(matcher3);


        final RobustHomographyEstimator ransac = new RobustHomographyEstimator(0.5, 1500,
                new RANSAC.PercentageInliersStoppingCondition(0.6), HomographyRefinement.NONE,
                new TransformMatrixConditionCheck<HomographyModel>(10000));

        for(int i=0;i<3;i++){
            try{
                modelImage = ImageUtilities.readMBF(new File(REGGISTERED_OBJ+i+".JPG"));
                this.modelImages.add(modelImage);
            }catch (IOException e){
                e.printStackTrace();
            }
            this.matchers.get(i).setFittingModel(ransac);
            this.matchers.get(i).setModelFeatures(engine.findFeatures(Transforms.calculateIntensityNTSC(modelImage)));
        }


        Video<MBFImage> video = new XuggleVideo(new File(INPUT_VIDEO));
        int[] count ={0,0,0};
        FileWriter fw = new FileWriter(TRAINING_FEATURES);
        BufferedWriter bw = new BufferedWriter(fw);


        for (MBFImage frame : video){
            final LocalFeatureList<Keypoint> FrameFeatures = this.engine.findFeatures(Transforms.calculateIntensityNTSC(frame));
            final MBFImageRenderer renderer = frame.createRenderer();
            renderer.drawPoints(FrameFeatures, RGBColour.MAGENTA, 3);
            for(int a=0 ;a<3;a++){
                if (this.matchers.get(a).findMatches(FrameFeatures)
                        && ((MatrixTransformProvider) this.matchers.get(a).getModel()).getTransform().cond() < 1e6 ) {
                    try {
                        final Matrix boundsToPoly = ((MatrixTransformProvider) this.matchers.get(a).getModel()).getTransform()
                                .inverse();

                        if (modelImages.get(a).getBounds().transform(boundsToPoly).isConvex()) {

                            renderer.drawShape(this.modelImages.get(a).getBounds().transform(boundsToPoly), 3, RGBColour.RED);

                            if(count[a] <= 10){
                                List<Point2d> vertices = this.modelImages.get(a).getBounds().transform(boundsToPoly).asPolygon().getVertices();
                                int x[] = new int[4], y[] = new int[4];
                                for (int i = 0; i < vertices.size(); i++) {
                                    x[i] = (int) vertices.get(i).getX();
                                    y[i] = (int) vertices.get(i).getY();
                                }
                                Polygon polygon = new Polygon(x, y, 4);
                                for (int i = 0; i < FrameFeatures.size(); i++) {
                                    if (polygon.contains(FrameFeatures.get(i).getX(), FrameFeatures.get(i).getY())) {
                                        double c[] = FrameFeatures.get(i).getFeatureVector().asDoubleVector();
                                        bw.write(a+",");
                                        for (int j = 0; j < c.length; j++) {
                                            bw.write(c[j] + " ");
                                        }
                                        bw.newLine();
                                    }
                                }

                                count[a]++;
                            }

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }

            }
            DisplayUtilities.displayName(frame, "Image");
        }
        bw.close();
    }


}
