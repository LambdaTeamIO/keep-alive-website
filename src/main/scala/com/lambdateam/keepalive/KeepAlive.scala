package com.lambdateam.keepalive

import fs2.{Scheduler, Strategy, Task}
import org.http4s.Status
import org.http4s.client.blaze._

import scala.concurrent.duration._

object KeepAlive extends App {

  implicit val S = Strategy.fromExecutionContext(scala.concurrent.ExecutionContext.Implicits.global)
  implicit val R = Scheduler.fromFixedDaemonPool(2, "loop")

  val httpClient = PooledHttp1Client()

  val program = httpClient.get[Status]("https://lambda-website.herokuapp.com/")(r => Task.delay(r.status))

  def loop[A](task: Task[A]): Unit = {
    task.unsafeRun()
    loop(task.schedule(30.seconds))
  }

  loop(program)

}
