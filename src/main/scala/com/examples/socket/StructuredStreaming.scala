package com.examples.socket

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.streaming.Trigger
//kafka_2.12-3.1.0
object StructuredStreaming {
  def main(args :Array[String]): Unit = {
    println("hello world")
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("Spark SQL basic example")
      .config("spark.sql.shuffle.partitions",1)
      .getOrCreate()

    import spark.implicits._
    val lines = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9997)
      .load()

    val words = lines.as[String].flatMap(_.split(" "))
    val wordCounts = words.groupBy("value").count()

    val query = wordCounts.writeStream
          .outputMode("complete")
          .option("checkpointLocation", "C:\\Users\\saitalla.I-FLEX\\IdeaProjects\\SparkStreaming\\checkpoint_dir")
          .format("console")
          .option("truncate", "false")
          .trigger(Trigger.ProcessingTime("60 seconds"))
          .start()
    query.awaitTermination()
  }
}
