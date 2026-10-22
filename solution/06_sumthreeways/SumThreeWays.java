import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

/**
 * SumThreeWays: the minutes of delay per station, computed 3 times with less data in the
 * shuffle each time. Compare the Shuffle Write of the 3 jobs.
 *
 *     ./run.sh sumthreeways            # exercise/06_sumthreeways/SumThreeWays.java
 *     ./run.sh solution sumthreeways   # solution/06_sumthreeways/SumThreeWays.java
 */
public class SumThreeWays {
    public static void main(String[] args) {
        String base = args.length > 0 ? args[0].replaceAll("/+$", "") : "/data";

        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("sumthreeways " + base));

        JavaPairRDD<Integer, Integer> departures = sc.textFile(base + "/trains")
            .mapToPair(line -> new Tuple2<>(Trains.station(line), Trains.delay(line)));

        // Job 0: groupByKey sends every departure through the shuffle.
        JavaPairRDD<Integer, Integer> grouped = departures.groupByKey().mapValues(SumThreeWays::sum);
        System.out.println("groupByKey:              " + grouped.collectAsMap().get(1) + " min at station 1");

        // TODO start: job 1, drop the departures without delay before groupByKey
        JavaPairRDD<Integer, Integer> filtered = departures
            .filter(t -> t._2() > 0)
            .groupByKey()
            .mapValues(SumThreeWays::sum);
        // TODO end
        System.out.println("filter, then groupByKey: " + filtered.collectAsMap().get(1) + " min at station 1");

        // TODO start: job 2, add up with reduceByKey
        JavaPairRDD<Integer, Integer> reduced = departures.reduceByKey(Integer::sum);
        // TODO end
        System.out.println("reduceByKey:             " + reduced.collectAsMap().get(1) + " min at station 1");

        sc.stop();
    }

    static int sum(Iterable<Integer> values) {
        int sum = 0;
        for (int v : values) {
            sum += v;
        }
        return sum;
    }
}
