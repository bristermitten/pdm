package me.bristermitten.pdmcommonlib.pipeline;

import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

public class SimplePipeline<I, O> implements Pipeline<I, O>
{

    private final List<Stage<?, ?>> stages;
    private final PipelineContext context;

    public SimplePipeline(List<Stage<?, ?>> stages)
    {
        this(stages, new SimplePipelineContext());
    }

    public SimplePipeline(Stage<I, O> stage)
    {
        this(Collections.singletonList(stage), new SimplePipelineContext());
    }

    public SimplePipeline(List<Stage<?, ?>> stages, PipelineContext context)
    {
        this.stages = stages;
        this.context = context;
    }

    @Override
    public @Unmodifiable List<Stage<?, ?>> getStages()
    {
        return Collections.unmodifiableList(stages);
    }

    @SuppressWarnings("unchecked")
    @Override
    public O process(I input)
    {
        Object value = input;
        for (Stage<?, ?> stage : stages)
        {
            value = ((Stage<Object, Object>) stage).process(context, value);
            context.set(stage.getClass().getName(), value);
        }

        return (O) value;
    }
}
