import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.SplittableRandom;

/**
 * Generates the departures of this session: trains leaving 40 Swiss stations in the first half
 * of 2025, each with its delay in minutes. The numbers are made up; a fixed seed makes every run
 * produce the same files.
 *
 *     data/trains/2025-01.csv ... 2025-06.csv   date,time,line,station_id,delay_min
 *     data/stations.csv                         station_id,name
 *
 * stations.csv leaves out stations 39 and 40 and lists station 41, which has no departures.
 *
 *     java data/Generate.java      # from the session folder, no Spark needed
 */
public class Generate {
    static final long SEED = 7;
    // January to June. The months differ in size, so their files become different numbers of partitions:
    // about 1, 12, 34, 44, 66 and 76 MiB.
    static final int[] DEPARTURES = {40_000, 480_000, 1_354_000, 1_775_000, 2_662_000, 3_066_000};

    // Index i is station i + 1. The last name has no departures.
    static final String[] STATIONS = {"Zürich HB", "Bern", "Basel SBB", "Genève", "Lausanne", "Luzern",
        "Winterthur", "St. Gallen", "Olten", "Biel/Bienne", "Lugano", "Chur", "Fribourg", "Zug", "Aarau", "Thun",
        "Neuchâtel", "Sion", "Schaffhausen", "Brig", "Bellinzona", "Visp", "Arth-Goldau", "Baden",
        "Zürich Flughafen", "Zürich Oerlikon", "Zürich Stadelhofen", "Yverdon-les-Bains", "Montreux", "Vevey",
        "Spiez", "Interlaken Ost", "Landquart", "Rapperswil", "Wil", "Frauenfeld", "Solothurn", "Burgdorf",
        "Lenzburg", "Rorschach", "Zermatt"};
    static final int WITH_DEPARTURES = 40;
    static final int[] MISSING_FROM_STATIONS = {39, 40};

    static final String[] LINES = {"IC1", "IC2", "IC5", "IC8", "IC21", "IR13", "IR15", "IR36", "RE33", "S3", "S12"};

    public static void main(String[] args) throws IOException {
        SplittableRandom random = new SplittableRandom(SEED);

        // Larger stations have more departures, and some stations are later than others.
        double[] traffic = new double[WITH_DEPARTURES];
        double[] lateness = new double[WITH_DEPARTURES];
        for (int s = 0; s < WITH_DEPARTURES; s++) {
            traffic[s] = 1.0 / (1 + s * 0.08);
            lateness[s] = 0.5 + 2.5 * random.nextDouble();
        }
        double[] cumulative = new double[WITH_DEPARTURES];
        double sum = 0;
        for (int s = 0; s < WITH_DEPARTURES; s++) {
            sum += traffic[s];
            cumulative[s] = sum;
        }

        Path trains = Path.of("data/trains");
        Files.createDirectories(trains);
        for (int month = 1; month <= 6; month++) {
            LocalDate first = LocalDate.of(2025, month, 1);
            int days = first.lengthOfMonth();
            Path file = trains.resolve(String.format("2025-%02d.csv", month));
            int departures = DEPARTURES[month - 1];
            try (BufferedWriter out = Files.newBufferedWriter(file)) {
                for (int i = 0; i < departures; i++) {
                    // Departures in time order: the minute of the month grows with i.
                    int minuteOfMonth = (int) ((long) i * days * 1440 / departures);
                    LocalDate date = first.plusDays(minuteOfMonth / 1440);
                    int hour = minuteOfMonth % 1440 / 60;
                    int minute = minuteOfMonth % 60;
                    int station = pick(cumulative, sum, random);
                    boolean rush = hour >= 6 && hour < 9 || hour >= 16 && hour < 19;
                    double mean = lateness[station] * (rush ? 2.0 : 1.0);
                    int delay = random.nextDouble() < 0.55 ? 0 : (int) Math.round(-mean * Math.log(1 - random.nextDouble()));
                    out.write(String.format("%s,%02d:%02d,%s,%d,%d%n", date, hour, minute,
                        LINES[random.nextInt(LINES.length)], station + 1, delay));
                }
            }
        }

        try (BufferedWriter out = Files.newBufferedWriter(Path.of("data/stations.csv"))) {
            for (int id = 1; id <= STATIONS.length; id++) {
                if (id != MISSING_FROM_STATIONS[0] && id != MISSING_FROM_STATIONS[1]) {
                    out.write(id + "," + STATIONS[id - 1] + "\n");
                }
            }
        }
    }

    static int pick(double[] cumulative, double sum, SplittableRandom random) {
        double r = random.nextDouble() * sum;
        for (int i = 0; i < cumulative.length; i++) {
            if (r < cumulative[i]) {
                return i;
            }
        }
        return cumulative.length - 1;
    }
}
