import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * StationNames: the minutes of delay per station, joined with the station names.
 *
 *     ./run.sh stationnames            # exercise/07_stationnames/StationNames.java
 *     ./run.sh solution stationnames   # solution/07_stationnames/StationNames.java
 */
public class StationNames {
    public static void main(String[] args) {
        String base = args.length > 0 ? args[0].replaceAll("/+$", "") : "/data";

        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("stationnames " + base));

        JavaPairRDD<Integer, Integer> minutes = sc.textFile(base + "/trains")
            .mapToPair(line -> new Tuple2<>(Trains.station(line), Trains.delay(line)))
            .reduceByKey(Integer::sum);

        // TODO start: read stations.csv (station_id,name) as station -> name
        JavaPairRDD<Integer, String> stations = null;
        // TODO end

        // TODO start: join them into station -> (minutes, name)
        JavaPairRDD<Integer, Tuple2<Integer, String>> named = null;
        // TODO end

        System.out.println(named.toDebugString());
        List<Tuple2<Integer, Tuple2<Integer, String>>> rows = named.collect();
        for (Tuple2<Integer, Tuple2<Integer, String>> row : rows) {
            System.out.printf("%2d  %-20s %7d min%n", row._1(), row._2()._2(), row._2()._1());
        }
        System.out.println(rows.size() + " stations");

        sc.stop();
    }
}
