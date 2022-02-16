package com.examples.socket

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.streaming.Trigger

object SocketToCSVWordCountPerBatch {

  def main(args :Array[String]): Unit = {
    println("Heello Heello !!")
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("SocketToCSVWordCount")
      .config("spark.sql.shuffle.partitions",1) // to create only 1 output file.
      .getOrCreate()

    import spark.implicits._
    val lines = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", 9997)
      .load()

    val words = lines.as[String].flatMap(_.split(" "))
    val wordCounts = words.groupBy("value").count()

    def myFunc( askDF:DataFrame, batchID:Long ) : Unit = {
      askDF.persist()
      askDF.write.format("csv").save("C:\\Users\\saitalla.I-FLEX\\IdeaProjects\\SparkStreaming\\target_dir\\batch_%s".format(batchID.toString))
      askDF.unpersist()
    }
    wordCounts.writeStream
      .option("checkpointLocation", "C:\\Users\\saitalla.I-FLEX\\IdeaProjects\\SparkStreaming\\checkpoint_dir")
      .trigger(Trigger.ProcessingTime("60 seconds"))
      .outputMode("complete")
      .foreachBatch(myFunc _).start().awaitTermination()
  }
}
