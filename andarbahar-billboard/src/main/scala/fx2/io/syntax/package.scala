/*
 * Copyright (c) 2018 by Tykhe Gaming Private Limited
 *
 * Licensed under the Software License Agreement (the "License") of Tykhe Gaming Private Limited.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://tykhegaming.github.io/LICENSE.txt.
 *
 * NOTICE
 * ALL INFORMATION CONTAINED HEREIN IS, AND REMAINS THE PROPERTY OF TYKHE GAMING PRIVATE LIMITED.
 * THE INTELLECTUAL AND TECHNICAL CONCEPTS CONTAINED HEREIN ARE PROPRIETARY TO TYKHE GAMING PRIVATE LIMITED AND
 * ARE PROTECTED BY TRADE SECRET OR COPYRIGHT LAW. DISSEMINATION OF THIS INFORMATION OR REPRODUCTION OF THIS
 * MATERIAL IS STRICTLY FORBIDDEN UNLESS PRIOR WRITTEN PERMISSION IS OBTAINED FROM TYKHE GAMING PRIVATE LIMITED.
 */

package fx2

package io

import cats.effect.{Concurrent, ContextShift, IO}
import fs2.Pipe
import fs2.concurrent.{Queue, SignallingRef, Topic}
import javafx.application.Platform
import javafx.concurrent.Worker
import sodium.Subscription
import sodium.syntax._

import scala.concurrent.ExecutionContextExecutor
import scala.reflect.runtime.universe
import scala.util.control.NonFatal

package object syntax {

  implicit class FxOps[A](val self: A) extends AnyVal {

    def fxResolver(implicit tag: universe.TypeTag[A]): FxResolver =
      fxResolver(strict = false)

    def fxResolver(strict: Boolean)(implicit tag: universe.TypeTag[A]): FxResolver =
      (_: String, dependencyType: universe.Type) =>
        if (tag.tpe =:= dependencyType) Some(self)
        else if (!strict && tag.tpe <:< dependencyType) Some(self)
        else None
  }

  implicit class BehaviorFxOps[A](val self: sodium.Behavior[A]) extends AnyVal {

    def cancels[I, O](driver: FxDriver[I, O])(implicit ev: A =:= Boolean): Subscription =
      sodium.Transaction(self.values).cancels(driver)

    def restarts[I, O](driver: FxDriver[I, O])(implicit ev: A =:= Boolean): Subscription =
      sodium.Transaction(self.values).restarts(driver)

    def to(writer: FxWriter[A]): Subscription =
      to(writer, ignore)

    def to(writer: FxWriter[A], onFailure: Throwable => Unit): Subscription =
      to(writer, onFailure, noop)

    def to(writer: FxWriter[A], onFailure: Throwable => Unit, onSuccess: () => Unit): Subscription =
      self.foreach_(a =>
        writer(a) match {
          case Right(_) => onSuccess()
          case Left(ex) => onFailure(ex)
        })
  }

  implicit class CellFxOps[A](val self: sodium.Cell[A]) extends AnyVal {

    def cancels[I, O](driver: FxDriver[I, O])(implicit ev: A =:= Boolean): Subscription =
      self.values.cancels(driver)

    def restarts[I, O](driver: FxDriver[I, O])(implicit ev: A =:= Boolean): Subscription =
      self.values.restarts(driver)

    def to(writer: FxWriter[A]): Subscription =
      to(writer, ignore)

    def to(writer: FxWriter[A], onFailure: Throwable => Unit): Subscription =
      to(writer, onFailure, noop)

    def to(writer: FxWriter[A], onFailure: Throwable => Unit, onSuccess: () => Unit): Subscription =
      self.foreach_(a =>
        writer(a) match {
          case Right(_) => onSuccess()
          case Left(ex) => onFailure(ex)
        })
  }

  implicit class StreamFxOps[A](val self: sodium.Stream[A]) extends AnyVal {

    def cancels[I, O](driver: FxDriver[I, O])(implicit ev: A =:= Boolean): Subscription =
      self.foreach_(a => if (a) driver.cancel())

    def restarts[I, O](driver: FxDriver[I, O])(implicit ev: A =:= Boolean): Subscription =
      self.foreach_(a =>
        if (a && driver.getState != Worker.State.SCHEDULED && driver.getState != Worker.State.RUNNING) driver.restart())

    def to(writer: FxWriter[A]): Subscription =
      to(writer, ignore)

    def to(writer: FxWriter[A], onFailure: Throwable => Unit): Subscription =
      to(writer, onFailure, noop)

    def to(writer: FxWriter[A], onFailure: Throwable => Unit, onSuccess: () => Unit): Subscription =
      self.foreach_(a =>
        writer(a) match {
          case Right(_) => onSuccess()
          case Left(ex) => onFailure(ex)
        })
  }

  implicit class FxReaderOps[I](val self: FxReader[I]) extends AnyVal {

    def updates: sodium.Stream[I] = self.valueProperty().updates.shifted
  }

  implicit class FxResolverOps(val self: FxResolver) extends AnyVal {

    def ++(that: FxResolver): FxResolver =
      chain(that)

    def chain(that: FxResolver): FxResolver =
      (paramName: String, dependencyType: universe.Type) =>
        self.get(paramName, dependencyType).orElse(that.get(paramName, dependencyType))
  }

  implicit class IoStreamFxOps[A](val self: fs2.Stream[IO, A]) {

    def fxDriver[B](writer: FxWriter[B])(
      implicit ECE: ExecutionContextExecutor,
      F: Concurrent[IO],
      CS: ContextShift[IO]): IO[FxDriver[A, B]] =
      SignallingRef[IO, Boolean](false)
        .map(new Display.Reader(self, _) with FxWriter[B] {
          def apply(v1: B): Either[Throwable, Unit] = writer(v1)
        })

    def fxReader(implicit ECE: ExecutionContextExecutor, F: Concurrent[IO], CS: ContextShift[IO]): IO[FxReader[A]] =
      SignallingRef[IO, Boolean](false).map(new Display.Reader(self, _))
  }

  implicit class QueueFxOps[A](val self: Queue[IO, A]) extends AnyVal {

    def fxWriter: FxWriter[A] =
      (v1: A) => self.enqueue1(v1).attempt.unsafeRunSync()
  }

  implicit class TopicFxOps[A](val self: Topic[IO, A]) extends AnyVal {

    def fxWriter: FxWriter[A] =
      (v1: A) => self.publish1(v1).attempt.unsafeRunSync()
  }

  implicit class StreamSinkFxOps[A](val self: sodium.StreamSink[A]) extends AnyVal {

    def push(a: A): IO[Unit] = IO.async[Unit] { cb =>
      Platform.runLater(() =>
        try {
          self.send(a)
          cb(rightUnit)
        } catch {
          case NonFatal(t) => cb(Left(t))
        })
    }

    def sink: Pipe[IO, A, Unit] = _.evalMap(push)
  }

  private val rightUnit = Right(())

  private val noop: () => Unit = () => ()

  private def ignore(ex: Throwable): Unit = ()
}