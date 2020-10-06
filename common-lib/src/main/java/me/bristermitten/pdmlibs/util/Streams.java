package me.bristermitten.pdmlibs.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Streams
{

    private Streams()
    {

    }

    @Nullable
    public static String toString(@NotNull final InputStream stream)
    {
        final byte[] bytes = toByteArray(stream);

        if (bytes.length == 0)
        {
            return null;
        }

        return new String(bytes);
    }

    @NotNull
    public static byte @NotNull [] toByteArray(@NotNull final InputStream stream)
    {
        try (final InputStream in = stream)
        {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();

            int next;
            while ((next = in.read()) != -1)
            {
                output.write(next);
            }

            return output.toByteArray();
        }
        catch (IOException exception)
        {
            return new byte[0];
        }
    }

    @NotNull
    public static InputStream createEmptyStream()
    {
        return new ByteArrayInputStream(new byte[0]);
    }
}
