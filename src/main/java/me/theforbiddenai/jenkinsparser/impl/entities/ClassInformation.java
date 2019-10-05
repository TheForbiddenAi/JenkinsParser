package me.theforbiddenai.jenkinsparser.impl.entities;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.impl.Utilites;
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
    private final List<Document> classDocumentList;

    private String name;
    private String description;
    private String rawDescription;
    private String url;

    private HashMap<String, String> extraInformation;
    private HashMap<String, String> rawExtraInformation;

    private ArrayList<String> nestedClassList;
    private ArrayList<String> methodList;
    private ArrayList<String> methodListWithParams;
    private ArrayList<String> enumList;
    private ArrayList<String> fieldList;

    private List<ClassInformation> allFoundClasses;

    public ClassInformation(String baseUrl, @Nullable Document classDocument) {
        this.baseUrl = baseUrl;
        this.classDocument = classDocument;

        List<Document> classDocList = new ArrayList<>();
        classDocList.add(classDocument);

        classDocumentList = classDocList;

        init();
    }

    public ClassInformation(String baseUrl, ArrayList<Element> classList, String className) {
        this.baseUrl = baseUrl;

        classDocumentList = retrieveClasses(classList, className);

        if(classDocumentList == null || classDocumentList.size() == 0) {
            throw new NullPointerException("Class not found!");
        }
        classDocument = retrieveClasses(classList, className).get(0);

        init();
    }

    @Override
    public @Nullable String getName() {
        return name;
    }

    @Override
    public @Nullable String getDescription() {
        return description;
    }

    @Override
    public @Nullable String getRawDescription() {
        return rawDescription;
    }

    @Override
    public @Nullable String getUrl() {
        return url;
    }

    @Override
    public @Nullable HashMap<String, String> getExtraInformation() {
        return extraInformation;
    }

    @Override
    public @Nullable HashMap<String, String> getRawExtraInformation() {
        return rawExtraInformation;
    }

    public @Nullable ArrayList<String> getNestedClassList() {
        return nestedClassList;
    }

    public @Nullable ArrayList<String> getMethodList() {
        return methodList;
    }

    public @Nullable ArrayList<String> getMethodListWithParameters() {
        return methodListWithParams;
    }

    public @Nullable ArrayList<String> getEnumList() {
        return enumList;
    }

    public @Nullable ArrayList<String> getFieldList() {
        return fieldList;
    }

    public @NotNull List<ClassInformation> getAllClasses() {
        if(allFoundClasses.size() == 0) {
            classDocumentList.forEach(document -> {
                allFoundClasses.add(new ClassInformation(baseUrl, document));
            });
        }
        return allFoundClasses;
    }

    public @Nullable Document getClassDocument() {
        return classDocument;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @Override
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    @Override
    public void setRawDescription(@NotNull String rawDescription) {
        this.rawDescription = description;
    }

    @Override
    public void setUrl(@NotNull String url) {
        this.url = url;
    }

    @Override
    public void setExtraInformation(@NotNull HashMap<String, String> extraInformation) {
        this.extraInformation = extraInformation;
    }

    @Override
    public void setRawExtraInformation(@NotNull HashMap<String, String> rawExtraInformation) {
        this.rawExtraInformation = rawExtraInformation;
    }

    public void setNestedClassList(@NotNull ArrayList<String> nestedClassList) {
        this.nestedClassList = nestedClassList;
    }

    public void setMethodList(@NotNull ArrayList<String> methodList) {
        this.methodList = methodList;
    }

    public void setMethodListWithParameters(@NotNull ArrayList<String> methodListWithParameters) {
        this.methodListWithParams = methodListWithParameters;
    }

    public void setEnumList(@NotNull ArrayList<String> enumList) {
        this.enumList = enumList;
    }

    public void setFieldList(@NotNull ArrayList<String> fieldList) {
        this.fieldList = fieldList;
    }

    // LinkList methods are for internal use

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

        Element firstTr = tableBody.selectFirst("tr");
        String columnToGet = listName.equalsIgnoreCase("enum.constant.summary") ?
                (firstTr.selectFirst("th.colFirst") != null ? "th.colFirst" : "td.colOne") :
                (firstTr.selectFirst("th.colSecond") != null ? "th.colSecond" : "td.colLast");

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
        if (classDocument == null) return null;

        Element aElement = classDocument.selectFirst("a[name=" + listName + "]");
        if (aElement == null) aElement = classDocument.selectFirst("a[id=" + listName + "]");
        if (aElement == null) return null;

        // This is the element that contains the table
        Element blocklist = aElement.parent();

        Element tableBody = blocklist.selectFirst("tbody");

        Element firstTr = tableBody.selectFirst("tr");
        String columnToGet = listName.equalsIgnoreCase("enum.constant.summary") ?
                (firstTr.selectFirst("th.colFirst") != null ? "th.colFirst" : "td.colOne") :
                (firstTr.selectFirst("th.colSecond") != null ? "th.colSecond" : "td.colLast");


        HashMap<String, String> methodList = new HashMap<>();
        tableBody.select("tr").stream()
                .filter(element -> element.selectFirst("td") != null)
                .forEach(tableRow -> {
                    Element column = tableRow.selectFirst(columnToGet);
                    String methodName = column.text();

                    String href = column.selectFirst("a").attr("href").replace("../", "");
                    String url = (href.startsWith("#") ? getUrl() : baseUrl) + href;

                    methodList.put(methodName, url);
                });

        return methodList;
    }

    /**
     * Retrieves the specific class, works with nested classes as well
     *
     * @param classList The element list containing all of the class (does not contain nested classes)
     * @param className The name of the class being searched for
     * @return The list of documents for all found classes with the specified name or null if none is found
     */
    private @Nullable List<Document> retrieveClasses(ArrayList<Element> classList, String className) {

        List<Document> foundClasses = new ArrayList<>();
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

            foundClasses.add(classDocument);
        } else {
            if (elementList.size() == 0) return null;

            for(Element element : elementList) {
                foundClasses.add(getDocument(baseUrl + element.selectFirst("a").attr("href")));
            }
        }
        return foundClasses;

    }

    private @Nullable Document getDocument(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void init() {
        try {
            name = classDocument.selectFirst("h2").text();

            if (classDocument.select("div.block") == null) {
                description = "";
                rawDescription = "";
            } else {
                description = classDocument.selectFirst("div.block").text();
                rawDescription = classDocument.selectFirst("div.block").html();
            }

            url = classDocument.baseUri();

            Element classDescBlock = classDocument.selectFirst("div.description");
            extraInformation = Utilites.getExtraInformation(classDescBlock, false);
            rawExtraInformation = Utilites.getExtraInformation(classDescBlock, true);

            nestedClassList = getList("nested.class.summary", false);
            methodList = getList("method.summary", false);
            methodListWithParams = getList("method.summary", true);
            enumList = getList("enum.constant.summary", false);
            fieldList = getList("field.summary", false);

            allFoundClasses = new ArrayList<>();
        } catch (NullPointerException ignored) {
        }

    }

}
