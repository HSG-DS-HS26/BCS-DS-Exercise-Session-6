import java.util.Map;
import java.util.TreeMap;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * DelayMinutes: the minutes of delay per station. reduceByKey is wide: it needs all pairs of
 * a station in the same partition, so Spark shuffles them and splits the job into 2 stages.
 *
 *     ./run.sh delayminutes            # exercise/05_delayminutes/DelayMinutes.java
 *     ./run.sh solution delayminutes   # solution/05_delayminutes/DelayMinutes.java
 */
public class DelayMinutes {
    public static void main(String[] args) {
        String base = args.length > 0 ? args[0].replaceAll("/+$", "") : "/data";

        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("delayminutes " + base));

        JavaPairRDD<Integer, Integer> late = sc.textFile(base + "/trains")
            .filter(line -> Trains.delay(line) > 0)
            .mapToPair(line -> new Tuple2<>(Trains.station(line), Trains.delay(line)));

        // TODO start: add up the minutes of delay per station
        JavaPairRDD<Integer, Integer> minutes = null;
        // TODO end

        // The indented lines are the stage before the shuffle.
        System.out.println(minutes.toDebugString());
        // Job 0. TreeMap sorts the stations by id on the driver.
        Map<Integer, Integer> perStation = new TreeMap<>(minutes.collectAsMap());
        perStation.forEach((station, total) -> System.out.printf("station %2d  %7d min%n", station, total));

        // TODO start: the same sum, into 2 partitions
        JavaPairRDD<Integer, Integer> minutesIn2 = null;
        // TODO end

        // Job 1: its second stage runs a task per partition of minutesIn2.
        System.out.println("partitions: " + minutes.getNumPartitions() + " and " + minutesIn2.getNumPartitions());
        System.out.println("stations: " + minutesIn2.count());

        sc.stop();
    }
}
