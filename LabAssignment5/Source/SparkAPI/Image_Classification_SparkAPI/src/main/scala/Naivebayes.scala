/**
  * Created by venkatesh on 02/18/17.
  */
import java.nio.file.{Files, Paths}

import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

import scala.collection.mutable

object Naivebayes{
  def nbclassification(sc:SparkContext): Unit ={
    if (Files.exists(Paths.get(Classification_Paths.NAIVEBAYES))) {
      println(s"${Classification_Paths.NAIVEBAYES} exists, skipping decisiontree model formation..")
      return
    }

    val data = sc.textFile(Classification_Paths.HISTOGRAMS)
    val parsedData = data.map{line=>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }

    val nbmodel = NaiveBayes.train(parsedData,lambda = 4.0,modelType = "multinomial")

    nbmodel.save(sc,Classification_Paths.NAIVEBAYES)
    println("************************************Naivebayes model generated*******************************")
  }
}