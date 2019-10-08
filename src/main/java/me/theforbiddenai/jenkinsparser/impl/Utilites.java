package me.theforbiddenai.jenkinsparser.impl;

import me.theforbiddenai.jenkinsparser.Information;
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
                info.add(linkList.get(objectName.toLowerCase()));
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

    /**
     * Gets the extra information from the specified element
     * @param element The element the extra info is coming from
     * @param rawHtml Whether or not to return the extra info in raw html
     * @return A hashmap containing the extra info for the element
     */
    public static @NotNull HashMap<String, String> getExtraInformation (Element element, boolean rawHtml) {
        HashMap<String, String> extraInfo = new HashMap<>();

        element.select("dl").select("dt").forEach(label -> {
            Element nextElement = label.nextElementSibling();
            StringBuilder infoBuilder = new StringBuilder();
            while(nextElement != null && nextElement.nodeName().equals("dd")) {
                infoBuilder.append(rawHtml ? nextElement.html() : nextElement.text());
                nextElement = nextElement.nextElementSibling();
            }

            String infoBuilderString = infoBuilder.toString().trim();
            infoBuilderString = rawHtml ? infoBuilderString.replace("\n", "<br>") : infoBuilderString;

            extraInfo.put(label.text(), infoBuilderString);
        });



        return extraInfo;
    }

    /**
     * Converts Class/Enum/Field/Method Info lists to an Info List
     * @param listToConvert The list being converted
     * @param <T> The type of the list
     * @return The converted list
     */
    static @NotNull <T> List<Information> convertList(List<T> listToConvert) {
        List<Information> convertedList = new ArrayList<>();

        listToConvert.forEach(info -> {
            convertedList.add((Information) info);
        });

        return convertedList;
    }

}
