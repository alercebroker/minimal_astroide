package org.alerce.minimal_astroide

import org.apache.spark.sql.api.java.UDF4
import healpix.essentials._
import org.apache.spark.sql.SparkSession


object AstroideUDF {

  def sphericalDistance(RA1: Double, DEC1: Double, RA2: Double, DEC2: Double): Double = {

    val RA1_rad = RA1.toRadians
    val RA2_rad = RA2.toRadians
    val DEC1_rad = DEC1.toRadians
    val DEC2_rad = DEC2.toRadians
    var d = scala.math.pow(math.sin((DEC1_rad - DEC2_rad) / 2), 2)
    d += scala.math.pow(math.sin((RA1_rad - RA2_rad) / 2), 2) * math.cos(DEC1_rad) * math.cos(DEC2_rad)

    return (2 * math.asin(math.sqrt(d)).toDegrees)

  }

  def computeZone(ra: Double, dec: Double, deltaRa: Double, deltaDec: Double): Int = {

        val raFullRange = 360
        val nRa   = (raFullRange/deltaRa).ceil      
        var idxRa  = (ra/deltaRa).ceil
        val idxDec = ((dec+90.0)/deltaDec).floor 
        val result = idxDec * nRa + idxRa 
        return result.toInt

  }
 
}
