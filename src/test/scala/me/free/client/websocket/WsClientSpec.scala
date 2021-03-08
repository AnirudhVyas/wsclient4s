package me.free.client.websocket

import me.free.client.BaseFeatureSpec
import monix.execution.Scheduler.Implicits.global
import sttp.client3.Response

import scala.concurrent.Await
import scala.concurrent.duration._

class WsClientSpec extends BaseFeatureSpec {

  Feature("Websocket client tests") {
    Scenario("happy path single message request response test") {
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
  }
}