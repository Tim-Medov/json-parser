
package enterprise;

import enterprise.createJson.JsonCreator;
import enterprise.subway.CreateSubwayFromJson;
import enterprise.subway.Subway;

public class Main {

    public static void main(String[] args) {

        String htmlFile = "src/main/resources/SubwayHtml.html";

        new JsonCreator().createJsonViaSavedHtmlFile(htmlFile);

        String jsonFile = "src/main/resources/SubwayJson.json";

        Subway subway = new CreateSubwayFromJson(jsonFile).createSubway();

        subway.printSubway();
    }
}
