/**
  * Created by venkatesh on 1/30/17.
  */

import org.apache.spark.{SparkConf, SparkContext}
object WordCombinations{
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setAppName("WordCombinations").setMaster("local[*]")
    //New SparkContext
    val sc=new SparkContext(sparkConf)
    //Input from About_UMKC file
    val input=sc.textFile("About_UMKC")
    //mapping every two words after spliting
    val wc=input.flatMap(line=>{line.split(" ").sliding(2)}).map( p => (p.mkString(" "),1))
    //reducing by key and sorting in decending order
    val output=wc.reduceByKey(_+_,1).sortBy(_._2,false)
    //Saving to output file
    output.saveAsTextFile("output")
  }
}