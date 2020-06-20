package me.bristermitten.pdm.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class InputStreamUtil
{

    private InputStreamUtil()
    {

    }

    public static String readAll(InputStream inputStream)
    {
        try (InputStreamReader ir = new InputStreamReader(inputStream))
        {
            try (StringWriter out = new StringWriter())
            {
                int next;
                while ((next = ir.read()) != -1)
                {
                    out.write(next);
                }
                return out.toString();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
