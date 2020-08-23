package me.bristermitten.pdmcommonlib.pipeline;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * A pipeline represents a blocking transformation between 2 values.
 * A typical pipeline has multiple stages
 */
public interface Pipeline<I, O> extends PureStage<I, O>
{

    @Unmodifiable
    List<Stage<?, ?>> getStages();
}
