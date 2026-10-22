import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * WorstStations: the 10 stations with the most minutes of delay, once with sortByKey and once
 * with top.
 *
 *     ./run.sh worststations            # exercise/08_worststations/WorstStations.java
 *     ./run.sh solution worststations   # solution/08_worststations/WorstStations.java
 */
public class WorstStations {
    // Serializable, because the executors compare the pairs.
    static final Comparator<Tuple2<Integer, Integer>> BY_MINUTES =
        (Comparator<Tuple2<Integer, Integer>> & Serializable) (a, b) -> Integer.compare(a._2(), b._2());

    public static void main(String[] args) {
        String base = args.length > 0 ? args[0].replaceAll("/+$", "") : "/data";

        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("worststations " + base));

        JavaPairRDD<Integer, Integer> minutes = sc.textFile(base + "/trains")
            .mapToPair(line -> new Tuple2<>(Trains.station(line), Trains.delay(line)))
            .reduceByKey(Integer::sum);

        // TODO start: make the minutes the key, sort them in descending order and collect
        List<Tuple2<Integer, Integer>> sorted = null;
        // TODO end
        System.out.println("sortByKey: " + sorted.subList(0, 10));

        // TODO start: the 10 pairs with the most minutes, using BY_MINUTES
        List<Tuple2<Integer, Integer>> top = null;
        // TODO end
        System.out.println("top:       " + top);

        sc.stop();
    }
}
