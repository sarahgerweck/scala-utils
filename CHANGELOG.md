# Gerweck Utils Changelog

### 2.0

  - Move core utilities into a `core` subfolder
    - Does not affect users of the library
  - Add Akka submodule for actor & stream utils
  - Drop support for Scala 2.10
  - Default package now requires Java 8
    - For Java 6, use the `-java6` suffix
    - The old `-java8` suffix will no longer exist

### 2.1

  - Add `prefs` and `mapping` packages
    - `Prefs` makes `java.util.prefs` more Scala friendly
    - `Mapping` is experimental, contains type morphisms

### 2.2

  - Add `gerweck-util-db` submodule with DB utilities

#### 2.2.2

  - Update to Akka 2.4.8
  - Disable `-optimize` for Akka
     - Optimization with Akka tends to pin you to the exact version of Akka,
       which is not something we want for utilities.

#### 2.2.4

  - Update to Akka 2.4.11

#### 2.2.5

  - Make `VersionNumber` serializable

#### 2.2.6

  - Update the Json4s optional dependency to 3.5.0.
  - Update the Twitter Core optional dependency to 6.38.0.
