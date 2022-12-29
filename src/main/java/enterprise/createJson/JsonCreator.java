
package enterprise.createJson;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JsonCreator {

    private JSONArray getLines(Elements lineElements) {

        JSONArray jsonArrayLines = new JSONArray();

        for (Element element : lineElements) {

            String lineName = element.text();
            String lineNumber = element.attr("data-line");

            LineBlank lineBlank = new LineBlank(lineName, lineNumber);

            JSONObject jsonObjectLine = new JSONObject();

            jsonObjectLine.put("number", lineBlank.getNumber());
            jsonObjectLine.put("name", lineBlank.getName());
            jsonArrayLines.add(jsonObjectLine);
        }

        return jsonArrayLines;
    }

    private JSONObject getStations(Elements stationElements) {

        List<StationBlank> stationsList = new ArrayList<>();

        for (Element element : stationElements) {

            String lineNumber = element.attr("data-line");
            Elements stationsNames = element.select("span.name");

            for (Element stationName : stationsNames) {

                String name = stationName.text();
                stationsList.add(new StationBlank(name, lineNumber ));

            }
        }

        Map<String, List<String>> stationsMap = stationsList.stream()
                .collect(Collectors.groupingBy(StationBlank :: getLine,
                        LinkedHashMap :: new,
                        Collectors.mapping(StationBlank :: getStation,
                                Collectors.toList())));

        return new JSONObject(stationsMap);
    }

    private JSONArray getConnections(Elements stationElements) {

        ArrayList<ConnectionBlank> connectionsList = new ArrayList<>();

        for (Element stationEl : stationElements) {

            String strLineIn = stationEl.attr("data-line");

            Elements connectionLevelOneElements = stationEl.select("p:has(span.t-icon-metroln)");

            for (Element connectionLevOneEl : connectionLevelOneElements) {

                String strNameInBeforeFormatted = connectionLevOneEl.text();
                String strNameIn = strNameInBeforeFormatted.replaceAll("\\d+\\.\\s", "");

                Elements connectionLevelTwoElements =
                        connectionLevOneEl.select("p:has(span.t-icon-metroln) > span.t-icon-metroln");

                for (Element connectionLevTwoEl : connectionLevelTwoElements) {

                    String strLineToBeforeFormatted = connectionLevTwoEl.attr("class");
                    String strLineTo = strLineToBeforeFormatted.replaceAll(".+\\-", "")
                            .replaceAll(".{4}", "");

                    String strNameToBeforeFormatted = connectionLevTwoEl.attr("title");
                    String strNameTo =
                            strNameToBeforeFormatted.replaceAll("(.+\\«)(.+)(\\».+)", "$2");

                    connectionsList.add(new ConnectionBlank(strLineIn, strNameIn, strLineTo, strNameTo));
                }
            }
        }

        TreeSet<String> connectionSet = new TreeSet<>();

        connectionsList.stream().forEach(connection -> connectionSet.add(connection.getLineIn() + "*" +
                connection.getNameIn() + "*" + connection.getLineTo() + "*" + connection.getNameTo()));

        TreeMap<Integer, String> connectionMap = new TreeMap<>();

        for (String connection : connectionSet) {

            int sum = 0;

            for (char ch : connection.toCharArray()) {
                sum += 1 + ch;
            }

            connectionMap.put(sum, connection);
        }

        JSONArray jsonArrayConnections = new JSONArray();

        for (Map.Entry entry : connectionMap.entrySet()) {

            JSONObject connectionObject1 = new JSONObject();
            JSONObject connectionObject2 = new JSONObject();
            JSONArray connectionArray = new JSONArray();

            String stringValue = String.valueOf(entry.getValue());
            String[] valueElements = stringValue.split(Pattern.quote("*"));

            String strLineIn = valueElements[0];
            String strNameIn = valueElements[1];
            String strLineTo = valueElements[2];
            String strNameTo = valueElements[3];

            connectionObject1.put("line", strLineIn);
            connectionObject1.put("station", strNameIn);
            connectionObject2.put("line", strLineTo);
            connectionObject2.put("station", strNameTo);
            connectionArray.add(connectionObject1);
            connectionArray.add(connectionObject2);
            jsonArrayConnections.add(connectionArray);
        }

        return jsonArrayConnections;
    }

    public void createJsonThruUrlPath(String urlPath) {

        try {

            Document doc = Jsoup.connect(urlPath).maxBodySize(0).get();

            Elements lineElements = doc.select("span.js-metro-line");
            Elements stationElements = doc.select("div.js-metro-stations");
            Elements connectionsElements = doc.select("div.js-metro-stations");

            JSONObject objectForJson = new JSONObject();

            objectForJson.put("lines", getLines(lineElements));
            objectForJson.put("stations", getStations(stationElements));
            objectForJson.put("connections", getConnections(connectionsElements));

            BufferedWriter bufferedWriter = Files.newBufferedWriter
                    (Paths.get("src/main/resources/SubwayJson.json"));
            bufferedWriter.write(objectForJson.toJSONString());
            bufferedWriter.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void createJsonViaSavedHtmlFile(String htmlFile) {

        try {

            StringBuilder rawHtml = new StringBuilder();

            List<String> stringLines = Files.readAllLines(Paths.get(htmlFile));
            stringLines.forEach(stringLine -> rawHtml.append(stringLine).append("\n"));

            Document doc = Jsoup.parse(rawHtml.toString());

            Elements lineElements = doc.select("span.js-metro-line");
            Elements stationElements = doc.select("div.js-metro-stations");
            Elements connectionElements = doc.select("div.js-metro-stations");

            JSONObject objectForJson = new JSONObject();

            objectForJson.put("lines", getLines(lineElements));
            objectForJson.put("stations", getStations(stationElements));
            objectForJson.put("connections", getConnections(connectionElements));

            BufferedWriter bufferedWriter = Files.newBufferedWriter
                    (Paths.get("src/main/resources/SubwayJson.json"));
            bufferedWriter.write(objectForJson.toJSONString());
            bufferedWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
