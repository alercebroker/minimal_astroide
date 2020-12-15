package org.alerce.minimal_astroide

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.{Encoders, Encoder}
import healpix.essentials._

object Xmatcher {

def execute(
              spark: SparkSession,
              inputData1: DataFrame,
              inputData2: DataFrame,
              healpixLevel: Int,
              radius:       Double,
              best: Boolean = true) : DataFrame = {

      import spark.implicits._

      //UDF
      def udfNeighbours = udf((ipix: Long) => {

        ipix +: HealpixProc.neighboursNest(healpixLevel, ipix)

      })
      val sphe_dist = udf((a: Double, d: Double, a2: Double, d2: Double) => AstroideUDF.sphericalDistance(a, d, a2, d2))

      //change second catalog's columns
      val columnName = inputData2.columns
      val newNames = columnName.map(x => x + "_2")
      var inputData2Aux = inputData2
      for (i <- newNames.indices) {
          inputData2Aux = inputData2Aux.withColumnRenamed(columnName(i), newNames(i))
      }

      //compute second catalog's healpix neighbours
      val explodedData2 = inputData2Aux.withColumn("ipix_neigh", explode(udfNeighbours(col("ipix_2"))) )

      //join catalogs and compute spherical distances
      val joined = inputData1.join(explodedData2, explodedData2.col("ipix_neigh") === inputData1.col("ipix"))
      val intermediate_result = joined.withColumn("distance",sphe_dist($"ra", $"dec", $"ra_2", $"dec_2") )

      //filter by distance and select only closest match
      val w = Window.partitionBy(intermediate_result("ra_2"), intermediate_result("dec_2")).orderBy(intermediate_result("distance"))

      var result = intermediate_result
                   .filter( $"distance" < radius)
      if( !best )
          return result

      result =  result.withColumn("axsrownum", row_number().over(w))
                   .where(col("axsrownum") === 1)
                   .drop("axsrownum")

      return result
  }

}
