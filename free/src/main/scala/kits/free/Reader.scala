package kits.free

sealed abstract class Reader[R] {

  type T

  type Member[U <: Union] = kits.free.Member[Reader[R], U]

}

object Reader {

  case class Ask[R]() extends Reader[R] { type T = R }

  def run[U <: Union, R, A](free: Free[Reader[R] :+: U, A], value: R): Free[U, A] =
    Free.handleRelay(free)(a => Right(Pure(a): Free[U, A])) {
      case Ask() => k => Left(k(value))
    }

  def ask[U <: Union, R](implicit F: Member[Reader[R], U]): Free[U, R] = Free(F.inject(Ask()))

  def local[U <: Union: Reader[R]#Member, R, A](free: Free[U, A])(f: R => R): Free[U, A] =
    ask.flatMap { r0 =>
      val r = f(r0)
      Free.interpose(free)(a => Right(Pure(a): Free[U, A]))((_: Reader[R]) match {
        case Ask() => k => Left(k(r))
      })
    }

}
