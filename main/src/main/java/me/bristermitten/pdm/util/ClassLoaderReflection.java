package me.bristermitten.pdm.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderReflection
{

    private static final Method addUrlMethod;

    static
    {
        Method addURL;
        try
        {
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
        }
        catch (NoSuchMethodException e)
        {
            addURL = null;
        }
        addUrlMethod = addURL;
    }

    private ClassLoaderReflection()
    {

    }

    public static void addURL(URLClassLoader classLoader, URL url)
    {
        try
        {
            addUrlMethod.invoke(classLoader, url);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
