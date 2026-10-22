import org.apache.spark.sql.SparkSession;

/**
 * DelayReport: a delay report in Spark SQL over all departures, for a look at its queries in the
 * DataFlint tab of the History Server.
 *
 *     ./run.sh solution delayreport          # solution/10_delayreport/DelayReport.java, from /data
 *     ./run.sh solution delayreport hdfs     # the same, from HDFS
 */
public class DelayReport {
    public static void main(String[] args) {
        String base = args.length > 0 ? args[0].replaceAll("/+$", "") : "/data";

        SparkSession spark = SparkSession.builder().appName("delayreport " + base).getOrCreate();

        spark.read().schema("date DATE, time STRING, line STRING, station_id INT, delay_min INT")
            .csv(base + "/trains").createOrReplaceTempView("trains");
        spark.read().schema("station_id INT, name STRING")
            .csv(base + "/stations.csv").createOrReplaceTempView("stations");

        // Query 1: per station, the departures, the average delay and the share more than 3 minutes late.
        spark.sql("""
            SELECT s.name, COUNT(*) AS departures, ROUND(AVG(t.delay_min), 2) AS avg_delay,
                   ROUND(100 * AVG(IF(t.delay_min > 3, 1, 0)), 1) AS late_pct
            FROM trains t JOIN stations s ON t.station_id = s.station_id
            GROUP BY s.name
            ORDER BY avg_delay DESC
            """).show(10);

        // Query 2: the average delay per hour of the day.
        spark.sql("""
            SELECT SUBSTRING(time, 1, 2) AS hour, ROUND(AVG(delay_min), 2) AS avg_delay
            FROM trains
            GROUP BY hour
            ORDER BY hour
            """).show(24);

        // Query 3: per station, the worst streak: the highest average delay over 10 departures in a row.
        // The window needs all departures of a station in time order.
        spark.sql("""
            SELECT station_id, ROUND(MAX(streak), 1) AS worst_streak
            FROM (SELECT station_id,
                         AVG(delay_min) OVER (PARTITION BY station_id ORDER BY date, time
                                              ROWS BETWEEN 9 PRECEDING AND CURRENT ROW) AS streak
                  FROM trains)
            GROUP BY station_id
            ORDER BY worst_streak DESC
            """).show(10);

        spark.stop();
    }
}
