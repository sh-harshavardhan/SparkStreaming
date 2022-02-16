package com.examples.socket

import org.apache.spark.sql.SparkSession

object SparkReadFileExample {
  def main(args :Array[String]): Unit = {
    println("hello world")

    val spark = SparkSession
      .builder()
      .master("local[1]")
      .appName("Spark SQL basic example")
      .getOrCreate()

    val df = spark.read.json("C:\\Users\\saitalla.I-FLEX\\IdeaProjects\\SparkStreaming\\src\\main\\resources\\people.json")
    df.show()
  }



}
