package com.hackholyoke.game;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommentParser {

    private Document plot;
    private Comment[] truths;
    private Comment[] lies;

    public CommentParser(String filename) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            plot = builder.parse(new File(filename));
            // Making truths and lies
            Element root = plot.getDocumentElement();
            NodeList truthList = root.getElementsByTagName("truth");
            truths = new Comment[truthList.getLength()];
            NodeList lieList = root.getElementsByTagName("lie");
            lies = new Comment[lieList.getLength()];
            // Cycling through truths
            for (int i = 0; i < truthList.getLength(); i++) {
                Node truth = truthList.item(i);
                if (truth instanceof Element) {
                    Element choiceElement = (Element) truth;
                    // Getting target
                    String target = choiceElement.getElementsByTagName("target").item(0).getTextContent();
                    // Getting ID
                    String flavor = choiceElement.getElementsByTagName("flavor").item(0).getTextContent();
                    // Getting ID
                    String strength = choiceElement.getElementsByTagName("strength").item(0).getTextContent();
                    // Getting ID
                    String effect = choiceElement.getElementsByTagName("effect").item(0).getTextContent();
                    truths[i] = new Comment(true, target, flavor, strength, effect);
                }
            }
            // Cycling through lies
            for (int i = 0; i < lieList.getLength(); i++) {
                Node lie = lieList.item(i);
                if (lie instanceof Element) {
                    Element choiceElement = (Element) lie;
                    // Getting target
                    String target = choiceElement.getElementsByTagName("target").item(0).getTextContent();
                    // Getting ID
                    String flavor = choiceElement.getElementsByTagName("flavor").item(0).getTextContent();
                    // Getting ID
                    String strength = choiceElement.getElementsByTagName("strength").item(0).getTextContent();
                    // Getting ID
                    String effect = choiceElement.getElementsByTagName("effect").item(0).getTextContent();
                    lies[i] = new Comment(false, target, flavor, strength, effect);
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Comment[] getTruths() {
        return truths;
    }

    public Comment[] getLies() {
        return lies;
    }
}
