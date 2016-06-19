package org.gerweck.scala.util.mapping

import language.implicitConversions

/** A bidirectional correspondence between two types where values can
  * be represented in both ways, but doing a reverse transformation
  * only guarantees an equivalent output. There is an equivalence
  * relation `a =~= b` such that `coapply(apply(x)) ~=~ x` and
  * `apply(coapply(y) =~= y`.
  */
trait Homomorphism[A, B] {
  def apply(a: A): B
  def coapply(b: B): A
  final def unapply(b: B): Option[A] = Some(coapply(b))

  def invert: Homomorphism[B, A] = Homomorphism.invert(this)
}
object Homomorphism {
  object Implicits {
    implicit def inversion[A, B](homo: Homomorphism[A, B]): Homomorphism[B, A] = invert(homo)
  }
  def apply[A, B](forward: A => B, backward: B => A) = new Homomorphism[A, B] {
    def apply(a: A) = forward(a)
    def coapply(b: B) = backward(b)
  }
  def invert[A, B](homo: Homomorphism[A, B]): Homomorphism[B, A] = new Homomorphism[B, A] {
    def apply(b: B) = homo.coapply(b)
    def coapply(a: A) = homo.apply(a)
  }
}
