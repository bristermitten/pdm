package me.bristermitten.pdmcommonlib.pipeline;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimplePipelineContext implements PipelineContext
{

    private final Map<String, Object> data = new LinkedHashMap<>();

    @Override
    public <I, O> O getResult(Stage<I, O> stage) throws IllegalStateException
    {
        try
        {
            return get(stage.getClass().getName());
        }
        catch (IllegalStateException e)
        {
            throw new IllegalStateException("Stage " + stage.getClass().getName() + " has not yet been executed.", e);
        }
    }

    @Override
    public <T> void set(String key, T value)
    {
        data.put(key, value);
    }

    @Override
    public <T> T get(String key)
    {
        //noinspection unchecked
        final T value = (T) data.get(key);
        if (value == null)
        {
            throw new IllegalStateException("No such property " + key);
        }
        return value;
    }
}
