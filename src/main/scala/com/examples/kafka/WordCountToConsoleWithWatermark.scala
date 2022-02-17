package com.examples.kafka

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.count
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.streaming.Trigger
//kafka_2.12-3.1.0
object WordCountToConsoleWithWatermark {
  def main(args :Array[String]): Unit = {
    println("Example : Kafka To Console Word Count for All Batches")

    val spark = SparkSession
      .builder()
      .master("local")
      .appName("Spark SQL basic example")
      .config("spark.sql.shuffle.partitions",1)
      .getOrCreate()
    import spark.implicits._
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "initaltopic")
      .option("startingOffsets", "earliest")
      .load()
    val parsed_df = df.selectExpr("CAST(offset AS STRING)",
      "CAST(timestamp AS STRING)",
      "CAST(key AS STRING)",
      "CAST(value AS STRING)")
      .groupBy(col("value"))
      .agg(count("*").alias("cnt"))
      .sort("value")
      .select("value", "cnt")

    val query = parsed_df.writeStream
      .outputMode("complete")
      .format("console")
      .option("truncate", "false")
      .trigger(Trigger.ProcessingTime("60 seconds"))
      .start()
    query.awaitTermination()
  }
}
