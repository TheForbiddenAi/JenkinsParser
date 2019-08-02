package me.theforbiddenai.jenkinsparser.impl.entities;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.impl.Utilites;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Element;

import java.util.List;

public class EnumInformation implements Information {

    private final ClassInformation classInfo;
    private final Element enumElement;
    private final String enumUrl;

    public EnumInformation(ClassInformation classInfo, String enumName) {
        this.classInfo = classInfo;

        List<Object> enumInfo = Utilites.getObjectInfo(classInfo, classInfo.getEnumLinkList(), "enum.constant.detail", enumName);
        if (enumInfo == null || enumInfo.size() != 2) {
            throw new NullPointerException("Could not find the specified enum");
        }

        this.enumElement = (Element) enumInfo.get(1);
        this.enumUrl = (String) enumInfo.get(0);

    }

    @Override
    public @NotNull String getName() {
        return enumElement.selectFirst("h4").text();
    }

    @Override
    public @NotNull String getDescription() {
        if (enumElement.selectFirst("div.block") == null) return "";
        return enumElement.selectFirst("div.block").text();
    }

    @Override
    public @NotNull String getRawDescription() {
        if (enumElement.selectFirst("div.block") == null) return "";
        return enumElement.selectFirst("div.block").html();
    }

    @Override
    public @NotNull String getUrl() {
        return enumUrl;
    }

    public @NotNull Element getElement() {
        return enumElement;
    }

}
