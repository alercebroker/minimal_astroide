# Minimal Astroide

It main purpose of this software is to help perform crossmatch between massive catalogs using **pyspark**.

It contains a simplified and minimal version of **Astroide** (https://github.com/CnesUvsqAstroide/ASTROIDE) that is written in **Scala** and provides also a **Python** wrapper.

(0) **Requirements**

Spark 2.4.5
Python 3.7

(1) **Installation**

- Compile Scala code
- Install Python package

(2) **Configuration**

Add to Spark configuration the PATH of the jar files needed:

```
spark.jars                      PATH/minimal_astroide.jar,PATH/healpix-1.0.jar
```

(3) **Usage**

```
from pyspark.sql.session import SparkSession

#init
spark= SparkSession.builder.getOrCreate()
api = AstroideAPI()

#load
catalogx = spark.read.load("xxx")
allwise = spark.read.load("s3a://allwise/*")

#create helpix index
healpix_level = 12
df_healpix = api.create_healpix_index(df,12,'ra','dec')

#perform crossmatch
radius = 1.0/3600. #arc-sec
best = True #only best match
result = api.xmatch(allwise,catalogx,healpix_level,radius,best)
```

(4) **Catalogs**

- ALLWISE: s3://allwise
- GAIA DR2: s3://gaia-dr2
- SDSS DR16: s3://sdss-dr16
