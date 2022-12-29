
package enterprise.subway;

import java.util.ArrayList;
import java.util.List;

public class Line implements Comparable<Line> {

    private String number;
    private List<Station> stations;

    public Line(String number){
        this.number = number;
        this.stations = new ArrayList<>();
    }

    public String getNumber() {
        return number;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public String stationsList() {

        StringBuilder builder = new StringBuilder();

        for (Station station : stations) {
            builder.append("        ").append(station.getName()).append("\n");
        }

        return builder.toString();
    }

    @Override
    public int compareTo(Line line) {
        return number.compareToIgnoreCase(line.getNumber());
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Line) obj) == 0;
    }
}
