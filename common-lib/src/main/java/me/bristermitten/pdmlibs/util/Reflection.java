package me.bristermitten.pdmlibs.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * @author Johnny_JayJay (https://www.github.com/JohnnyJayJay)
 */
public final class Reflection
{

    private Reflection()
    {

    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(@NotNull final Object instance, @NotNull final String name)
    {
        try
        {
            final Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(instance);
        }
        catch (NoSuchFieldException exception)
        {
            throw new RuntimeException("Field could not be found", exception);
        }
        catch (IllegalAccessException exception)
        {
            throw new AssertionError("Field could not be accessed after setting accessible = true", exception);
        }
    }

}
