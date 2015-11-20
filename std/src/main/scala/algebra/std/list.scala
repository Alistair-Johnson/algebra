package algebra
package std

import scala.annotation.tailrec
import scala.collection.mutable

package object list extends ListInstances 

trait ListInstances {
  implicit def listOrder[A: Order] = new ListOrder[A]
  implicit def listMonoid[A] = new ListMonoid[A]
}

trait ListInstances1 {
  implicit def listPartialOrder[A: PartialOrder] = new ListPartialOrder[A]
}

trait ListInstances2 {
  implicit def listEq[A: Eq] = new ListEq[A]
}

class ListOrder[A](implicit ev: Order[A]) extends Order[List[A]] {
  def compare(xs: List[A], ys: List[A]): Int = {
    @tailrec def loop(xs: List[A], ys: List[A]): Int =
      xs match {
        case Nil =>
          if (ys.isEmpty) 0 else -1
        case x :: xs =>
          ys match {
            case Nil => 1
            case y :: ys =>
              val n = ev.compare(x, y)
              if (n != 0) n else loop(xs, ys)
          }
      }
    loop(xs, ys)
  }
}

class ListPartialOrder[A](implicit ev: PartialOrder[A]) extends PartialOrder[List[A]] {
  def partialCompare(xs: List[A], ys: List[A]): Double = {
    @tailrec def loop(xs: List[A], ys: List[A]): Double =
      xs match {
        case Nil =>
          if (ys.isEmpty) 0.0 else -1.0
        case x :: xs =>
          ys match {
            case Nil => 1.0
            case y :: ys =>
              val n = ev.partialCompare(x, y)
              if (n != 0.0) n else loop(xs, ys)
          }
      }
    loop(xs, ys)
  }
}

class ListEq[A](implicit ev: Eq[A]) extends Eq[List[A]] {
  def eqv(x: List[A], y: List[A]): Boolean = {
    def loop(xs: List[A], ys: List[A]): Boolean =
      xs match {
        case Nil =>
          ys.isEmpty
        case x :: xs =>
          ys match {
            case y :: ys =>
              if (ev.eqv(x, y)) loop(xs, ys) else false
            case Nil =>
              false
          }
      }
    loop(x, y)
  }
}

class ListMonoid[A] extends Monoid[List[A]] {
  def empty: List[A] = Nil
  def combine(x: List[A], y: List[A]): List[A] = x ::: y

  override def combineN(x: List[A], n: Int): List[A] = {
    val buf = mutable.ListBuffer.empty[A]
    var i = n
    while (i > 0) {
      buf ++= x
      i -= 1
    }
    buf.toList
  }

  override def combineAll(xs: TraversableOnce[List[A]]): List[A] = {
    val buf = mutable.ListBuffer.empty[A]
    xs.foreach(buf ++= _)
    buf.toList
  }
}
