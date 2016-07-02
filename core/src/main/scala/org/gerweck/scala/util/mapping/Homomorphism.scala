package org.gerweck.scala.util.mapping

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

  def andThen[C](next: Homomorphism[B, C]): Homomorphism[A, C] = Homomorphism.combine(this, next)
  def compose[A0](h2: Homomorphism[A0, A]): Homomorphism[A0, B] = h2 andThen this
}

object Homomorphism {
  def apply[A, B](forward: A => B, backward: B => A) = new Homomorphism[A, B] {
    def apply(a: A) = forward(a)
    def coapply(b: B) = backward(b)
  }
  def invert[A, B](homo: Homomorphism[A, B]): Homomorphism[B, A] = new Homomorphism[B, A] {
    def apply(b: B) = homo.coapply(b)
    def coapply(a: A) = homo.apply(a)
  }
  def combine[A, B, C](first: Homomorphism[A, B], second: Homomorphism[B, C]): Homomorphism[A, C] = new Homomorphism[A, C] {
    def apply(a: A): C = second(first(a))
    def coapply(c: C): A = first.coapply(second.coapply(c))
  }
}
