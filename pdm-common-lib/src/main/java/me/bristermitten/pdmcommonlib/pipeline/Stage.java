package me.bristermitten.pdmcommonlib.pipeline;

public interface Stage<I, O>
{
    O process(PipelineContext context, I input);
}
