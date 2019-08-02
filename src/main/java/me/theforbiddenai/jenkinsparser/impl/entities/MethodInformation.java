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
    private final String methodUrl;
    private final HashMap<String, String> classMethodLinkList;

    public MethodInformation(@NotNull ClassInformation classInfo, String methodName) {
        methodName = methodName.contains("(") ? methodName.substring(0, methodName.indexOf("(")) : methodName;

        this.classInfo = classInfo;
        this.classMethodLinkList = classInfo.getMethodLinkList();
        try {
            HashMap<Element, String> methods = getMethods(methodName);
            this.methodElement = (Element) methods.keySet().toArray()[0];
            this.methodUrl = (String) methods.values().toArray()[0];
        } catch (NullPointerException | IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            throw new NullPointerException("Couldn't find the specified method!");
        }
    }

    private MethodInformation(ClassInformation classInfo, Element methodElement, String methodUrl) {
        this.classInfo = classInfo;
        this.classMethodLinkList = classInfo.getMethodLinkList();
        this.methodElement = methodElement;
        this.methodUrl = methodUrl;
    }

    @Override
    public @NotNull String getName() {
        return methodElement.selectFirst("h4").text().trim();
    }

    public @NotNull String getNameWithParameters() {
        for(Map.Entry<String, String> entrySet : classMethodLinkList.entrySet()) {
            if(entrySet.getValue().equalsIgnoreCase(getUrl())) {
                return entrySet.getKey();
            }
        }

        return "";
    }

    @Override
    public @NotNull String getDescription() {
        if (methodElement.selectFirst("div.block") == null) return "";
        return methodElement.selectFirst("div.block").text();
    }

    @Override
    public @NotNull String getRawDescription() {
        if (methodElement.selectFirst("div.block") == null) return "";
        return methodElement.selectFirst("div.block").html();
    }

    @Override
    public @NotNull String getUrl() {
        return methodUrl;
    }

    public @NotNull HashMap<String, List<String>> getExtraInformation (boolean rawHtml) {
        return Utilites.getExtraInformation(methodElement, rawHtml);
    }

    public @NotNull Element getMethodElement() {
        return methodElement;
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
            if(check.equalsIgnoreCase(methodName)) {
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
        for(String url : foundMethods.values()) {
            methods.put(elementList.get(i), url);
            i++;
        }

        return methods;
    }

    /**
     * Gets all methods with the same name as the current name
     *
     * @return A list of methods that share the same name or null if none are found
     */
    public List<MethodInformation> getAllMethods() {
        HashMap<Element, String> methods = getMethods(getName());
        if (methods == null) {
            throw new NullPointerException("Couldn't find any methods with the specified name");
        }

        List<MethodInformation> methodInfoList = new ArrayList<>();
        methods.forEach((element, url) -> methodInfoList.add(new MethodInformation(classInfo, element, url)));

        return methodInfoList;
    }

}
