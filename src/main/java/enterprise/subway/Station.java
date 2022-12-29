
package enterprise.subway;

public class Station implements Comparable<Station> {

    private String name;
    private Line line;

    public Station(String name, Line line) {
        this.name = name;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public Line getLine() {
        return line;
    }

    @Override
    public int compareTo(Station station) {

        int lineComparison = line.compareTo(station.getLine());

        if(lineComparison != 0) {
            return lineComparison;
        }

        return name.compareToIgnoreCase(station.getName());
    }

    @Override
    public boolean equals(Object obj) {
        return compareTo((Station) obj) == 0;
    }
}
