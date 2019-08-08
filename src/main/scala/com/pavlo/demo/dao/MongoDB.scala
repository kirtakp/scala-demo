package com.pavlo.demo.dao

import com.pavlo.demo.models.{User, UserWithId}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

object MongoDB {
  private val USER = "test"
  private val PWD = "test"
  private val DB_NAME = "test"
  private val HOST = "testcluster-4plis.mongodb.net"
  private val URI = s"mongodb+srv://$USER:$PWD@$HOST"
  private val USER_COLLECTION = "users"

  private def init: MongoDatabase = {
    val codecRegistry = fromRegistries(fromProviders(classOf[User], classOf[UserWithId]), DEFAULT_CODEC_REGISTRY)
    val client: MongoClient = MongoClient(URI)
    client.getDatabase(DB_NAME).withCodecRegistry(codecRegistry)
  }

  private val db: MongoDatabase = init

  def userCollection: MongoCollection[User] = {
    db.getCollection(USER_COLLECTION)
  }

  def userWithIdCollection: MongoCollection[UserWithId] = {
    db.getCollection(USER_COLLECTION)
  }
}
