import csv
import os
import tempfile
from typing import Tuple, List

from pyspark.sql import SparkSession

from python_wrapper.astroide_wrapper import AstroideAPI
from python_tests.integration import SPARK


def test_create_healpix_index() -> None:
    given_ingest_folder, given_transform_folder = __create_ingest_and_transform_folders()
    input_csv_path = given_ingest_folder + 'input.csv'
    csv_content = [
        ['source_id', 'source_ra', 'source_dec', 'source_class', 'source_redshift', 'source_cat'],
        ['SN 2021oxy', '287.3943833333333', '28.954116666666668', 'SN Ia-91T-like', '0.082', 'TNS'],
        ['SN 2021otc', '229.2524083333333', '22.096475', 'SN Ic-BL', '0.4', 'TNS'],
    ]
    __write_csv_file(input_csv_path, csv_content)
    __create_healpix_index(SPARK, input_csv_path, given_transform_folder)

    actual = SPARK.read.parquet(given_transform_folder)
    expected = SPARK.createDataFrame(
        [
            ('SN 2021oxy', '287.3943833333333', '28.954116666666668', 'SN Ia-91T-like', '0.082', 'TNS', 59301438),
            ('SN 2021otc', '229.2524083333333', '22.096475', 'SN Ic-BL', '0.4', 'TNS', 35368985)
        ],
        ['source_id', 'source_ra', 'source_dec', 'source_class', 'source_redshift', 'source_cat', 'ipix']
    )

    assert expected.collect() == actual.collect()


def __create_ingest_and_transform_folders() -> Tuple[str, str]:
    base_path = tempfile.mkdtemp()
    ingest_folder = f'{base_path}{os.path.sep}'
    transform_folder = f'{base_path}{os.path.sep}transform'
    return ingest_folder, transform_folder


def __write_csv_file(file_path: str, content: List[List[str]]) -> None:
    with open(file_path, 'w', encoding="utf8") as csv_file:
        input_csv_writer = csv.writer(csv_file)
        input_csv_writer.writerows(content)
        csv_file.close()


def __create_healpix_index(spark: SparkSession, ingest_path: str, transformation_path: str) -> None:
    # init
    api = AstroideAPI()

    # load
    df = spark.read.format("org.apache.spark.csv").option("header", True).csv(ingest_path)

    # create healpix index
    healpix_level = 12
    df_healpix = api.create_healpix_index(df, healpix_level, 'source_ra', 'source_dec')

    # save
    df_healpix.write.parquet(transformation_path)
