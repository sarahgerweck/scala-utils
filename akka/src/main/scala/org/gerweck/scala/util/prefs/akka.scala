package org.gerweck.scala.util
package prefs

import _root_.akka.util.ByteString

package object akka {
  implicit val byteStringPrefHandler: PrefHandler[ByteString] = {
    implicit val byteArray2ByteStringMorphism =
      mapping.Homomorphism[Array[Byte], ByteString](ByteString(_), _.toArray[Byte])
    PrefHandler.mappedPrefHandler[Array[Byte], ByteString]
  }
}
