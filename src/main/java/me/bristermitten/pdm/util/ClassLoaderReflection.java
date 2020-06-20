package me.bristermitten.pdm.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderReflection
{

    private static volatile Method addUrlMethod;

    private ClassLoaderReflection()
    {

    }

    public static void addURL(URLClassLoader classLoader, URL url)
    {
        Method addUrlMethod = getAddUrlMethod();
        try
        {
            addUrlMethod.invoke(classLoader, url);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    private static Method getAddUrlMethod()
    {
        if (addUrlMethod == null)
        {
            synchronized (ClassLoaderReflection.class)
            {
                if (addUrlMethod == null)
                {
                    Class<URLClassLoader> urlClassLoaderClass = URLClassLoader.class;
                    try
                    {
                        Method addURL = urlClassLoaderClass.getDeclaredMethod("addURL", URL.class);
                        addURL.setAccessible(true);
                        addUrlMethod = addURL;
                    }
                    catch (NoSuchMethodException e)
                    {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        return addUrlMethod;
    }
}
