package org.benkei.storage

import java.io.ByteArrayOutputStream

import cats.effect.{Blocker, Concurrent, ContextShift, Resource}
import cats.implicits._
import cats.mtl.FunctorRaise
import cats.{Inject, MonadError}
import com.google.cloud.storage.{Blob, BlobId, BlobInfo, Storage}
import org.benkei.storage.model.StorageError.{DeserializeError, ObjectNotFoundError}
import org.benkei.storage.model.{Bucket, ObjectStored, StorageError}

trait BucketService[F[_]] {
  def put[E: Inject[*, Array[Byte]]](blobId: ObjectStored.Id, content: E): F[ObjectStored.Id]
  def get[E: Inject[*, Array[Byte]]](id:     ObjectStored.Id): F[E]
}

object BucketService {

  def apply[F[_]: Concurrent: ContextShift](blocker: Blocker, client: Storage)(implicit
    FR: FunctorRaise[F, StorageError],
    ME: MonadError[F, Throwable]
  ): BucketService[F] =
    new BucketService[F] {

      override def put[A: Inject[*, Array[Byte]]](
        id:      ObjectStored.Id,
        content: A
      ): F[ObjectStored.Id] = {
        blocker.delay {
          val serialise: A => Array[Byte] = Inject[A, Array[Byte]].inj
          val created = client.create(blobInfo(id), serialise(content))
          fromBlobId(created.getBlobId)
        }
      }

      override def get[A: Inject[*, Array[Byte]]](id: ObjectStored.Id): F[A] = {
        val deserialize: Array[Byte] => F[A] = b =>
          F.delay(Inject[A, Array[Byte]].prj(b)).flatMap {
            case Some(value) => F.pure(value)
            case None        => FR.raise[A](DeserializeError(id))
          }

        blocker.blockOn {
          Option(client.get(blobId(id))).filter(_.exists()) match {
            case Some(blob) => download(blob).flatMap(deserialize)
            case None       => FR.raise[A](ObjectNotFoundError(id))
          }
        }
      }

      def download[E: Inject[*, Array[Byte]]](blob: Blob): F[Array[Byte]] = {
        Resource
          .fromAutoCloseable(F.delay(new ByteArrayOutputStream()))
          .evalTap(os => F.delay(blob.downloadTo(os)))
          .use(os => F.delay(os.toByteArray))
      }
    }

  def fromBlobId(gid: BlobId): ObjectStored.Id = {
    ObjectStored.Id(
      Bucket.Name(gid.getBucket),
      ObjectStored.Name(gid.getName),
      Some(ObjectStored.Version(gid.getGeneration))
    )
  }

  def blobId(id: ObjectStored.Id): BlobId = {
    id match {
      case ObjectStored.Id(bucketName, name, Some(gen)) =>
        BlobId.of(bucketName.value, name.value, gen.value)
      case ObjectStored.Id(bucketName, name, None) =>
        BlobId.of(bucketName.value, name.value)
    }
  }

  def blobInfo(id: ObjectStored.Id): BlobInfo = {
    BlobInfo.newBuilder(blobId(id)).build
  }
}
