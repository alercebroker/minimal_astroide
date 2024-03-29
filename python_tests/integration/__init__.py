from pyspark.sql import SparkSession

SPARK = (
            SparkSession.builder.appName("IntegrationTests")
            .config(
                "spark.jars",
                "libs/healpix-1.0.jar,build/libs/minimal_astroide-1.0.1.jar",
            )
            .getOrCreate()
        )
