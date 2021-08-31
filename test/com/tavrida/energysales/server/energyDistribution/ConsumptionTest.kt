package com.tavrida.energysales.server.energyDistribution

import com.tavrida.energysales.energy_distribution.readingDeltaWithCycle
import com.tavrida.utils.round3
import io.kotest.matchers.shouldBe
import org.junit.Test

class ConsumptionTest {
    @Test
    fun readingDeltaWithCycleTest() {
        readingDeltaWithCycle(97210.0, 1204.0) shouldBe (101204.0 - 97210.0).round3()
        readingDeltaWithCycle(97210.0, 1204.563) shouldBe (101204.563 - 97210.0).round3()
        readingDeltaWithCycle(97210.861, 1204.563) shouldBe (101204.563 - 97210.861).round3()

        readingDeltaWithCycle(97210.861, 204.563) shouldBe (100204.563 - 97210.861).round3()
        readingDeltaWithCycle(97210.861, 4.563) shouldBe (100004.563 - 97210.861).round3()
        readingDeltaWithCycle(97210.861, 0.563) shouldBe (100000.563 - 97210.861).round3()

        readingDeltaWithCycle(97210.861, 0.0) shouldBe (100000.0 - 97210.861).round3()

        readingDeltaWithCycle(9721011112.861, 0.0) shouldBe (10000000000.0 - 9721011112.861).round3()
    }
}