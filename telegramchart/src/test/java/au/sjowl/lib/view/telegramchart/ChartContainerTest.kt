package au.sjowl.lib.view.telegramchart

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.coroutines.suspendCoroutine

class ChartContainerTest {

    var startTime = 0L

    val justLaunch: (i: Int) -> Job = {
        GlobalScope.launch(Dispatchers.Unconfined) {
            suspendCoroutine<Unit> {}
        }
    }

    @Test
    fun coroutinesTest() {
        warmup()
        benchmark(100, 1)
        benchmark(100, 1)
        benchmark(5, 1)
        benchmark(1000, 1)
    }

    fun warmup() {
        (1..1_000_000).forEach { justLaunch(it).cancel() }
    }

    fun benchmark(tasks: Int, duration: Long) {
        println("\n===================\n\ntasks: $tasks, duration: $duration")
        println("coroutines")
        startTime = System.currentTimeMillis()
        runBlocking {
            GlobalScope.launch {
                val deferredResults = arrayListOf<Deferred<Unit>>()
                repeat(tasks) { deferredResults += async { task(duration) } }
                deferredResults.map { it.await() }
            }.join()
        }
        var tx = System.currentTimeMillis() - startTime
        println("end: $tx, overhead: ${tx - duration}")

        println("sequential")
        startTime = System.currentTimeMillis()
        repeat(tasks) { Thread.sleep(duration) }
        tx = System.currentTimeMillis() - startTime
        println("end: $tx, overhead: ${tx - tasks * duration}")
    }

    suspend fun task(t: Long) {
        Thread.sleep(t)
    }
}
