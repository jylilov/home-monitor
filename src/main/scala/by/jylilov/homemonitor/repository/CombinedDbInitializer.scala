package by.jylilov.homemonitor.repository

import by.jylilov.homemonitor.config.DbConfig
import cats.effect._
import cats.implicits._

class CombinedDbInitializer[F[_] : Sync](
  initializers: Seq[DbInitializer[F]]
) extends DbInitializer[F] {
  override def init(dbConfig: DbConfig): F[Unit] = initializers.traverse(_.init(dbConfig)).as()
}

object CombinedDbInitializer {
  def apply[F[_] : Sync](initializers: DbInitializer[F]*): DbInitializer[F] = new CombinedDbInitializer(initializers)
}
