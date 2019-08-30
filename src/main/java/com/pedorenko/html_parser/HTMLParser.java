package com.pedorenko.html_parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HTMLParser {

    public static Document parseDocument(File htmlFile, String charsetName) throws IOException {
        return Jsoup.parse(
                htmlFile,
                charsetName,
                htmlFile.getAbsolutePath());
    }

    public static Optional<Element> findElementById(File htmlFile, String charsetName, String targetElementId) {
        try {
            Document doc = parseDocument(htmlFile, charsetName);

            Element targetElement = doc.getElementById(targetElementId);

            return targetElement == null ? Optional.empty() : Optional.of(doc.getElementById(targetElementId));

        } catch (IOException e) {
            System.out.println("Error reading file " + htmlFile.getAbsolutePath() + "\n" + e);
            return Optional.empty();
        }
    }

    public static String getPathToElementFormatted(Element element) {
        List<String> path = element
                .parents()
                .stream()
                .map(parent -> parent.tagName() + (elementHasSiblings(parent) ? "[" + parent.elementSiblingIndex() + "]" : "")).collect(Collectors.toList());

        Collections.reverse(path);

        path.add(element.tagName() + (elementHasSiblings(element) ? "[" + element.elementSiblingIndex() + "]" : ""));

        return String.join(" > ", path);
    }

    private static boolean elementHasSiblings(Element element) {
        return element.siblingElements().stream().anyMatch(sibling -> sibling.tagName().equals(element.tagName()));
    }
}
