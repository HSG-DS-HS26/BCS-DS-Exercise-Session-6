import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Departures: all departures of half a year, read from a directory of files. Each file split
 * becomes a partition.
 *
 *     ./run.sh departures                 # exercise/03_departures/Departures.java, from /data
 *     ./run.sh solution departures        # solution/03_departures/Departures.java, from /data
 *     ./run.sh solution departures hdfs   # solution/03_departures/Departures.java, from HDFS
 */
public class Departures {
    public static void main(String[] args) {
        String base = args.length > 0 ? args[0].replaceAll("/+$", "") : "/data";

        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("departures " + base));

        // TODO start: read every file in base + "/trains"
        JavaRDD<String> lines = sc.textFile(base + "/trains");
        // TODO end

        // The driver lists the files to find the partitions; no job runs for this.
        System.out.println("partitions: " + lines.getNumPartitions());
        System.out.println("departures: " + lines.count());

        sc.stop();
    }
}
