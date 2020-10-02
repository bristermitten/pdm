package me.bristermitten.pdm.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderReflection
{

    private static final Method ADD_URL_METHOD;

    static
    {
        final Method addURL;

        try
        {
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
        }
        catch (NoSuchMethodException exception)
        {
            throw new AssertionError(exception);
        }

        ADD_URL_METHOD = addURL;
    }

    private ClassLoaderReflection()
    {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static void addURL(@NotNull final URLClassLoader classLoader, @NotNull final URL url)
    {
        try
        {
            ADD_URL_METHOD.invoke(classLoader, url);
        }
        catch (IllegalAccessException | InvocationTargetException exception)
        {
            throw new IllegalArgumentException(exception);
        }
    }
}
