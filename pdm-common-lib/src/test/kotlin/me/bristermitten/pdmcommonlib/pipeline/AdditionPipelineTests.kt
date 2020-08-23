package me.bristermitten.pdmcommonlib.pipeline

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AdditionPipelineTests
{

	@Test
	fun `Test Simple Addition Pipeline`()
	{
		val stages = listOf<Stage<Int, Int>>(
				Stage { _, i -> i + 1 },
				Stage { _, i -> i + i },
		)

		val pipeline = SimplePipeline<Int, Int>(stages)

		assertEquals(8, pipeline.process(3))
	}

	@Test
	fun `Test Simple Multiplication Pipeline with Variables`()
	{
		val stages = listOf<Stage<Int, Int>>(
				Stage { context, i ->
					context["double"] = i * 2
					i + 1
				},
				Stage { context, i ->
					context.get<Int>("double") + i
				},
		)

		val pipeline = SimplePipeline<Int, Int>(stages)

		assertEquals(10, pipeline.process(3))
	}

	@Test
	fun `Test Simple Pipeline with different types`()
	{
		val stages = listOf(
				PureStage { i: Int ->
					i.toString(16)
				},
				PureStage { s: String ->
					setOf(3, s)
				},
		)

		val pipeline = SimplePipeline<Int, Set<Any>>(stages)

		assertEquals(setOf(3, "14"), pipeline.process(20))
	}

	@Test
	fun `Test Simple Pipeline with different Pipelines`()
	{
		val doublePipeline = SimplePipeline(PureStage<Int, Int> { it * 2 })
		val doubleAndAdd3Pipeline = SimplePipeline<Int, Int>(
				listOf(doublePipeline, PureStage { it + 3 })
		)

		assertEquals(19, doubleAndAdd3Pipeline.process(8))
	}
}
