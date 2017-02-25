import java.nio.file.{Files, Paths}
/**
  * Created by Venkatesh on 2/24/2017.
  */
object Main{
  def main(args: Array[String]): Unit = {
    //Feature Extraction from Video of registered ibjects
    val objectMainDetection: ObjectDetection = new ObjectDetection

    //Feature Extraction of Test Images
    val Testingfeatures: TestingFeatureExtraction = new TestingFeatureExtraction

    //RandomForest and DecisionTree Classification
    Classification_RF.Randomforest();
    Classification_DT.Decisiontree();
  }
}