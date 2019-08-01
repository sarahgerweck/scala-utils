package org.gerweck.scala.util.stream

import akka.stream._
import akka.stream.stage._

/** A [[akka.stream.scaladsl.Flow]] that has a piece of state that it either updates or emits and
  * replaces.
  *
  * A common pattern in stream processing is that you need to collect data that spans across
  * several input elements if they share some key, or you output your state and start over when
  * you get a new key. '''In this model, you never have more than one piece of state at a time.'''
  *
  * A simple example of this is run length encoder, where your input is a stream of `Symbol`,
  * and your output is a stream of `(Symbol, Int)` pairs that represent how many times you saw
  * that symbol. At each point in time, you're going to keep a `(Symbol, Int)` pair, emitting the
  * pending pair if you get a new symbol or the stream ends.
  *
  * This can be implemented like this:
  *
  * {{{
  * class RleFlow[A] extends BuildingFlow[A, (A, Int)] {
  *   protected[this] override def absorb(a: A) = (A, 1)
  *   protected[this] override def combine(state: (A, Int), a: A) = {
  *     if (a == state._1) {
  *       // We have the same symbol, so increment our counter and keep our state
  *       CanCombine((a, state._2 + 1))
  *     } else {
  *       // This is a new symbol, so emit our existing state and absorb this element
  *       NoCombine
  *     }
  *   }
  * }
  * }}}
  *
  * See that there are two methods that you must override:
  *
  *  - [[BuildingFlow.absorb]] uses a single element to seed the state when there is no existing
  *    state available.
  *  - [[BuildingFlow.combine]] uses the current state plus a new element and decides whether that
  *    element can be added to the current state.
  *
  * Note that it's not trivial to implement this using built-in `Flow` combinators, because you
  * need to emit any pending state when the stream ends. It's possible to do this by concatenating
  * a special symbol that you use when the stream ends, but this complicates the code and often
  * necessitates a wrapper.
  *
  * This stage does not provide a mechanism to transform the state for output, as this can be
  * trivially accomplished by calling `.map(…)` on your `BuildingFlow`.
  *
  * @tparam A the type of object that flows into this graph stage
  * @tparam B the state representation that the stage builds and the output of the flow stage. It
  * is permissible for this state to be mutable provided that you follow the rules described in [[BuildingFlow.combine]].
  */
abstract class BuildingFlow[A, B] extends GraphStage[FlowShape[A, B]] {
  final val in: Inlet[A] = Inlet("Input")
  final val out: Outlet[B] = Outlet("Output")
  override final val shape: FlowShape[A, B] = FlowShape(in, out)

  protected[this] final val NoCombine = new BuildingFlow.CombineResult[B](BuildingFlow.noCombineInner)
  @inline
  protected[this] final def CanCombine(b: B) = new BuildingFlow.CombineResult[B](b)

  /** Seed the state from an incoming element.
    *
    * This is only called when the state is empty, either because this is the first element or
    * because you've just emitted an element because [[BuildingFlow.combine]] indicated that the
    * incoming element could not be combined into the current state.
    *
    * If you need to consider the previous state or differentiate whether this is the first
    * element, your use case is not suitable for a `BuildingFlow`.
    *
    * @param a the incoming element
    * @return the fresh state
    */
  protected[this] def absorb(a: A): B

  /** Combine the incoming element into the state, or indicate that you should emit the current
    * state and start a new state using [[absorb]].
    *
    * This method must produce a [[BuildingFlow.CombineResult]] through one of two mechanisms:
    *  - Call [[CanCombine]] with the updated state if the incoming element can be added into the
    *    current state.
    *    - In this case, no element will be emitted.
    *  - Use [[NoCombine]] if the incoming element cannot be added into the current state.
    *    - In this case, the current state will be emitted and then [[absorb]] will be called to
    *      seed the new state with the incoming element.
    *    - If you have need to both update the state and emit an element, `BuildingFlow` is not
    *      suitable for your use case.
    *
    * @note it is safe to mutate the `state` object ''if and only if'' you return a `CanCombine`.
    * You may not mutate the state at output time. (If you are using mutability for performance
    * reasons while building the state, you may wish to add a stage after this one that eliminates
    * mutable access. This can be accomplished by simply calling `.map` on the `BuildingFlow` and
    * transforming the output into an immutable object.)
    *
    * @param state the current state
    * @param a the incoming element
    * @return a [[BuildingFlow.CombineResult]] produced from [[CanCombine]] or [[NoCombine]]
    */
  protected[this] def combine(state: B, a: A): BuildingFlow.CombineResult[B]

  override final def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
    /* This pair is effectively an Option, but we don't want to pay the boxing penalty for every
     * new object. This is a considerable cost savings for a large stream */
    private[this] var beforeFirst: Boolean = true
    private[this] var current: B = _

    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        val elem = grab(in)
        if (beforeFirst) {
          current = absorb(elem)
          beforeFirst = false
          pull(in)
        } else {
          val cr = combine(current, elem)
          if (cr.value == BuildingFlow.noCombineInner) {
            val complete = current
            current = absorb(elem)
            push(out, complete)
          } else {
            current = cr.value.asInstanceOf[B]
            pull(in)
          }
        }
      }
      override def onUpstreamFinish(): Unit = {
        /* This builder _always_ has pending state unless we never got any elements */
        if (!beforeFirst) {
          emit(out, current)
        }
        complete(out)
      }
    })

    setHandler(out, new OutHandler {
      override def onPull(): Unit = pull(in)
    })
  }
}

object BuildingFlow {
  private[BuildingFlow] final val noCombineInner: Any = new Object
  final class CombineResult[A] private[BuildingFlow] (private[BuildingFlow] val value: Any) extends AnyVal
}
