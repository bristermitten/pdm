package me.bristermitten.pdmcommonlib.pipeline;

public interface PipelineContext
{

    <I, O> O getResult(Stage<I, O> stage) throws IllegalStateException;

    <T> void set(String key, T value);

    <T> T get(String key) throws IllegalStateException;

}
