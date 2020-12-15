package org.alerce.minimal_astroide

import healpix.essentials._
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql._

object HealpixPartitioner {

  @throws(classOf[Exception])
  def execute(  
              spark: SparkSession, 
              inputData: DataFrame, 
              level: Int, 
              coordinates1: String, 
              coordinates2: String 
  ) : DataFrame = {

    def udfToHealpix = udf((alpha: Double, delta: Double) => {

      val theta = math.Pi / 2 - delta.toRadians
      val phi = alpha.toRadians

      HealpixProc.ang2pixNest(level, new Pointing(theta, phi))
    })

    val outputData = inputData.withColumn("ipix", udfToHealpix(col(coordinates1), col(coordinates2)))

    return outputData


  }
}
