# Gerweck Utils Changelog

## 2.0

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

### 2.3.0

- Add support for Scala 2.12
  - Slick is only available as a milestone release for 2.12, so you may not
    want to use the 2.12 version for production software if you use the
    Slick support.
- Update to Akka 2.4.14
- Update to Akka HTTP 10.0
  - Akka HTTP is no longer experimental, and it's now on a different release
    schedule from Akka
- Start dropping build support for Scala 2.12 milestones.
- Slight performance improvements in `TokenParserUtil`.

### 2.4.0

- Update to Akka 2.4.17
- Add `ZipStream` to Akka module, which provides a streaming, non-blocking
  mechanism for producing zip archives.
- Add `hashing` package that provides idiomatic access to hash algorithms.
  - This also adds support for the Bouncy Castle hash implementations. This
    is now an optional dependency.

### 2.5.0

- Updates to the `ZipStream` module, *including some breaking changes*.
- Deprecate the `foldl1` and `rx` extension methods. These are both
  available in recent version of the Scala standard library.
- Update to Akka 2.5.3
- Update to Akka HTTP 10.0.9
- Update to Slick 3.2

#### 2.5.1

- Update Scala to 2.12.3
- Add `Throwing` JDBC objects

### 2.6.0

- Update to SBT 1.0
- Minor dependency updates
