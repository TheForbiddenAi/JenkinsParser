package me.theforbiddenai.jenkinsparser;

import org.jetbrains.annotations.NotNull;

public interface Information {

    @NotNull
    String getName();

    @NotNull
    String getDescription();

    @NotNull
    String getRawDescription();

    @NotNull
    String getUrl();

}
