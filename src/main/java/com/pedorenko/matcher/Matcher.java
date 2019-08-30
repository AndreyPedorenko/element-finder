package com.pedorenko.matcher;

import com.pedorenko.html_parser.HTMLParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Matcher {

    public static Optional<Element> findMostMatchingElement(File htmlFile, String charsetName, MatchingParams matchingParams) {

        try {
            Document doc = HTMLParser.parseDocument(htmlFile, charsetName);

            return findMostMatchingElementInDocument(doc, matchingParams);

        } catch (IOException e) {
            System.out.println("Error reading file " + htmlFile.getAbsolutePath() + "\n" + e);
            return Optional.empty();
        }
    }

    private static Optional<Element> findMostMatchingElementInDocument(Document doc, MatchingParams matchingParams) {

        double maxMatchPercent = -1;
        Optional<Element> bestMatch = Optional.empty();

        for (Element element : doc.getAllElements()) {

            double matchPercent = defineMatchPercent(element, matchingParams);

            if (matchPercent > maxMatchPercent) {
                maxMatchPercent = matchPercent;
                bestMatch = Optional.of(element);
            }

        }

        return bestMatch;
    }

    public static double defineMatchPercent(Element element, MatchingParams matchingParams) {

        String matchingTagName = matchingParams.getTagName();
        Map<String, String> matchingAttributes = matchingParams.getAttributes();
        String matchingContent = matchingParams.getContent();

        int matchCount = 0;

        if (element.tagName().equals(matchingTagName)) {
            matchCount++;
        }

        for (String attributeName : matchingAttributes.keySet()) {
            if (element.hasAttr(attributeName) && element.attr(attributeName).equals(matchingAttributes.get(attributeName))) {
                matchCount++;
            }
        }

        if (element.text().equals(matchingContent)) {
            matchCount++;
        }

        return (double) matchCount / (matchingAttributes.size() + 2) * 100;
    }

    public static MatchingParams defineMatchingParams(Element button) {

        String tagName = button.tagName();

        Map<String, String> attributes = new HashMap<>();
        button.attributes().asList().forEach(attribute -> attributes.put(attribute.getKey(), attribute.getValue()));

        String elementContent = button.text();

        return new MatchingParams(tagName, attributes, elementContent);
    }
}
