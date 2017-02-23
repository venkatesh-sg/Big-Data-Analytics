/**
  * Created by venkatesh on 02/21/17.
  */
import org.apache.spark.{SparkConf, SparkContext}

object APITestImage{
  def Test(string: String): String ={
    System.setProperty("hadoop.home.dir", "C:\\winutils")
    val conf = new SparkConf().setAppName(s"IPApp").setMaster("local[*]")

    val sparkConf = new SparkConf().setAppName("SparkAPITestImage").setMaster("local[*]")
    val sc= SparkContext.getOrCreate(sparkConf)
    val res = Classification_TestInput.classify(sc, string)


    val prediction=s"Test image predicted as : " + Classification_Paths.IMAGE_CATEGORIES(res.toInt)
    prediction
  }
}