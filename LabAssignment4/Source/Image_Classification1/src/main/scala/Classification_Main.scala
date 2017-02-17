import java.nio.file.{Files, Paths}

import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.bytedeco.javacpp.opencv_highgui._

object Classification_Main{


  def main(args: Array[String]): Unit = {
    //Winutils path for HDFS file system
    System.setProperty("hadoop.home.dir", "C:\\winutils")

    //SparkConfiguration
    val config =new SparkConf().setAppName("Classification").setMaster("local[*]")

    //SparkContext
    val sc = new SparkContext(config)

    //Training Input Image directory
    val Input_images =sc.wholeTextFiles(s"${Classification_Paths.INPUT_TRAIN}/*/*.jpg")

    //Extracts Key Descriptors from the Training set Saves it to a text file
    extractDescriptors(sc,Input_images)

    //Clustering Features in to k clusters using KMeans
    kMeans.kMeans(sc)

    //create histogram for each image to be used as input for classification algorithm
    Image_Histogram.ImageHistogram(sc,Input_images)

    //Random Forest model can be created using Labeled histograms
    Decisiontree.decisiontree(sc)


    val testImages = sc.wholeTextFiles(s"${Classification_Paths.INPUT_TEST}/*/*.jpg")
    val testImageArray = testImages.collect()
    var TestPredictionLabels = List[String]()
    testImageArray.foreach(f=>{
      println(f._1)
      val splitstr =f._1.split("file:/")
      val predictedClass: Double = Classification_TestInput.classify(sc,splitstr(1))
      val segments = f._1.split("/")
      val cat = segments(segments.length - 2)
      val GivenClass = Classification_Paths.IMAGE_CATEGORIES.indexOf(cat)
      println(s"Predicting test image : " + cat + " as " + Classification_Paths.IMAGE_CATEGORIES(predictedClass.toInt))
      TestPredictionLabels = predictedClass + ";" + GivenClass :: TestPredictionLabels
    })

    val PLArray = TestPredictionLabels.toArray

    TestPredictionLabels.foreach(f=>{
      val ff=f.split(";")
      println(ff(0),ff(1))
    })

    val TestPredictionLabelsRDD = sc.parallelize(PLArray)

    val pRDD = TestPredictionLabelsRDD.map(f => {
      val ff = f.split(";")
      (ff(0).toDouble, ff(1).toDouble)
    })

    val accuracy = 1.0 * pRDD.filter(x => x._1 == x._2).count() / testImages.count

    println("Accuracy"+accuracy)
    Evaluation_Decisiontree.evaluateModel(pRDD)







  }


  def extractDescriptors(sc:SparkContext,images:RDD[(String,String)]): Unit ={

    //Check if Image features file exits or not
    if(Files.exists(Paths.get(Classification_Paths.IMAGE_FEATURES))){
      println(s"${Classification_Paths.IMAGE_FEATURES} path exists,Will skip feature extraction" )
      return
    }

    val data =images.map{
      case(name,contents)=>{
        val imagedescriptors=Image_processing.KeyDescriptors(name.split("file:/")(1))
        val featurestring= Image_processing.matToString(imagedescriptors)
        println("size of featurestring"+featurestring.size)
        featurestring
      }
    }.reduce((x,y)=>x ::: y)
    val featureseq=sc.parallelize(data)
    featureseq.saveAsTextFile(Classification_Paths.IMAGE_FEATURES)
    println("Total size of all image feature"+data.size)
  }

}