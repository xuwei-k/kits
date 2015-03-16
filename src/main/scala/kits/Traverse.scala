package kits

trait Traverse[F[_]] extends Functor[F] {
  def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]
  def map[A, B](fa: F[A])(f: A => B): F[B] = traverse[Id, A, B](fa)(f)
}

object Traverse {
  def traverse[F[_], G[_], A, B](fa: F[A])(f: A => G[B])(implicit F: Traverse[F], G: Applicative[G]): G[F[B]] = F.traverse(fa)(f)
  def sequence[F[_]: Traverse, G[_]: Applicative, A](fga: F[G[A]]): G[F[A]] = traverse(fga)(identity)
  def foldMap[F[_]: Traverse, A, B: Monoid](fa: F[A])(f: A => B): B = traverse[F, ({ type G[A] = Const[B, A] })#G, A, B](fa)(f)
  def fold[F[_]: Traverse, A: Monoid](fa: F[A]): A = foldMap(fa)(identity)
}
