package com.hackholyoke.game;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DialogueParser {

    private Document plot;
    private Scene[] scenes;

    public DialogueParser(String filename) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            plot = builder.parse(new File(filename));
            // Making scenes from plot
            Element root = plot.getDocumentElement();
            NodeList sceneList = root.getElementsByTagName("scene");
            scenes = new Scene[sceneList.getLength()];
            // Cycling through each choice
            for (int i = 0; i < sceneList.getLength(); i++) {
                Node scene = sceneList.item(i);
                if (scene instanceof Element) {
                    Element sceneElement = (Element) scene;
                    // Getting ID
                    String idString = sceneElement.getElementsByTagName("id").item(0).getTextContent();
                    int id = Integer.parseInt(idString);
                    // Getting speaker
                    String speaker = sceneElement.getElementsByTagName("speaker").item(0).getTextContent();
                    // Getting text
                    String text = sceneElement.getElementsByTagName("speaker").item(0).getTextContent();
                    scenes[i] = new Scene(id, speaker, text);
                }
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Scene[] getScenes() {
        return scenes;
    }
}
