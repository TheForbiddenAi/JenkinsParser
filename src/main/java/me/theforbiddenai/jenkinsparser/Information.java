package me.theforbiddenai.jenkinsparser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public interface Information {

    @Nullable String getName();

    @Nullable String getDescription();

    @Nullable String getRawDescription();

    @Nullable String getUrl();

    @Nullable HashMap<String, String> getExtraInformation();

    @Nullable HashMap<String, String> getRawExtraInformation();

    void setName(@NotNull String name);

    void setDescription(@NotNull String description);

    void setRawDescription(@NotNull String rawDescription);

    void setUrl(@NotNull String url);

    void setExtraInformation(@NotNull HashMap<String, String> extraInformation);

    void setRawExtraInformation(@NotNull HashMap<String, String> rawExtraInformation);
}
