import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

/**
 * Timetable: transformations only plan the work, and every action starts a job.
 *
 *     ./run.sh timetable             # exercise/02_timetable/Timetable.java
 *     ./run.sh solution timetable    # solution/02_timetable/Timetable.java
 */
public class Timetable {
    public static void main(String[] args) {
        JavaSparkContext sc = new JavaSparkContext(new SparkConf().setAppName("timetable"));

        JavaRDD<Integer> platforms = sc.parallelize(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9), 3);

        // TODO start: keep the even platforms and label each as "platform <number>"
        JavaRDD<Integer> even = null;
        JavaRDD<String> labels = null;
        // TODO end
        System.out.println("transformations defined, no job has started yet");

        System.out.println("count: " + labels.count());
        System.out.println("collect: " + labels.collect());

        sc.stop();
    }
}
