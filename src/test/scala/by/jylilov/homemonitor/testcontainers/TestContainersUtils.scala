package by.jylilov.homemonitor.testcontainers

import cats.effect._
import org.testcontainers.containers.GenericContainer

object TestContainersUtils {

  def testContainerResource[F[_] : Sync](containerFun: () => GenericContainer[_]): Resource[F, GenericContainer[_]] =
    Resource.make(
      Sync[F].blocking {
        val container = containerFun()
        container.start()
        container
      }
    ) { container =>
      Sync[F].blocking {
        container.stop()
      }
    }
}
