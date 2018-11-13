import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object PopularMovies {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    println("==============================================")
    println("| Find most popular movie for a 100k dataset |")
    println("==============================================")

    val sc = new SparkContext("local[*]", "PopularMovies")

    val lines = sc.textFile("src/Data/u.data")

    // Convert data to (MovieId, 1) tuples
    val movies = lines.map(x=>(x.split("\t")(1).toInt,1))

//    movies.take(5).foreach(println)
    val ratingCounts=movies.reduceByKey((x,y)=>x+y)

//    ratingCounts.take(5).foreach(println)
    val flipped = ratingCounts.map(x=>(x._2,x._1))
    val sorted = flipped.sortByKey()
    val result = sorted.collect()

    result.foreach(println) 
  }
}
