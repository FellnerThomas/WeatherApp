package WeatherParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLDecoder;


/**
 * Created by Ramin on 26.03.2015.
 */
public class FetchWeatherData {
    public static void main(String[] args) throws Exception {

    }

    public static DailyWeather fetchIt (String url)  throws Exception {

        ThreeHourlyWeather thw = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        url = url.replaceAll("\\s","%20");
        Document document = builder.parse(url);


        DailyWeather dw = new DailyWeather();

        NodeList nodeList = document.getDocumentElement().getChildNodes();

        //Parent auf "forecast" setzen, auf der Position 9
        Node forecast = nodeList.item(4);
        System.out.println(nodeList);
        //Childnodes von forecast bekommen.
        NodeList forecastList = forecast.getChildNodes();

        for(int i=0; i<16&&i<forecastList.getLength(); i++) {
            Node node = forecastList.item(i);
            if(node instanceof Element) {
                thw = new ThreeHourlyWeather();
                thw.setStarting_hour(node.getAttributes().getNamedItem("from").getNodeValue().split("T")[1]);
                NodeList timeList = node.getChildNodes();
                for (int j = 0; j < timeList.getLength(); j++) {
                    Node cNode = timeList.item(j);
                    if (cNode instanceof Element) {
                        switch (cNode.getNodeName()) {
                            case "temperature":
                                thw.setTemperature_celsius(cNode.getAttributes().getNamedItem("value").getNodeValue());
                                break;
                            case "symbol":
                                thw.setClimate(cNode.getAttributes().getNamedItem("name").getNodeValue());
                                break;
                            case "windSpeed":
                                thw.setWindSpeed(cNode.getAttributes().getNamedItem("name").getNodeValue());
                                break;
                            case "windDirection":
                                thw.setWindDirection(cNode.getAttributes().getNamedItem("name").getNodeValue());
                                break;
                        }

                    }
                }
                dw.addThreeHourlyWeatherData(thw);
            }

        }
        return dw;
    }

}

