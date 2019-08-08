package com.pavlo.demo.server

import cats.effect.Sync
import cats.implicits._
import com.pavlo.demo.dao.UserService
import com.pavlo.demo.models.User
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.OptionalQueryParamDecoderMatcher
import org.http4s.headers.Location
import org.http4s.{EntityDecoder, HttpRoutes, Uri}

object DemoRoutes {
  val USER = "user"

  object OptionalLimitQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Int]("limit")

  def userRoutes[F[_] : Sync](US: UserService[F]): HttpRoutes[F] = {
    implicit val decoder: EntityDecoder[F, User] = jsonOf[F, User]
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / USER / id =>
        for {
          option <- US.get(id)
          resp <- option match {
            case Some(user) => Ok(user)
            case None => NotFound()
          }
        } yield resp

      case GET -> Root / USER :? OptionalLimitQueryParamMatcher(limit) =>
        for {
          users <- US.getAll(limit)
          resp <- Ok(users)
        } yield resp

      case req @ PUT -> Root / USER / id =>
        for {
          count <- req.as[User].flatMap(US.update(id, _))
          resp <- count match {
            case 0 => NotFound()
            case _ => NoContent()
          }
        } yield resp

      case req @ POST -> Root / USER =>
        for {
          userWithId <- req.as[User].flatMap(US.create)
          resp <- Created(userWithId, Location(Uri.unsafeFromString(s"$Root/$USER/${userWithId._id}")))
        } yield resp

      case DELETE -> Root / USER / id =>
        for {
          count <- US.delete(id)
          resp <- count match {
            case 0 => NotFound()
            case _ => NoContent()
          }
        } yield resp

      case _ => NotFound()
    }
  }
}
