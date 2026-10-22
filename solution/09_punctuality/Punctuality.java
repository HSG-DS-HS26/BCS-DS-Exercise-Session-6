import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Punctuality: how many departures are more than 1, 3, 5 and 10 minutes late, counted first
 * without and then with cache().
 *
 *     ./run.sh punctuality            # exercise/09_punctuality/Punctuality.java
 *     ./run.sh solution punctuality   # solution/09_punctuality/Punctuality.java
 */
public class Punctuality {
    static final int[] LIMITS = {1, 3, 5, 10};

    public static void main(String[] args) {
        String base = args.length > 0 ? args[0].replaceAll("/+$", "") : "/data";

        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("punctuality " + base));

        JavaRDD<Integer> delays = sc.textFile(base + "/trains").map(Trains::delay);

        // Jobs 0 to 3: each count reads all files again.
        for (int limit : LIMITS) {
            System.out.println("more than " + limit + " min late: " + delays.filter(d -> d > limit).count());
        }

        // TODO start: keep delays in the memory of the executors
        delays.cache();
        // TODO end

        // Jobs 4 to 7: job 4 reads the files and keeps the partitions, jobs 5 to 7 read them from memory.
        for (int limit : LIMITS) {
            System.out.println("more than " + limit + " min late: " + delays.filter(d -> d > limit).count());
        }

        sc.stop();
    }
}
