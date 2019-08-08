package com.pavlo.demo

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.pavlo.demo.server.DemoServer

object DemoMain extends IOApp {
  def run(args: List[String]) =
    DemoServer.stream[IO].compile.drain.as(ExitCode.Success)
}
