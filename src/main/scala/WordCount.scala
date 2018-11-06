import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}

object WordCount {
  def main(args: Array[String]): Unit = {
    println("Word Count using flatMap")

    Logger.getLogger("org").setLevel(Level.ERROR)

    //Create Spark Context
    val sc = new SparkContext("local[*]","MinTemperatures")

    //Load data
    val lines = sc.textFile("src/Data/book.txt")

    // split the hole document into words
//    val words = lines.flatMap(x => x.split(" "))

    //Split using a requar expression that extracts words
    val words = lines.flatMap(x => x.split("\\W+"))

    //Normalize everything to lowercase
    val lowercaseWords = words.map(x=>x.toLowerCase())

//    println("Split the hole document into words")
//    println("Sample from 10 first words:")
//    println("")
//    lowercaseWords.take(10).foreach(println)
//    println("")
//
//    //Count up the occurrences of each word
//    println("Count up the occurrences of each word")
//    val wordCounts = lowercaseWords.countByValue();
//    println("")
//
//    println("Print results:")
//    wordCounts.foreach(println)
//    println("")
//
//    println("Get most frequent words ( > 500 ):")
//    println("")
//    val frequent= wordCounts.filter(x=>x._2 >= 500)
//    frequent.foreach(println)

//    count of the occurrences of each word
    val wordCounts = lowercaseWords.map(x=>(x,1)).reduceByKey((x,y) => x+y)

//    Flip (word, count) tuples to (count, word ) and then short by key (the counts)
    val wordCountSorted = wordCounts.map(x=>(x._2,x._1)).sortByKey()
    for(result <- wordCountSorted){
      val count = result._1
      val word = result._2
      println(s"$word: $count")
    }

  }
}
