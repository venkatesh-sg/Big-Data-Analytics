/**
  * Created by venkatesh on 02/12/17.
  */
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.rdd.RDD

object Classification_Evaluation{
  def evaluateModel(predictionAndLabels: RDD[(Double, Double)]): Unit ={
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val cfMatrix = metrics.confusionMatrix
    println("**********************Confusion matrix ************************")
    println(cfMatrix)
    println("FMeasure :"+metrics.fMeasure)
  }

}