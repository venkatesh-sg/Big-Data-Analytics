import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.{DecisionTree, RandomForest}
import org.apache.spark.{SparkConf, SparkContext}

import scala.concurrent.duration.DurationConversions.fromNowConvert.R
/**
  * Created by Venkatesh on 3/1/2017.
  */
object Classification_RF {
  def Randomforest() :String={
    val IMAGE_CATEGORIES = Array("Button", "Crossing", "Signal")
    System.setProperty("hadoop.home.dir", "C:\\winutils")
    // Turn off Info Logger for Consolexxx
    Logger.getLogger("org").setLevel(Level.OFF);
    Logger.getLogger("akka").setLevel(Level.OFF);
    val sparkConf = new SparkConf().setAppName("ImageClassification").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)
    val train = sc.textFile("Data/TrainingFeatures")
    val test = sc.textFile("Data/TestingFeatures")
    val parsedData = train.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }
    val testData1 = test.map(line => {
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    })


    val trainingData = parsedData


    val numClasses = 3
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val maxDepth = 5
    val maxBins = 32
    val featureSubsetStrategy = "auto"
    val numTrees = 5

    val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
      numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

    val classify1 = testData1.map { line =>
      val prediction = model.predict(line.features)
      (line.label, prediction)
    }

    val prediction1 = classify1.groupBy(_._1).map(f => {
      var fuzzy_Pred = Array(0, 0, 0)
      f._2.foreach(ff => {
        fuzzy_Pred(ff._2.toInt) += 1
      })
      var count = 0.0
      fuzzy_Pred.foreach(f => {
        count += f
      })
      var i = -1
      var maxIndex = 3
      val max = fuzzy_Pred.max
      val pp = fuzzy_Pred.map(f => {
        val p = f * 100 / count
        i = i + 1
        if(f == max)
          maxIndex=i
        (i, p)
      })
      (f._1, pp, maxIndex)
    })
    prediction1.foreach(f => {
      println("\n\n\n" + f._1 + " : " + f._2.mkString(";\n"))
    })
    val y = prediction1.map(f => {
      (f._3.toDouble,f._1 )
    })

    y.collect().foreach(println(_))

    sc.stop()
    val R = prediction1.foreach(f=>f._2.foreach(ff=>ff._1))
    val cat=R.toString;
    if(R=="0"){
      val cat ="Button";
    }
    else if(R=="1"){
      val cat = "Crossing";
    }
    else{
      val cat ="signal";
    }
    ("Image Classified as"+cat)
  }
}