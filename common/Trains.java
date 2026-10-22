/** The fields of a line in data/trains: date,time,line,station_id,delay_min */
public final class Trains {
    private Trains() {}

    public static int station(String line) {
        return Integer.parseInt(line.split(",")[3]);
    }

    public static int delay(String line) {
        return Integer.parseInt(line.split(",")[4]);
    }
}
