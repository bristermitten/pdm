package me.bristermitten.pdm.util;

import com.google.gson.Gson;

public class Constants
{

    private Constants()
    {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static final Gson GSON = new Gson();
}
