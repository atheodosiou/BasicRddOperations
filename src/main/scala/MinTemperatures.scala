import org.apache.spark.{SparkConf, SparkContext}
import org.apache.log4j.{Level, Logger}
import scala.math.{min,max}

//Find min and max temperatures by weather station
object MinTemperatures {

  //Get only wanted values
  def parseLine(line: String)={
    val fields = line.split(",")
    val stationId = fields(0)
    val entryType = fields(2)
    val temperature = fields(3).toFloat * 0.1f * ( 9.0f / 5.0f ) + 32.0f

    (stationId,entryType,temperature)
  }

  def main(args: Array[String]): Unit = {

    Logger.getLogger("org").setLevel(Level.ERROR)

    //Create Spark Context
    val sc = new SparkContext("local[*]","MinTemperatures")

    //Load data
    val lines = sc.textFile("src/Data/tempratures.csv")

    val parsedLines = lines.map(parseLine)

    println("Parsed lines:")
    println("")
    parsedLines.take(2).foreach(println)
    println("")

    //Get only lines with TMIN  for min temperatures

    val minTemps = parsedLines.filter(x=>x._2 == "TMIN")

    println("Min temperatures sample:")
    println("")
    minTemps.take(2).foreach(println)
    println("")

    //Get only lines with TMIN  for min temperatures

    val maxTemps = parsedLines.filter(x=>x._2 == "TMAX")

    println("Max temperatures sample:")
    println("")
    maxTemps.take(2).foreach(println)
    println("")

    //Convert to (stationId, temperature) min
     val statingTemps = minTemps.map(x=>(x._1,x._3.toFloat))

    //Convert to (stationId, temperature) max
    val tempsMax = maxTemps.map(x=>(x._1,x._3.toFloat))

    //Get the min value by stationId
    val minTempByStationId= statingTemps.reduceByKey((x,y) => min(x,y))

    //Get the max value by stationId
    val maxTempByStationId= tempsMax.reduceByKey((x,y) => max(x,y))

    //Collect, format and print the results
    val results = minTempByStationId.collect()
    val maxResults = maxTempByStationId.collect()

    println("Minimun teperature for a station id for the hole dataset:")
    println()
    for(result <- results.sorted){
      val station = result._1
      val temperature = result._2
      val formatedTemp = f"$temperature%.2f F"
      println(s"$station minimun temperature: $formatedTemp")
    }

    println("")

    println("Maximun teperature for a station id for the hole dataset:")
    println()
    for(result <- maxResults.sorted){
      val station = result._1
      val temperature = result._2
      val formatedTemp = f"$temperature%.2f F"
      println(s"$station maximun temperature: $formatedTemp")
    }
  }
}
