package algebra

import scala.{ specialized => sp }
import scala.collection.SeqLike
import scala.collection.generic.CanBuildFrom
import scala.reflect.ClassTag
import scala.annotation.tailrec

trait CoordinateSpace[V, @sp(Float, Double) F] extends InnerProductSpace[V, F] {
  def dimensions: Int

  def coord(v: V, i: Int): F  // = v dot axis(i)

  def axis(i: Int): V

  def _x(v: V): F = coord(v, 0)
  def _y(v: V): F = coord(v, 1)
  def _z(v: V): F = coord(v, 2)

  def basis: Vector[V] = Vector.tabulate(dimensions)(axis)

  def dot(v: V, w: V): F = {
    @tailrec def loop(sum: F, i: Int): F = if (i < dimensions) {
      loop(scalar.plus(sum, scalar.times(coord(v, i), coord(w, i))), i + 1)
    } else {
      sum
    }

    loop(scalar.zero, 0)
  }
}

object CoordinateSpace {
  @inline final def apply[V, @sp(Float,Double) F](implicit V: CoordinateSpace[V, F]) = V
}
