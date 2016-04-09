package kits

trait Semigroup[A] {

  def append(x: A, y: A): A

}

object Semigroup {

  implicit val Conj: Monoid[Boolean] =
    new Monoid[Boolean] {
      val empty: Boolean = true
      def append(x: Boolean, y: Boolean): Boolean = x && y
    }

  implicit val Disj: Monoid[Boolean] =
    new Monoid[Boolean] {
      val empty: Boolean = false
      def append(x: Boolean, y: Boolean): Boolean = x || y
    }

  implicit val String: Monoid[String] =
    new Monoid[String] {
      val empty: String = ""
      def append(x: String, y: String): String = x + y
    }

  implicit val Unit: Monoid[Unit] =
    new Monoid[Unit] {
      val empty: Unit = ()
      def append(x: Unit, y: Unit): Unit = ()
    }

  implicit def Pair[A, B](implicit A: Monoid[A], B: Monoid[B]): Monoid[(A, B)] =
    new Monoid[(A, B)] {
      val empty: (A, B) = (A.empty, B.empty)
      def append(x: (A, B), y: (A, B)): (A, B) =
        (x, y) match {
          case ((ax, bx), (ay, by)) => (A.append(ax, ay), B.append(bx, by))
        }
    }

  implicit def Triple[A, B, C](implicit A: Monoid[A], B: Monoid[B], C: Monoid[C]): Monoid[(A, B, C)] =
    new Monoid[(A, B, C)] {
      val empty: (A, B, C) = (A.empty, B.empty, C.empty)
      def append(x: (A, B, C), y: (A, B, C)): (A, B, C) =
        (x, y) match {
          case ((ax, bx, cx), (ay, by, cy)) => (A.append(ax, ay), B.append(bx, by), C.append(cx, cy))
        }
    }

  implicit def Option[A](implicit A: Semigroup[A]): Monoid[Option[A]] =
    new Monoid[Option[A]] {
      val empty: Option[A] = None
      def append(x: Option[A], y: Option[A]): Option[A] =
        (x, y) match {
          case (None, None) => None
          case (_, None) => x
          case (None, _) => y
          case (Some(a), Some(b)) => Some(A.append(a, b))
        }
    }

  implicit def Map[K, V](implicit V: Semigroup[V]): Monoid[Map[K, V]] =
    new Monoid[Map[K, V]] {
      val empty: Map[K, V] = scala.collection.immutable.Map.empty
      def append(x: Map[K, V], y: Map[K, V]): Map[K, V] =
        x.foldLeft(y) {
          case (a, (k, v)) => a.updated(k, a.get(k).fold(v)(V.append(v, _)))
        }
    }

  implicit def Min[A](implicit A: Ordering[A]): Semigroup[A] =
    new Semigroup[A] {
      def append(x: A, y: A): A = A.min(x, y)
    }

  implicit def Max[A](implicit A: Ordering[A]): Semigroup[A] =
    new Semigroup[A] {
      def append(x: A, y: A): A = A.max(x, y)
    }

  implicit def Sum[A](implicit A: Numeric[A]): Monoid[A] =
    new Monoid[A] {
      val empty: A = A.zero
      def append(x: A, y: A): A = A.plus(x, y)
    }

  implicit def Prod[A](implicit A: Numeric[A]): Monoid[A] =
    new Monoid[A] {
      val empty: A = A.one
      def append(x: A, y: A): A = A.times(x, y)
    }

  implicit def Ordering[A]: Monoid[Ordering[A]] =
    new Monoid[Ordering[A]] {
      val empty: Ordering[A] =
        new Ordering[A] {
          def compare(a: A, b: A): Int = 0
        }
      def append(x: Ordering[A], y: Ordering[A]): Ordering[A] =
        new Ordering[A] {
          def compare(a: A, b: A): Int =
            x.compare(a, b) match {
              case 0 => y.compare(a, b)
              case n => n
            }
        }
    }

  implicit def MonadPlus[F[_], A](implicit F: MonadPlus[F]): Monoid[F[A]] =
    new Monoid[F[A]] {
      val empty: F[A] = F.zero
      def append(x: F[A], y: F[A]): F[A] = F.plus(x, y)
    }

}
