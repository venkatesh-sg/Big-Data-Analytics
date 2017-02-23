/**
  * Created by venkatesh on 02/12/17.
  */
import java.nio.file.{Files, Paths}

import org.apache.spark.SparkContext
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD

object kMeans{
  def kMeans(sc:SparkContext): Unit ={
    if(Files.exists(Paths.get(Classification_Paths.KMeansModelClusters))){
      println(s"${Classification_Paths.KMeansModelClusters} path exists,Will skip kMeans" )
      return
    }

    //Get features from saved text file from feature extraction
    val data = sc.textFile(Classification_Paths.IMAGE_FEATURES)
    val parsedata = data.map(s=> Vectors.dense(s.split(' ').map(_.toDouble)))


    //Cluster in to 400 clusters
    val K = 400
    val I = 20
    val kMeansclusters =KMeans.train(parsedata,K,I)

    //Sum of Squared distance from cluster centers
    val SSD = kMeansclusters.computeCost(parsedata)
    println("Sum of Squared Distance from cluster centers = "+SSD)
    kMeansclusters.save(sc,Classification_Paths.KMeansModelClusters)
    println("****************************** Cluster saved ****************************")
    sc.parallelize(kMeansclusters.clusterCenters. map(v=> v.toArray.mkString(" "))).saveAsTextFile(Classification_Paths.kMeansModelClusterCenters)
  }
}