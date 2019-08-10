package com.pavlo.demo.dao

import cats.Applicative
import cats.effect.IO._
import cats.effect.{IO, LiftIO}
import cats.implicits._
import com.pavlo.demo.models.{User, UserWithId}
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters._

import scala.concurrent.ExecutionContext.global

trait UserService[F[_]] {
  def get(id: String): F[Option[User]]
  def getAll(limit: Option[Int]): F[Seq[UserWithId]]
  def update(id: String, user: User): F[Long]
  def create(user: User): F[UserWithId]
  def delete(id: String): F[Long]
}

object UserService {
  def impl[F[_] : Applicative : LiftIO]: UserService[F] = new UserService[F] {
    private implicit val cs = IO.contextShift(global)

    val DEFAULT_GET_ALL_LIMIT = 100

    val users: MongoCollection[User] = MongoDB.userCollection
    val usersWithId: MongoCollection[UserWithId] = MongoDB.userWithIdCollection

    override def get(id: String): F[Option[User]] =
      if (ObjectId.isValid(id))
        fromFuture(IO(users.find(equalById(id)).head())).map {
          case u: User => Option(u)
          case _ => Option.empty[User]
        }.to[F]
      else Option.empty[User].pure[F]

    override def getAll(limit: Option[Int]): F[Seq[UserWithId]] =
      fromFuture(IO(usersWithId.find().limit(limit match {
        case Some(value) => value
        case None => DEFAULT_GET_ALL_LIMIT
      }).toFuture())).to[F]

    override def update(id: String, user: User): F[Long] =
      if (ObjectId.isValid(id))
        fromFuture(IO(users.replaceOne(equalById(id), user).toFuture())).map(_.getModifiedCount).to[F]
      else 0L.pure[F]

    override def create(user: User): F[UserWithId] = {
      val userWithId = UserWithId(user)
      fromFuture(IO(usersWithId.insertOne(userWithId).head())).map(_ => userWithId).to[F]
    }

    override def delete(id: String): F[Long] =
      if (ObjectId.isValid(id))
        fromFuture(IO(users.deleteOne(equalById(id)).toFuture())).map(_.getDeletedCount).to[F]
      else 0L.pure[F]
  }

  def equalById(_id: String): Bson =
    equal("_id", new ObjectId(_id))
}


