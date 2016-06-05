# Gerweck Utils Changelog

### 2.0

  - Move core utilities into a `core` subfolder
    - Does not affect users of the library
  - Add Akka submodule for actor & stream utils
  - Drop support for Scala 2.10
  - Default package now requires Java 8
    - For Java 6, use the `-java6` suffix
    - The old `-java8` suffix will no longer exist

