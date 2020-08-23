package me.bristermitten.pdmcommonlib.pipeline;

public interface PureStage<I, O> extends Stage<I, O>
{

    O process(I input);

    @Override
    default O process(PipelineContext context, I input)
    {
        return process(input);
    }
}
