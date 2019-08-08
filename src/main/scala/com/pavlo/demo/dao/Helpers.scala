package com.pavlo.demo.dao

import java.util.concurrent.TimeUnit

import com.pavlo.demo.models.User
import org.mongodb.scala.{Document, Observable}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Helpers {

  implicit class UserObservable[C](val observable: Observable[User]) extends ImplicitObservable[User] {
    override val converter: (User) => String = (doc) => doc.toString
  }

  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
    override val converter: (Document) => String = (doc) => doc.toJson
  }

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val converter: (C) => String = (doc) => doc.toString
  }

  trait ImplicitObservable[C] {
    val observable: Observable[C]
    val converter: (C) => String

    def results(): Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))

    def headResult() = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))

    def printResults(initial: String = ""): Unit = {
      if (initial.length > 0) print(initial)
      results().foreach(res => println(converter(res)))
    }

    def printHeadResult(initial: String = ""): Unit = println(s"${initial}${converter(headResult())}")
  }

}
