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

    @SuppressWarnings("unchecked")
    public static <T> @NotNull T getFieldValue(@NotNull Object instance, @NotNull String name)
    {
        try
        {
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(instance);
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException("Field could not be found", e);
        }
        catch (IllegalAccessException e)
        {
            throw new AssertionError("Field could not be accessed after setting accessible = true", e);
        }
    }

}
