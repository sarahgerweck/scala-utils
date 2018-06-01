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
- Update Scala to 2.12.4
- Add support for `java.nio.file.Path` preferences

### 2.7.0

- Update the `Prefs` API to allow for optional preferences
- Make Commons-IO and Commons-VFS optional dependencies
  - These are only used by the deprecated `FileUtils` system
- Make JCL-over-SLF4J an optional dependency
  - This should have always been optional.
  - I *strongly* recommend against using commons-logging, but there's no
    reason this library should make that decision for you.

#### 2.7.1

- Update Twitter Util to 17.12
- Update Akka to 2.5.8
- Update Akka HTTP to 10.0.11

#### 2.7.2

- Update SBT to 1.1
- Update BouncyCastle to 1.59

## 3.0

- Remove all deprecated interfaces
- Update Scala Parser Combinators to 1.1.0
- Update Akka to 2.5.9

#### 3.0.1

- Fix a bug that affects the `equals` of `VersionNumber` and add tests
- Dependency updates. (These are all binary compatible.)
  - Update Akka to 2.5.11
  - Update Scala XML to 1.1.0
  - Update Liquibase to 3.5.5
  - Update Log4s to 1.6.0
  - Update SBT to 1.1.1
  - Update Slick to 3.2.2
  - Update Akka HTTP to 10.1.0
  - Update Twitter Util to 18.3.0
- Added `unwrapOption` extension to `java.sql.Wrapper` classes
  - This is a more convenient way to do unwrapping in Scala
  - Import `org.gerweck.scala.util.jdbc._` to make it available
- Remove some dead code from Scala 2.10 & simplify the build

#### 3.0.2

- Build Scala 2.11 version with Scala 2.11.12
- Build Scala 2.12 version with Scala 2.12.5
  - Scala 2.12.5 includes some substantial performance improvements for string
    interpolation, so this may improve performance in some situations.

### 3.1

- Add SHAKE-128 and SHAKE-256 hash methods
- New `RichByteArray` extension methods
  - `toHexString`
  - `toBasicBase64`
  - `toUriBase64`

### 3.2

- Update Slick to 3.2.3
  - This is binary compatible with Slick 3.2.1 and 3.2.2.
- Update Akka HTTP to 10.1.1
- Update Liquibase to 3.6.0
- Non-functional changes
  - Update JVM options to build
  - Update SBT to 1.1.2
- Add experimental `ObjectTree` pretty printer
  - As documented, this is not suitable for use beyond debugging.

## 4.0

- Drop support for Java 6
- Add `skipNones` support in `ObjectTree`. It remains experimental
- Dependency updates (binary compatible)
  - Update Akka to 2.5.12
  - Update Twitter Util to 18.4.0
  - Update Liquibase to 3.6.1

#### 4.0.1

- Improvements to `ObjectTree`
  - Fix an inefficiency in sequence traversal
  - Fix a bug when `skipNones` was `false`
  - Add a maximum recursion depth
  - Add a length limit for single-line 1-products

### 4.1

- Update Scala to 2.12.6
- Update SBT to 1.1.6
- Tweak `ObjectTree` placeholder string for too-deep recursion
- Dependency updates (binary compatible)
  - Updated Json4s to 3.5.4
