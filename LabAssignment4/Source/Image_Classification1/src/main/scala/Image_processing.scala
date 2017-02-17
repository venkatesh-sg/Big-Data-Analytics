import org.apache.spark.mllib.linalg.{DenseVector, Vector}
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_features2d.{BOWImgDescriptorExtractor, DescriptorExtractor, FlannBasedMatcher, KeyPoint}
import org.bytedeco.javacpp.opencv_highgui._
import org.bytedeco.javacpp.opencv_nonfree.SIFT

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
object Image_processing{


  def KeyDescriptors(file: String): Mat ={
    //Loading image in gray scale
    val img1 = imread(file,CV_LOAD_IMAGE_GRAYSCALE)

    //Check if image is empty
    if(img1.empty()){
      println("Image is empty")
    }

    //Detect the keypoints using ORB
    val detector = new SIFT()
    val keypoints1= new KeyPoint()

    val mask = new Mat()
    val descriptors1=new Mat()

    detector.detectAndCompute(img1,mask,keypoints1,descriptors1)

    println(s"No of Keypoints ${keypoints1.size()}")
    println(s"Key Descriptorss ${descriptors1.rows()} rows X ${descriptors1.cols()} colomns")

    descriptors1

  }



  def matToString(mat:Mat): List[String]={
    val imageCvmat = mat.asCvMat()

    val noOfCols = imageCvmat.cols()
    val noOfRows = imageCvmat.rows()

    val fVectors = new mutable.MutableList[String]
    //Channels discarded, take care of this when you are using multiple channels

    for (row <- 0 to noOfRows - 1) {
      val vecLine = new StringBuffer("")
      for (col <- 0 to noOfCols - 1) {
        val pixel = imageCvmat.get(row, col)
        vecLine.append(pixel + " ")
      }

      fVectors += vecLine.toString
    }
    fVectors.toList
  }



  def vectorsToMat(centers: Array[Vector]): Mat = {

    val vocab = new Mat(centers.size, centers(0).size, CV_32FC1)

    var i = 0
    for (c <- centers) {

      var j = 0
      for (v <- c.toArray) {
        vocab.asCvMat().put(i, j, v)
        j += 1
      }
      i += 1
    }

    //    println("The Mat is")
    //    println(vocab.asCvMat().toString)

    vocab
  }




  def bowDescriptors(file: String, dictionary: Mat): Mat = {
    val matcher = new FlannBasedMatcher()
    val detector = new SIFT()
    val extractor = DescriptorExtractor.create("SIFT")
    val bowDE = new BOWImgDescriptorExtractor(extractor, matcher)
    bowDE.setVocabulary(dictionary)
    println(bowDE.descriptorSize() + " " + bowDE.descriptorType())

    val img = imread(file, CV_LOAD_IMAGE_GRAYSCALE)
    if (img.empty()) {
      println("Image is empty")
      -1
    }

    val keypoints = new KeyPoint

    detector.detect(img, keypoints)

    val response_histogram = new Mat
    bowDE.compute(img, keypoints, response_histogram)

    println("Histogram size : " + response_histogram.size().asCvSize().toString)
    println("Histogram : " + response_histogram.asCvMat().toString)
    response_histogram
  }



  def matToVector(mat: Mat): Vector = {
    val imageCvmat = mat.asCvMat()

    val noOfCols = imageCvmat.cols()

    //Channels discarded, take care of this when you are using multiple channels

    val imageInDouble = new Array[Double](noOfCols)
    for (col <- 0 to noOfCols - 1) {
      val pixel = imageCvmat.get(0, col)
      imageInDouble(col) = pixel
    }
    val featureVector = new DenseVector(imageInDouble)
    featureVector
  }




}