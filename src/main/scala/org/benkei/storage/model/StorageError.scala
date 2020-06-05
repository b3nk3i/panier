package org.benkei.storage.model

sealed trait StorageError

object StorageError {
  case class DeserializeError(objId: ObjectStored.Id) extends StorageError
  case class ObjectNotFoundError(objId: ObjectStored.Id) extends StorageError
}
