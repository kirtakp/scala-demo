package com.pavlo.demo.models

import cats.Applicative
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import org.mongodb.scala.bson.ObjectId

case class UserWithId(_id: ObjectId, name: String, age: Int)

object UserWithId {
  def apply(user: User): UserWithId =
    UserWithId(new ObjectId(), user.name, user.age)

  implicit val userWithIdEncoder: Encoder[UserWithId] = new Encoder[UserWithId] {
    final def apply(user: UserWithId): Json = Json.obj(
      ("_id", Json.fromString(user._id.toHexString)),
      ("name", Json.fromString(user.name)),
      ("age", Json.fromInt(user.age))
    )
  }

  implicit def userWithIdEntityEncoder[F[_] : Applicative]: EntityEncoder[F, UserWithId] =
    jsonEncoderOf[F, UserWithId]
}
