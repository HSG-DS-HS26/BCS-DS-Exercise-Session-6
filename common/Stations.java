/** The fields of a line in data/stations.csv: station_id,name */
public final class Stations {
    private Stations() {}

    public static int id(String line) {
        return Integer.parseInt(line.split(",")[0]);
    }

    public static String name(String line) {
        return line.split(",")[1];
    }
}
