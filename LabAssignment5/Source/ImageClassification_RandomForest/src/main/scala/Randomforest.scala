/**
  * Created by venkatesh on 02/18/17.
  */
import java.nio.file.{Files, Paths}

import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest
import org.apache.spark.mllib.tree.model.RandomForestModel

import scala.collection.mutable

object Randomforest{
  def randomforest(sc: SparkContext): Unit ={
    if (Files.exists(Paths.get(Classification_Paths.RANDOMFOREST))) {
      println(s"${Classification_Paths.RANDOMFOREST} exists, skipping Random Forest model formation..")
      return
    }

    val data = sc.textFile(Classification_Paths.HISTOGRAMS)
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }

    // Split data into training (70%) and test (30%).
    val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 15L)
    val training = splits(0)
    val test = splits(1)

    // Train a RandomForest model.
    //  Empty categoricalFeaturesInfo indicates all features are continuous.
    val numClasses = 4
    val categoricalFeaturesInfo = Map[Int, Int]()
    val maxBins = 100

    val numOfTrees = 4 to(10, 1)
    val strategies = List("all", "sqrt", "log2", "onethird")
    val maxDepths = 3 to(6, 1)
    val impurities = List("gini", "entropy")

    var bestModel: Option[RandomForestModel] = None
    var bestErr = 1.0
    val bestParams = new mutable.HashMap[Any, Any]()
    var bestnumTrees = 0
    var bestFeatureSubSet = ""
    var bestimpurity = ""
    var bestmaxdepth = 0

    numOfTrees.foreach(numTrees => {
      strategies.foreach(featureSubsetStrategy => {
        impurities.foreach(impurity => {
          maxDepths.foreach(maxDepth => {

            println("numTrees " + numTrees + " featureSubsetStrategy " + featureSubsetStrategy +
              " impurity " + impurity + " maxDepth " + maxDepth)

            val model = RandomForest.trainClassifier(training, numClasses, categoricalFeaturesInfo,
              numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

            val predictionAndLabel = test.map { point =>
              val prediction = model.predict(point.features)
              (point.label, prediction)
            }

            val testErr = predictionAndLabel.filter(r => r._1 != r._2).count.toDouble / test.count()
            println("Test Error = " + testErr)
            Classification_Evaluation.evaluateModel(predictionAndLabel)

            if (testErr < bestErr) {
              bestErr = testErr
              bestModel = Some(model)

              bestParams.put("numTrees", numTrees)
              bestParams.put("featureSubsetStrategy", featureSubsetStrategy)
              bestParams.put("impurity", impurity)
              bestParams.put("maxDepth", maxDepth)

              bestFeatureSubSet = featureSubsetStrategy
              bestimpurity = impurity
              bestnumTrees = numTrees
              bestmaxdepth = maxDepth
            }
          })
        })
      })
    })

    println("Best Err " + bestErr)
    println("Best params " + bestParams.toArray.mkString(" "))


    val randomForestModel = RandomForest.trainClassifier(parsedData, numClasses, categoricalFeaturesInfo, bestnumTrees, bestFeatureSubSet, bestimpurity, bestmaxdepth, maxBins)


    // Save and load model
    randomForestModel.save(sc, Classification_Paths.RANDOMFOREST)
    println("***********************************Random Forest Model generated********************************")
  }
}