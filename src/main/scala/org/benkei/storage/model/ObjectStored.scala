package org.benkei.storage.model

import io.estatico.newtype.macros.newtype

object ObjectStored {
  @newtype case class Name(value: String)
  @newtype case class Version(value: Long)

  case class Id(
    bucketName: Bucket.Name,
    name:       ObjectStored.Name,
    version:    Option[ObjectStored.Version]
  )
}
