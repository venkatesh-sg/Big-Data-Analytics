import java.nio.file.{Files, Paths}

import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel

import scala.collection.mutable

object Decisiontree{
  def decisiontree(sc:SparkContext): Unit ={
    if (Files.exists(Paths.get(Classification_Paths.DECISIONTREE))) {
      println(s"${Classification_Paths.DECISIONTREE} exists, skipping decisiontree model formation..")
      return
    }

    val data = sc.textFile(Classification_Paths.HISTOGRAMS)
    val parsedData = data.map{line=>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }

    //split data in to 70% and 30% to get best param
    val splits= parsedData.randomSplit(Array(0.7,0.3),seed=10L)
    val training = splits(0)
    val test =splits(1)

    //Training DecisionTree model
    val numClasses = 4
    val categoricalFeaturesInfo = Map[Int, Int]()
    val maxBins = 68
    val maxDepths = 3 to(6, 1)
    val impurities = List("gini","entropy")

    //empty Pram for selecting best param
    var bestModel: Option[DecisionTreeModel] = None
    var bestErr = 1.0
    val bestParams = new mutable.HashMap[Any, Any]()
    var bestimpurity = ""
    var bestmaxdepth = 0

    impurities.foreach(impurity => {
      maxDepths.foreach(maxDepth => {

        println(" impurity " + impurity + " maxDepth " + maxDepth)

        val model = DecisionTree.trainClassifier(training, numClasses, categoricalFeaturesInfo, impurity, maxDepth, maxBins)

        val predictionAndLabel = test.map { point =>
          val prediction = model.predict(point.features)
          (point.label, prediction)
        }

        val testErr = predictionAndLabel.filter(r => r._1 != r._2).count.toDouble / test.count()
        println("Test Error = " + testErr)
        Evaluation_Decisiontree.evaluateModel(predictionAndLabel)

        if (testErr < bestErr) {
          bestErr = testErr
          bestModel = Some(model)
          bestParams.put("impurity", impurity)
          bestParams.put("maxDepth", maxDepth)
          bestimpurity = impurity
          bestmaxdepth = maxDepth
        }
      })
    })



    println("Best Error :"+bestErr)
    println("Best Params :"+bestParams.toArray.mkString(" "))

    //Train Model again with best parameters we got
    val decisiontreemodel=DecisionTree.trainClassifier(parsedData,numClasses,categoricalFeaturesInfo,bestimpurity,bestmaxdepth,maxBins)

    //save trained model
    decisiontreemodel.save(sc,Classification_Paths.DECISIONTREE)

    println("************************* Decision Tree Model Generated*************************")
  }
}