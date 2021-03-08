# wsclient4s

Attempts to abstract on top of existing http/websocket client to make websockets very easy to use and follow. This is a
work in progress.

Sample usage: (note this is going to change, tune back for changes)

```scala
import monix.execution.Scheduler.Implicits.global
import sttp.client3.Response
import me.free.client.websocket._
import scala.concurrent.Await
import scala.concurrent.duration._

object SampleApp extends App {
  // 1. initialize client
  val sockClientFn = WSockClient("wss://echo.websocket.org")
  val inputMessage = """hello world"""
  // 2. register input message
  val registerTerminateOn = sockClientFn(inputMessage)
  // 3. register terminate on condition
  val resultTask = registerTerminateOn(messages => messages.contains("hello"))
  // run!
  // unsafe realm entry
  val r: Response[Either[String, List[String]]] = Await.result(resultTask.runToFuture, 10.seconds)
  assert(r.body.isRight)
  // sample test: check if message contains ws_links
  val result = r.body.getOrElse(List.empty)
  println(s"result was: $result")
  assert(result.nonEmpty)
}
```