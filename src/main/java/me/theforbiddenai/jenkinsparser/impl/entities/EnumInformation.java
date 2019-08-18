package me.theforbiddenai.jenkinsparser.impl.entities;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.impl.Utilites;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;

public class EnumInformation implements Information {

    private final ClassInformation classInfo;
    private final Element enumElement;

    private String name;
    private String description;
    private String rawDescription;
    private String url;

    private HashMap<String, String> extraInformation;
    private HashMap<String, String> rawExtraInformation;

    public EnumInformation(@Nullable ClassInformation classInfo, @Nullable Element enumElement) {
        this.classInfo = classInfo;
        this.enumElement = enumElement;

        init();
        if (enumElement != null && classInfo != null) {
            try {
                this.url = classInfo.getUrl() + "#" + getName();
            } catch (Exception ignored) {}
        }
    }

    public EnumInformation(ClassInformation classInfo, String enumName) {
        this.classInfo = classInfo;

        List<Object> enumInfo = Utilites.getObjectInfo(classInfo, classInfo.getEnumLinkList(), "enum.constant.detail", enumName);
        if (enumInfo == null || enumInfo.size() != 2) {
            throw new NullPointerException("Could not find the specified enum");
        }

        this.enumElement = (Element) enumInfo.get(1);
        this.url = (String) enumInfo.get(0);
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

    public @Nullable ClassInformation getClassInfo() {
        return classInfo;
    }

    public @Nullable Element getElement() {
        return enumElement;
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

    @SuppressWarnings("Duplicates")
    private void init() {
        try {
            name = enumElement.selectFirst("h4").text();

            if (enumElement.selectFirst("div.block") == null) {
                description = "";
                rawDescription = "";
            } else {
                description = enumElement.selectFirst("div.block").text();
                rawDescription = enumElement.selectFirst("div.block").html();
            }

            extraInformation = Utilites.getExtraInformation(enumElement, false);
            rawExtraInformation = Utilites.getExtraInformation(enumElement, true);
        } catch (NullPointerException ignored) {
        }
    }


}
