/**
  * Created by venkatesh on 02/12/17.
  */
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.mllib.tree.model.{DecisionTreeModel, RandomForestModel}

object Classification_TestInput{
  def classify(sc:SparkContext,path:String): Double ={
    val model = KMeansModel.load(sc, Classification_Paths.KMeansModelClusters)
    val vocabulary = Image_processing.vectorsToMat(model.clusterCenters)

    val Test_Descriptors = Image_processing.bowDescriptors(path,vocabulary)

    val Test_Histogram = Image_processing.matToVector(Test_Descriptors)

    println("Test Histogram size: "+ Test_Histogram.size)

    //val DTModel = DecisionTreeModel.load(sc,Classification_Paths.DECISIONTREE)
    //val p = DTModel.predict(Test_Histogram)

    //val NBModel = NaiveBayesModel.load(sc,Classification_Paths.NAIVEBAYES)
    //val p = NBModel.predict(Test_Histogram)

    val RFmodel = RandomForestModel.load(sc,Classification_Paths.RANDOMFOREST)
    val p = RFmodel.predict(Test_Histogram)

    p
  }
}