object Classification_Paths{
  val PATH = "Input/"
  val INPUT_TRAIN=PATH+"Train"
  val INPUT_TEST=PATH+"Test"
  val OUTPUT="Output/"
  val IMAGE_FEATURES = OUTPUT+"features"
  val KMeansModelClusters= OUTPUT+"KMeansmodel/clusters"
  val kMeansModelClusterCenters=OUTPUT+"KMeansmodel/clustercenters"
  val HISTOGRAMS = OUTPUT + "histograms"
  val DECISIONTREE=OUTPUT+"Decisiontree"

  val IMAGE_CATEGORIES = List("crossing","faces","lights","paths")
}