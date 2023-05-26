from pyspark.sql.session import SparkSession
from pyspark.sql import DataFrame


class AstroideAPI:

    def __init__(self):
        spark= SparkSession.builder.getOrCreate()
        self.api = spark._jvm.org.alerce.minimal_astroide

    def create_healpix_index(self, df, level, column1, column2):
        spark = SparkSession.builder.getOrCreate()
        jresult = self.api.HealpixPartitioner.execute(
                                                        spark._jsparkSession,
                                                        df._jdf,
                                                        level,
                                                        column1,
                                                        column2
        )
        result = DataFrame(jresult,df.sql_ctx)
        return result

    def xmatch(self, catalog1, catalog2, level, radius, best):
        spark = SparkSession.builder.getOrCreate()
        jresult = self.api.Xmatcher.execute(
                                            spark._jsparkSession,
                                            catalog1._jdf,
                                            catalog2._jdf,
                                            level,
                                            radius,
                                            best
        )
        result = DataFrame(jresult, catalog1.sql_ctx)
        return result
