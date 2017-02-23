/**
  * Created by venkatesh on 02/12/17.
  */
import java.nio.file.{Files, Paths}

import org.apache.spark.SparkContext
import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.rdd.RDD

object Image_Histogram{
  def ImageHistogram(sc:SparkContext,images:RDD[(String, String)]): Unit ={
    if(Files.exists(Paths.get(Classification_Paths.HISTOGRAMS))){
      println(s"${Classification_Paths.HISTOGRAMS} path exists,Will skip creating Histograms" )
      return
    }

    val Model_load = KMeansModel.load(sc, Classification_Paths.KMeansModelClusters)

    val kMeansCenters = sc.broadcast(Model_load.clusterCenters)

    val categories = sc.broadcast(Classification_Paths.IMAGE_CATEGORIES)


    val data=images.map{
      case(name,contents)=>{
        val vocabulary = Image_processing.vectorsToMat(kMeansCenters.value)


        val Bowdesc = Image_processing.bowDescriptors(name.split("file:/")(1),vocabulary)

        val Desclist = Image_processing.matToString(Bowdesc)

        println(" Bow Discriptors String size"+Desclist.size)

        val segments = name.split("/")
        val cat =segments(segments.length-2)
        List(categories.value.indexOf(cat)+ "," + Desclist(0))
      }
    }.reduce((x,y)=> x ::: y)

    val featuresSeq = sc.parallelize(data)

    featuresSeq.saveAsTextFile(Classification_Paths.HISTOGRAMS)
    println("Total size of Bag od Words:"+data.size)
  }
}