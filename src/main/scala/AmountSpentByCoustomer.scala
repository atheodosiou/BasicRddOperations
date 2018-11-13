import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object AmountSpentByCoustomer {

  def ExtractCustomerPricePairs(line: String)={
    val fields = line.split(",")
    val customerId = fields(0).toInt
    //      val productId = fields(1).toInt
    val amount = fields(2).toFloat

    (customerId, amount)
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    println("Amount Spent By Customer")

    val conf = new SparkConf()
      .setMaster("local[*]")
      .setAppName("AmountSpentByCustomer")

    val sc = new SparkContext(conf)

    val lines = sc.textFile("src/Data/customer-orders.csv")

    val orders =lines.map(ExtractCustomerPricePairs)
    println("Initial rdd sample:")
    orders.take(5).foreach(println)
    println("==================")

    val count = orders.count()
    println(s"count: $count")

    println("==================")

    orders.reduceByKey((x,y)=>x+y).collect()
    val flipped = orders.map(x=>(x._2,x._1))
    val results =flipped.sortByKey()
    results.foreach(println)

    println("==================")

    val bigestAmount = results.take(1)
    bigestAmount.foreach(println)

    println("==================")
//
//    val flipped = orders.map(x=>(x._2,x._1))
//
//    flipped.sortByKey().collect().foreach(println)
  }
}
