package me.bristermitten.pdmcommonlib.pipeline;

/**
 * A stage that cannot interact with a context at all, it is simply a transformation from input to output
 *
 * @param <I>
 * @param <O>
 */
public interface PureStage<I, O> extends Stage<I, O>
{

    O process(I input);

    @Override
    default O process(PipelineContext context, I input)
    {
        return process(input);
    }
}
