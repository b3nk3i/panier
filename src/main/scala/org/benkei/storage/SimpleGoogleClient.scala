package org.benkei.storage

import java.net.URI

import cats.effect.{Resource, Sync}
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.{Storage, StorageOptions}

object SimpleGoogleClient {

  case class Config(credentials: URI, projectId: String)

  def create[F[_]: Sync](config: Config): F[Storage] = {
    Resource
      .fromAutoCloseable { F.delay { config.credentials.toURL.openStream() } }
      .use { s =>
        F.delay {
          val credentials = GoogleCredentials.fromStream(s)
          StorageOptions
            .newBuilder()
            .setCredentials(credentials)
            .setProjectId(config.projectId)
            .build()
            .getService;
        }
      }
  }
}
