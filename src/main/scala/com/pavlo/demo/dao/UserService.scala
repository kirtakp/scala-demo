package com.pavlo.demo.dao

import cats.Applicative
import cats.implicits._
import com.pavlo.demo.dao.Helpers._
import com.pavlo.demo.models.{User, UserWithId}
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._

trait UserService[F[_]] {
  def get(id: String): F[Option[User]]
  def getAll(limit: Option[Int]): F[List[UserWithId]]
  def update(id: String, user: User): F[Long]
  def create(user: User): F[UserWithId]
  def delete(id: String): F[Long]
}

object UserService {
  def impl[F[_] : Applicative]: UserService[F] = new UserService[F] {

    val DEFAULT_GET_ALL_LIMIT = 100

    val users: MongoCollection[User] = MongoDB.userCollection
    val usersWithId: MongoCollection[UserWithId] = MongoDB.userWithIdCollection

    override def get(id: String): F[Option[User]] =
      if (ObjectId.isValid(id))
        users.find(equal("_id", new ObjectId(id))).headResult() match {
          case u: User => Option(u).pure[F]
          case _ => Option.empty[User].pure[F]
        }
      else Option.empty[User].pure[F]

    override def getAll(limit: Option[Int]): F[List[UserWithId]] =
      usersWithId.find().limit(limit match {
        case Some(value) => value
        case None => DEFAULT_GET_ALL_LIMIT
      }).results().toList.pure[F]

    override def update(id: String, user: User): F[Long] =
      if (ObjectId.isValid(id))
        users.replaceOne(equal("_id", new ObjectId(id)), user).headResult().getModifiedCount.pure[F]
      else 0L.pure[F]

    override def create(user: User): F[UserWithId] = {
      val userWithId = UserWithId(new ObjectId(), user)
      usersWithId.insertOne(userWithId).headResult()
      userWithId.pure[F]
    }

    override def delete(id: String): F[Long] =
      if (ObjectId.isValid(id))
        users.deleteOne(equal("_id", new ObjectId(id))).headResult().getDeletedCount.pure[F]
      else 0L.pure[F]
  }
}


