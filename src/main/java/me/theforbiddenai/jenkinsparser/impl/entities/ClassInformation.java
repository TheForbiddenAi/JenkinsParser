package me.theforbiddenai.jenkinsparser.impl.entities;

import me.theforbiddenai.jenkinsparser.Information;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ClassInformation implements Information {

    private Document classDocument;
    private final String baseUrl;

    public ClassInformation(String baseUrl, ArrayList<Element> classList, String className) {
        this.baseUrl = baseUrl;

        classDocument = retrieveClass(classList, className);
        if (classDocument == null) {
            throw new NullPointerException("Class not found!");
        }
    }

    /**
     * Retrieves the specific class, works with nested classes as well
     *
     * @param classList The element list containing all of the class (does not contain nested classes)
     * @param className The name of the class being searched for
     * @return The document for the class or null if none is found
     */
    private @Nullable Document retrieveClass(ArrayList<Element> classList, String className) {

        List<Element> elementList = classList.stream()
                .filter(element -> element.text().equalsIgnoreCase(className.replace("#", ".")))
                .collect(Collectors.toList());

        String[] queryArgs = className.replace("#", ".").split("\\.");
        if (elementList.size() == 0 && queryArgs.length > 1) {

            elementList = classList.stream()
                    .filter(element -> element.text().equalsIgnoreCase(queryArgs[0]))
                    .collect(Collectors.toList());

            if (elementList.size() == 0) return null;

            classDocument = getDocument(baseUrl + elementList.get(0).selectFirst("a").attr("href"));
            HashMap<String, String> nestedClassList = getNestedClassLinkList();

            if (nestedClassList == null || nestedClassList.size() == 0) return null;

            StringBuilder nestName = new StringBuilder(queryArgs[0]);

            // Loops through the queryArgs and nestedClassList until it finds the specific nested class
            for (int i = 1; i < queryArgs.length; i++) {
                for (String name : nestedClassList.keySet()) {

                    classDocument = null;
                    if (name.equalsIgnoreCase(nestName.toString() + "." + queryArgs[i])) {
                        String url = nestedClassList.get(name);
                        classDocument = getDocument(url);

                        // Updates the nested class list
                        nestedClassList = getNestedClassLinkList();

                        nestName.append(".").append(queryArgs[i]);
                    }
                }
            }

            return classDocument;
        } else {
            return getDocument(baseUrl + elementList.get(0).selectFirst("a").attr("href"));
        }

    }


    private @Nullable Document getDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public @NotNull String getName() {
        return classDocument.selectFirst("h2").text();
    }

    public @NotNull String getDescription() {
        if (classDocument.selectFirst("div.block") == null) return "";
        return classDocument.selectFirst("div.block").text();
    }

    public @NotNull String getRawDescription() {
        if (classDocument.selectFirst("div.block") == null) return "";
        return classDocument.selectFirst("div.block").html();
    }

    public @NotNull Document getClassDocument() {
        return classDocument;
    }


    public @NotNull String getUrl() {
        return classDocument.baseUri();
    }

    public @Nullable ArrayList<String> getNestedClassList() {
        return getList("nested.class.summary", false);
    }

    public @Nullable ArrayList<String> getMethodList(boolean withParams) {
        return getList("method.summary", withParams);
    }

    public @Nullable ArrayList<String> getEnumList() {
        return getList("enum.constant.summary", false);
    }

    public @Nullable ArrayList<String> getFieldList() {
        return getList("field.summary", false);
    }

    @Nullable HashMap<String, String> getNestedClassLinkList() {
        return getLinkList("nested.class.summary");
    }

    @Nullable HashMap<String, String> getMethodLinkList() {
        return getLinkList("method.summary");
    }

    @Nullable HashMap<String, String> getEnumLinkList() {
        return getLinkList("enum.constant.summary");
    }

    @Nullable HashMap<String, String> getFieldLinkList() {
        return getLinkList("field.summary");
    }

    /**
     * Gets the items in the list and stores them in a hashmap
     *
     * @param listName The list of items
     * @return An ArrayList containing the item name or null if the list is not found
     */
    private @Nullable ArrayList<String> getList(String listName, boolean withParams) {
        // This is the element that is inside the blocklist which contains the table
        Element aElement = classDocument.selectFirst("a[name=" + listName + "]");
        if (aElement == null) aElement = classDocument.selectFirst("a[id=" + listName + "]");
        if (aElement == null) return null;

        // This is the element that contains the table
        Element blocklist = aElement.parent();

        Element tableBody = blocklist.selectFirst("tbody");
        String columnToGet = tableBody.selectFirst("tr").selectFirst("th.colSecond") != null ? "th.colSecond" : "td.colLast";

        ArrayList<String> methodList = new ArrayList<>();
        tableBody.select("tr").stream()
                .filter(element -> element.selectFirst("td") != null)
                .forEach(tableRow -> {
                    String methodName = tableRow.selectFirst(columnToGet).text();

                    if (!withParams && methodName.contains("(")) {
                        methodName = methodName.substring(0, methodName.indexOf("("));
                    }

                    methodList.add(methodName);
                });
        return methodList;
    }

    /**
     * Gets the links for all of the items in the list and stores them in a hashmap
     *
     * @param listName The list the links of the items are coming from
     * @return A hashmap containing the item name and it's link or null if the list is not found
     */
    private @Nullable HashMap<String, String> getLinkList(String listName) {
        // This is the element that is inside the blocklist which contains the table
        Element aElement = classDocument.selectFirst("a[name=" + listName + "]");
        if (aElement == null) aElement = classDocument.selectFirst("a[id=" + listName + "]");
        if (aElement == null) return null;

        // This is the element that contains the table
        Element blocklist = aElement.parent();

        Element tableBody = blocklist.selectFirst("tbody");

        String columnToGet = listName.equalsIgnoreCase("enum.constant.summary") ?
                (tableBody.selectFirst("tr").selectFirst("th.colFirst") != null ? "th.colFirst" : "td.colOne") :
                (tableBody.selectFirst("tr").selectFirst("th.colSecond") != null ? "th.colSecond" : "td.colLast");

        HashMap<String, String> methodList = new HashMap<>();
        tableBody.select("tr").stream()
                .filter(element -> element.selectFirst("td") != null)
                .forEach(tableRow -> {
                    Element column = tableRow.selectFirst(columnToGet);
                    String methodName = tableRow.selectFirst("span.memberNameLink").text().trim()
                            .replaceAll("[\\p{Cf}]", ""); // Removes invisible characters;

                    String href = column.selectFirst("a").attr("href").replace("../", "");
                    String url = baseUrl + href;
                    methodList.put(methodName, url);
                });

        return methodList;
    }


}
