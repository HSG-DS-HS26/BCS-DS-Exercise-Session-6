import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Platforms: the cluster counts the 9 platforms of a station in 3 partitions.
 *
 *     ./run.sh platforms             # exercise/01_platforms/Platforms.java
 *     ./run.sh solution platforms    # solution/01_platforms/Platforms.java
 */
public class Platforms {
    public static void main(String[] args) {
        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("platforms"));

        // TODO start: put the platform numbers 1 to 9 into an RDD with 3 partitions
        JavaRDD<Integer> platforms = null;
        // TODO end

        System.out.println("partitions: " + platforms.getNumPartitions());
        // count is an action: it starts a job with a task per partition.
        System.out.println("platforms: " + platforms.count());

        sc.stop();
    }
}
