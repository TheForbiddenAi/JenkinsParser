package me.theforbiddenai.jenkinsparser.impl;

import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Utilites {

    /**
     * Gets an objects element and url
     *
     * @param classInfo  The class the object is being searched for in
     * @param linkList   The hashmap containing both the objects name and it's url
     * @param listName   The list the element is being searched for in
     * @param objectName The object's name that is being searched for
     * @return A list containing the object's url and it's element or null if none is found
     */
    public static @Nullable List<Object> getObjectInfo(ClassInformation classInfo, HashMap<String, String> linkList, String listName, String objectName) {
        List<Object> info = new ArrayList<>();

        if (linkList == null) return null;

        linkList.keySet().forEach(name -> {
            if (name.equalsIgnoreCase(objectName)) {
                info.add(linkList.get(objectName));
            }
        });

        Document classDocument = classInfo.getClassDocument();

        if (info.size() == 0) return null;

        // This is the element that is inside the blocklist which contains the method information
        Element aElement = classDocument.selectFirst("a[name=" + listName + "]");
        if (aElement == null) aElement = classDocument.selectFirst("a[id=" + listName + "]");

        Element enumList = aElement.parent();


        List<Element> elementList = enumList.select("h4").stream()
                .filter(element -> element.text().equalsIgnoreCase(objectName))
                .map(Element::parent)
                .collect(Collectors.toList());

        if (elementList.size() == 0) return null;
        info.add(elementList.get(0));

        return info;
    }

    public static @NotNull HashMap<String, List<String>> getExtraInformation (Element element, boolean rawHtml) {
        HashMap<String, List<String>> extraInfo = new HashMap<>();

        element.select("dl").select("dt").forEach(label -> {
            Element nextElement = label.nextElementSibling();
            List<String> info = new ArrayList<>();
            while(nextElement != null && nextElement.nodeName().equals("dd")) {
                info.add(rawHtml ? nextElement.html() : nextElement.text());
                nextElement = nextElement.nextElementSibling();
            }

            extraInfo.put(label.text(), info);
        });


        return extraInfo;
    }

}