
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object FakeFriends {
  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)

    println("Calculate avarage count of friends per age using Spark!")

    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("AvarageFriendsCount")
    val sc = new SparkContext(conf)

    //This function prepares the dataset

    def parseLineForAge(line: String)={
      val fields = line.split(",")
      val age = fields(1).toInt
      val numOfFriends = fields(2).toInt

      (age,numOfFriends)
    }

    def parseLineForNames(line: String)={
      val fields = line.split(",")
      val firstname = fields(0)
      val numOfFriends = fields(2).toInt

      (firstname,numOfFriends)
    }

    //Load our dataset from local file system

    val lines = sc.textFile("src/Data/data.csv")

    //Foreach line of dataset run parseLine to parse data
    val rdd = lines.map(parseLineForAge)
    val rdd2 = lines.map(parseLineForNames)

    println("Initial dataset by age:")
    //(27,789)
    println("")
    rdd.foreach(println)
    println("")

    println("Initial dataset by firstname:")
    //(tasos,1456)
    println("")
    rdd2.foreach(println)
    println("")

    val totals=rdd.mapValues(x=>(x,1)).reduceByKey((x,y)=>(x._1+y._1,x._2+y._2))
    val totals2=rdd2.mapValues(x=>(x,1)).reduceByKey((x,y)=>(x._1+y._1,x._2+y._2))

    println("Total counts by age:")
    println("")
    totals.foreach(println)
    println("")
    println("Avarege friends per age:")
    println("")

    val averageFriendsByAge= totals.mapValues(x=> (x._1 / x._2))
    val averageFriendsByFirstname= totals2.mapValues(x=> (x._1 / x._2))

    val results= averageFriendsByAge.collect()
    results.sorted.foreach(println)

    println("Avarege friends per firsname:")
    println("")

    val results2= averageFriendsByFirstname.collect()
    results2.sorted.foreach(println)
  }
}
