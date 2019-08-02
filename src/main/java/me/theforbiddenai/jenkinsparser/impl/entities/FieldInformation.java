package me.theforbiddenai.jenkinsparser.impl.entities;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.impl.Utilites;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;

public class FieldInformation implements Information {

    private final ClassInformation classInfo;
    private final Element fieldElement;
    private final String fieldUrl;

    public FieldInformation(ClassInformation classInfo, String fieldName) {
        this.classInfo = classInfo;

        List<Object> fieldInfo = Utilites.getObjectInfo(classInfo, classInfo.getFieldLinkList(), "field.detail", fieldName);
        if (fieldInfo == null || fieldInfo.size() != 2) {
            throw new NullPointerException("Could not find the specified field");
        }

        this.fieldElement = (Element) fieldInfo.get(1);
        this.fieldUrl = (String) fieldInfo.get(0);
    }

    @Override
    public @NotNull String getName() {
        return fieldElement.selectFirst("h4").text();
    }

    @Override
    public @NotNull String getDescription() {
        if (fieldElement.selectFirst("div.block") == null) return "";
        return fieldElement.selectFirst("div.block").text();
    }

    @Override
    public @NotNull String getRawDescription() {
        if (fieldElement.selectFirst("div.block") == null) return "";
        return fieldElement.selectFirst("div.block").html();
    }

    @Override
    public @NotNull String getUrl() {
        return fieldUrl;
    }

    public @NotNull Element getElement() {
        return fieldElement;
    }

    public @NotNull HashMap<String, List<String>> getExtraInformation(boolean rawHtml) {
        return Utilites.getExtraInformation(fieldElement, rawHtml);
    }

}
