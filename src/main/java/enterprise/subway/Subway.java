
package enterprise.subway;

import java.util.*;
import java.util.stream.Collectors;

public class Subway {

    private Integer connectionsCount;
    private HashMap<String, Line> lines;
    private TreeSet<Station> stations;
    private TreeMap<Station, TreeSet<Station>> connections;

    public Subway() {
        this.connectionsCount = 0;
        this.lines = new HashMap<>();
        this.stations = new TreeSet<>();
        this.connections = new TreeMap<>();
    }

    public int getConnectionsCount() {
        return connectionsCount;
    }

    public void setConnectionsCount(Integer connectionsCount) {
        this.connectionsCount = connectionsCount;
    }

    public HashMap<String, Line> getLines() {
        return lines;
    }

    public Line getLineNumber(String number) {
        return lines.get(number);
    }

    public Station getStation(String name, String lineNumber) {

        Station query = new Station(name, getLineNumber(lineNumber));
        Station station = stations.ceiling(query);

        return station.equals(query) ? station : null;
    }

    public void addLine(Line line) {
        lines.put(line.getNumber(), line);
    }

    public void addStation(Station station) {
        stations.add(station);
    }

    public void addConnection(List<Station> stations) {

        for (Station station : stations) {

            if (!connections.containsKey(station)) {
                connections.put(station, new TreeSet<>());
            }

            TreeSet<Station> connectedStations = connections.get(station);

            connectedStations.addAll(stations.stream()
                    .filter(s -> !s.equals(station)).collect(Collectors.toList()));
        }
    }

    public void printSubway() {

        for (Map.Entry<String, Line> item : lines.entrySet()) {
            System.out.printf("Line: %s\n   Stations:\n%s", item.getKey(), item.getValue().stationsList());
        }

        System.out.println("Total connections: " + connectionsCount);
    }
}
