
package enterprise.subway;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateSubwayFromJson {

    private String jsonFile;
    private Subway subway;

    public CreateSubwayFromJson(String jsonFile) {
        this.jsonFile = jsonFile;
        this.subway = new Subway();
    }

    public Subway createSubway() {

        parseSubway();

        return subway;
    }

    private String jsonToString() {

        StringBuilder builder = new StringBuilder();

        try {

            List<String> stringLines = Files.readAllLines(Paths.get(jsonFile));
            stringLines.forEach(line -> builder.append(line).append("\n"));

        } catch (Exception ex){
            ex.printStackTrace();
        }

        return builder.toString();
    }

    private void parseLines(JSONArray linesArray) {

        linesArray.forEach(lineObject -> {

            JSONObject lineJsonObject = (JSONObject) lineObject;

            Line line = new Line((String) lineJsonObject.get("number"));

            subway.addLine(line);
        });
    }

    private void parseStations(JSONObject stationsObject) {

        stationsObject.keySet().forEach(lineNumberObject -> {

            Line line = subway.getLineNumber((String)lineNumberObject);
            JSONArray stationsArray = (JSONArray) stationsObject.get(lineNumberObject);

            stationsArray.forEach(stationObject -> {

                Station station = new Station((String) stationObject, line);
                subway.addStation(station);
                line.addStation(station);
            });
        });
    }

    private void parseConnections(JSONArray connectionsArray) {

        connectionsArray.forEach(connectionObject -> {

            JSONArray connection = (JSONArray) connectionObject;
            List<Station> connectionStations = new ArrayList<>();

            connection.forEach(item -> {

                JSONObject itemObject = (JSONObject) item;

                String lineNumber = (String) itemObject.get("line");
                String stationName = (String) itemObject.get("station");

                Station station = subway.getStation(stationName, lineNumber);

                if(station == null) {
                    throw new IllegalArgumentException("Station " +
                            stationName + " on line " + lineNumber + " not found");
                }

                connectionStations.add(station);
            });

            subway.addConnection(connectionStations);
        });
    }

    private void parseSubway(){

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(jsonToString());

            JSONArray linesArray = (JSONArray) jsonData.get("lines");
            parseLines(linesArray);

            JSONObject stationsObject = (JSONObject) jsonData.get("stations");
            parseStations(stationsObject);

            JSONArray connectionsArray = (JSONArray) jsonData.get("connections");
            parseConnections(connectionsArray);

            Integer connectionsCount = ((JSONArray) jsonData.get("connections")).size();
            subway.setConnectionsCount(connectionsCount);
        }

        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
