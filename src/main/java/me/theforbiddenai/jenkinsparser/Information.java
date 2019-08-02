package me.theforbiddenai.jenkinsparser;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public interface Information {


    @NotNull String getName();

    @NotNull String getDescription();

    @NotNull String getRawDescription();

    @NotNull String getUrl();

    @NotNull HashMap<String, List<String>> getExtraInformation (boolean rawHtml);
}
