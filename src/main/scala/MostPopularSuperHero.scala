import org.apache.spark._
import org.apache.log4j._
import scala.io.Source
import java.nio.charset.CodingErrorAction
import scala.io.Codec

object MostPopularSuperHero {

  def countCoOccurences(line:String)={
    //Split each line on white space using regular expression \s+
    var elements = line.split("\\s+")
    // -1 means without heroId in counts
    (elements(0).toInt, elements.length -1)
  }

  /*An options is somthis like null in Java. Is says tha you have data or None in case of failure*/
  def parseNames(line:String): Option[(Int,String)]={
      var fields = line.split("\"")
      if(fields.length>1){
        return Some(fields(0).trim().toInt,fields(1))
      }else{
        return None //flatmap will just discard None results, and extract data from Some results.
      }
  }

  def main(args: Array[String]): Unit = {
    println("Find most popular superhero using Marvel dataset...")

    Logger.getLogger("org").setLevel(Level.ERROR)

    val sc= new SparkContext("local[*]","MostPopularSuperhero")

//    Build up a heroId -> name RDD

    val names= sc.textFile("src/Data/Marvel-names.txt")
    val namesRDD = names.flatMap(parseNames)

//    namesRDD.take(10).foreach(println)

    //Load up the superhero co-occurrences data
    val lines = sc.textFile("src/Data/Marvel-graph.txt")

    //Conver to (heroID, number of connections) RDD
    val pairings = lines.map(countCoOccurences)

    //Combine entries tha span more than one line
    //For two lines of data with the same key add the second filed
    val totalFriendsByCharacter = pairings.reduceByKey((x,y)=> x+y)

    //Flip in to # of connections, hero ID
    val fliped = totalFriendsByCharacter.map(x=>(x._2,x._1))

    //Find the max # of connections
    //Max finds the maximun value based on the key
    val mostPopular = fliped.max()

    //Look up the name (lookup returns an array of results, so we need to access the first result with (0)
    val mostPopularName = namesRDD.lookup(mostPopular._2)(0)

    //Print out our answer
    println(s"$mostPopularName is the most popular superhero with ${mostPopular._1} co-appearances.")

//    RESULT
    //CAPTAIN AMERICA is the most popular superhero with 1933 co-appearances.
  }
}
