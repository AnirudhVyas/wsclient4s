package me.free.client.websocket

import com.typesafe.config.ConfigFactory
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import sttp.client3._
import sttp.client3.asynchttpclient.monix.AsyncHttpClientMonixBackend
import sttp.model.Uri
import sttp.ws.WebSocket

import java.net.URI
import scala.util.Try

object WSockClient {

  /**
   * Curried sender that returns a fn that needs message to send and a terminate function.
   * @param endpoint endpoint
   * @return fn that needs input message to send and a terminate fn to halt processing further if condition is satisfied.
   */
  def apply(
             endpoint: String
           ): String => (String => Boolean) => Task[
    Response[Either[String, List[String]]]
  ] = {
    send(
      Task
        .fromTry(Try(URI.create(endpoint)))
        .onErrorRecoverWith[URI] {
          case _: Throwable => Task.fromTry(Try(URI.create(ConfigFactory.load().getString("ws-endpoint"))))
        }
    )(_: String)(_: String => Boolean)
  }.curried

  private def send(
                    uri: Task[URI]
                  )(msg: String)(terminateOn: String => Boolean): Task[Response[Either[String, List[String]]]] = {
    val resultT = uri.flatMap[Response[Either[String, List[String]]]] { uri =>
      implicit val scheduler: Scheduler = Scheduler.global
      AsyncHttpClientMonixBackend()
        .flatMap { backend =>
          basicRequest
            .get(Uri(uri))
            .body(msg)
            .response(
              asWebSocket { (f: WebSocket[Task]) =>
                val observable = Observable
                  .repeatEvalF(f.receiveText()) // repeatedly call receive text to grab messages.
                  .dump("Incoming Msg")         // dumps message to println (can pass logger here)
                  .takeWhileInclusive { e =>    // take all values until condition is met + also include value where
                    // where condition is not met for the first time (for us this would be till completion)
                    println(s"is condition met for $e ${terminateOn(e)}")
                    !terminateOn(e) // negative to accumulate all messages.
                  }
                implicit val scheduler: Scheduler = Scheduler.global
                f.sendText(msg) *> observable.toListL // toListL transforms to a Task.pure(List[String])
              }
            )
            .send(backend)
        }
    }
    resultT
  }
}
