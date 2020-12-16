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

- Healpix Partitioner:

- Xmatcher:

(4) **Catalogs**

- ALLWISE: s3://allwise
- GAIA DR2: s3://gaia-dr2
- SDSS DR16: s3://sdss-dr16
