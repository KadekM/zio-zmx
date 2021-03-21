package zio

package object zmx {
  def spawn(i: Int): UIO[Unit] =
    if (i <= 0) {
      ZIO.unit
    } else
      for {
        rec <- spawn(i - 1).fork
        f   <- rec.join
      } yield f

  def deep(size: Int) =
    for {
      _ <- spawn(size)
    } yield ()

  def broad(size: Int) =
    for {
      _ <- ZIO.foreachPar_(1 to size)(_ => ZIO.never.fork)
    } yield ()

  def mixed(size: Int) =
    for {
      _ <- ZIO.foreachPar_(1 to (size / 100))(_ => spawn(size / 100))
    } yield ()

  val ZMXSupervisor = zio.zmx.diagnostics.ZMXSupervisor
  val ZMXRuntime    = zio.Runtime.default.mapPlatform(_.withSupervisor(ZMXSupervisor))
}
