package by.jylilov.homemonitor.codec

import cats.effect.kernel.Async
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

trait JsonEntityCodec {
  implicit def encoder[F[_], A: Encoder]: EntityEncoder[F, A] =
    jsonEncoderOf[F, A]
  implicit def decoder[F[_] : Async, T: Decoder]: EntityDecoder[F, T] = jsonOf[F, T]
}

object JsonEntityCodec extends JsonEntityCodec
