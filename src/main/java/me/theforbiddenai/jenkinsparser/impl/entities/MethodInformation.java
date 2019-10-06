package me.theforbiddenai.jenkinsparser.impl.entities;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.impl.Utilites;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodInformation implements Information {

    private final ClassInformation classInfo;
    private final Element methodElement;
    private final HashMap<String, String> classMethodLinkList;

    private String name;
    private String nameWithParameters;
    private String description;
    private String rawDescription;
    private String url;

    private ArrayList<MethodInformation> methodList;

    private HashMap<String, String> extraInformation;
    private HashMap<String, String> rawExtraInformation;

    public MethodInformation(ClassInformation classInfo, String methodName) {
        methodName = methodName.contains("(") ? methodName.substring(0, methodName.indexOf("(")) : methodName;

        this.classInfo = classInfo;
        this.classMethodLinkList = classInfo.getMethodLinkList();
        try {
            HashMap<Element, String> methods = getMethods(methodName);
            this.methodElement = (Element) methods.keySet().toArray()[0];
            this.url = (String) methods.values().toArray()[0];

            init();
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            throw new NullPointerException("Couldn't find the specified method!");
        }
    }

    public MethodInformation(ClassInformation classInfo, Element methodElement, String methodUrl) {
        this.classInfo = classInfo;
        this.classMethodLinkList = classInfo.getMethodLinkList();
        this.methodElement = methodElement;
        this.url = methodUrl;

        try {
            init();
        } catch (Exception ignored) {}
    }

    @Override
    public @Nullable String getName() {
        return name;
    }

    public @Nullable String getNameWithParameters() {
        return nameWithParameters;
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

    /**
     * Gets all methods with the same name as the current name
     *
     * @return A list of methods that share the same name or null if none are found
     */
    public @Nullable ArrayList<MethodInformation> getAllMethods() {
        if (methodList == null) {

            HashMap<Element, String> methods = getMethods(getName());

            if (methods != null) {
                methodList = new ArrayList<>();
                methods.forEach((element, url) -> methodList.add(new MethodInformation(classInfo, element, url)));

            } else {
                throw new NullPointerException("Couldn't find any methods with the specified name");
            }
        }

        return methodList;
    }

    public @Nullable ClassInformation getClassInfo() {
        return classInfo;
    }

    public @Nullable Element getMethodElement() {
        return methodElement;
    }

    @Override
    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setNameWithParameters(@NotNull String nameWithParameters) {
        this.nameWithParameters = nameWithParameters;
    }

    @Override
    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    @Override
    public void setRawDescription(@NotNull String rawDescription) {
        this.rawDescription = rawDescription;
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

    public void setMethodList(@NotNull ArrayList<MethodInformation> methodList) {
        this.methodList = methodList;
    }

    /**
     * Gets all methods with the specified name
     *
     * @param methodName The method name being searched for
     * @return The list of methods with that name or null if none are found
     */
    private @Nullable HashMap<Element, String> getMethods(String methodName) {
        HashMap<String, String> foundMethods = new HashMap<>();

        HashMap<String, String> methodLinkList = classMethodLinkList;
        if (methodLinkList == null) return null;

        methodLinkList.forEach((name, url) -> {
            String check = name.substring(0, name.indexOf("("));
            if (check.equalsIgnoreCase(methodName)) {
                foundMethods.put(name, url);
            }
        });

        Document classDocument = classInfo.getClassDocument();

        if (foundMethods.size() == 0) return null;

        // This is the element that is inside the blocklist which contains the method information
        Element aElement = classDocument.selectFirst("a[name=method.detail]");
        if (aElement == null) aElement = classDocument.selectFirst("a[id=method.detail]");

        Element methodList = aElement.parent();

        HashMap<Element, String> methods = new HashMap<>();

        List<Element> elementList = methodList.select("h4").stream()
                .filter(element -> element.text().equalsIgnoreCase(methodName))
                .map(Element::parent)
                .collect(Collectors.toList());

        int i = 0;
        for (String url : foundMethods.values()) {
            methods.put(elementList.get(i), url);
            i++;
        }

        return methods;
    }

    @SuppressWarnings("Duplicates")
    private void init() {
        name = methodElement.selectFirst("h4").text();

        nameWithParameters = "";
        for (Map.Entry<String, String> entrySet : classMethodLinkList.entrySet()) {
            if (entrySet.getValue().equalsIgnoreCase(getUrl())) {
                nameWithParameters = entrySet.getKey();
            }
        }

        if (methodElement.selectFirst("div.block") == null) {
            description = "";
            rawDescription = "";
        } else {
            description = methodElement.selectFirst("div.block").text();
            rawDescription = methodElement.selectFirst("div.block").html();
        }

        extraInformation = Utilites.getExtraInformation(methodElement, false);
        rawExtraInformation = Utilites.getExtraInformation(methodElement, true);
    }

}