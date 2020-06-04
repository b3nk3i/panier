package org.benkei.storage.model

import io.estatico.newtype.macros.newtype

object Bucket {
  @newtype case class Name(value: String)
}
