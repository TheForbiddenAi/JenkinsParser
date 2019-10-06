package me.theforbiddenai.jenkinsparser.impl.entities;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.impl.Utilites;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;

public class FieldInformation implements Information {

    private final ClassInformation classInfo;
    private final Element fieldElement;

    private String name;
    private String description;
    private String rawDescription;
    private String url;

    private HashMap<String, String> extraInformation;
    private HashMap<String, String> rawExtraInformation;

    public FieldInformation(@Nullable ClassInformation classInfo, @Nullable Element fieldElement) {
        this.classInfo = classInfo;
        this.fieldElement = fieldElement;

        try {
            init();
        } catch (Exception ignored) {}
        if (fieldElement != null && classInfo != null) {
            try {
                this.url = classInfo.getUrl() + "#" + getName();
            } catch (Exception ignored) {
            }
        }
    }

    public FieldInformation(ClassInformation classInfo, String fieldName) {
        this.classInfo = classInfo;

        List<Object> fieldInfo = Utilites.getObjectInfo(classInfo, classInfo.getFieldLinkList(), "field.detail", fieldName);
        if (fieldInfo == null || fieldInfo.size() != 2) {
            throw new NullPointerException("Could not find the specified field");
        }

        this.fieldElement = (Element) fieldInfo.get(1);
        this.url = (String) fieldInfo.get(0);

        init();
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @NotNull String getRawDescription() {
        return rawDescription;
    }

    @Override
    public @NotNull String getUrl() {
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

    public @Nullable ClassInformation getClassInfo() {
        return classInfo;
    }

    public @Nullable Element getElement() {
        return fieldElement;
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


    @SuppressWarnings("Duplicates")
    private void init() {
        name = fieldElement.selectFirst("h4").text();

        if (fieldElement.selectFirst("div.block") == null) {
            description = "";
            rawDescription = "";
        } else {
            description = fieldElement.selectFirst("div.block").text();
            rawDescription = fieldElement.selectFirst("div.block").html();
        }

        extraInformation = Utilites.getExtraInformation(fieldElement, false);
        rawExtraInformation = Utilites.getExtraInformation(fieldElement, true);
    }

}
