package com.pavlo.demo.server

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import com.pavlo.demo.dao.UserService
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

object DemoServer {
  def stream[F[_] : ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    val userServiceAlg = UserService.impl[F]
    val httpApp = DemoRoutes.userRoutes[F](userServiceAlg).orNotFound
    val finalHttpApp = Logger.httpApp(true, true)(httpApp)

    for {
      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
