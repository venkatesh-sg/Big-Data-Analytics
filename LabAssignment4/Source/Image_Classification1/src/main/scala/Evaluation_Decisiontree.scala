import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.rdd.RDD

object Evaluation_Decisiontree{
  def evaluateModel(predictionAndLabels: RDD[(Double, Double)]): Unit ={
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val cfMatrix = metrics.confusionMatrix
    println("**********************Confusion matrix ************************")
    println(cfMatrix)
    println("FMeasure :"+metrics.fMeasure)
  }

}