import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * LateTrains: the late departures as (station, delay). filter and mapToPair are narrow:
 * they work on each partition by itself, so Spark runs them in the same stage as the read.
 *
 *     ./run.sh latetrains            # exercise/04_latetrains/LateTrains.java
 *     ./run.sh solution latetrains   # solution/04_latetrains/LateTrains.java
 */
public class LateTrains {
    public static void main(String[] args) {
        String base = args.length > 0 ? args[0].replaceAll("/+$", "") : "/data";

        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("latetrains " + base));

        JavaRDD<String> lines = sc.textFile(base + "/trains");

        // TODO start: keep the late departures and turn each into (station, delay)
        JavaPairRDD<Integer, Integer> late = null;
        // TODO end

        // The lineage: each RDD above the RDD it is computed from.
        System.out.println(late.toDebugString());
        System.out.println("late departures: " + late.count());

        sc.stop();
    }
}
