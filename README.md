# Sarah’s Scala Utilities #

This project contains miscellaneous Scala utility code, mostly for my own
projects.  It contains components that are relatively independent and that are
likely to be reused without modification across a wide variety of projects.

Some code may be included that is only useful in specialized situations if has
minimal dependencies, is likely to be well contained, and has the generality
of utility code.

No guarantees are offered, but feel free to use this if it is helpful to you.
If you find any bugs, please send me a message or a pull request.  Like many
utility libraries, this code is *not* meant to be abuse proof.  If you try to
do something that seems like it might be a bad idea, you're on your own. :-)

Because of dependencies, this requires Scala 2.11 or higher. Java 8 is
required for version 4.0 or higher.

## Installation ##

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils" % "5.0.1"


### Akka Utilities ###

For utilities related to Akka and Akka Streaming, use this include. The Akka
utilities require Java 8 and Akka 2.4.

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils-akka" % "5.0.1"

### DB Utilities ###

Some DB lightweight utilities, based on pure JDBC, are included in the core
module. However, advanced utilties or those requiring libraries are in a
separate module. These modules require Java 8 and can be included as such:

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils-db" % "5.0.1"

## Contributors

- [Sarah Gerweck](https://github.com/sarahgerweck/) (primary author)
- [Rouzbeh Safaie](https://github.com/rsafaie/)
