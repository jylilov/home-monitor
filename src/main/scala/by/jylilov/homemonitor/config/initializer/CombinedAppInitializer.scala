package by.jylilov.homemonitor.config.initializer

import by.jylilov.homemonitor.config.AppConfig
import cats.effect._
import cats.implicits._

class CombinedAppInitializer[F[_] : Sync](
  initializers: Seq[AppInitializer[F]]
) extends AppInitializer[F] {
  override def init(config: AppConfig): F[Unit] = initializers.traverse(_.init(config)).as()
}

object CombinedAppInitializer {
  def apply[F[_] : Sync](initializers: AppInitializer[F]*): AppInitializer[F] = new CombinedAppInitializer(initializers)
}
