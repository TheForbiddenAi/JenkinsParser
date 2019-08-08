package me.theforbiddenai.jenkinsparser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public interface Information {

    @Nullable String getName();

    @Nullable String getDescription();

    @Nullable String getRawDescription();

    @Nullable String getUrl();

    @Nullable HashMap<String, List<String>> getExtraInformation();

    @Nullable HashMap<String, List<String>> getRawExtraInformation();

    void setName(@NotNull String name);

    void setDescription(@NotNull String description);

    void setRawDescription(@NotNull String rawDescription);

    void setUrl(@NotNull String url);

    void setExtraInformation(@NotNull HashMap<String, List<String>> extraInformation);

    void setRawExtraInformation(@NotNull HashMap<String, List<String>> rawExtraInformation);
}
