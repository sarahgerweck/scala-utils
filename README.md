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

Because of dependencies, this requires Scala 2.11 or higher.


### When we will get builds for Scala 2.12? ###

I do my best to put out my libraries within one or two business days of any
milestone or release candidate, provided any libraries that I require are
available. Most of this project's dependencies are widely available, except
that the maintainers of [Spire](https://github.com/non/spire) have not kept
up with the community standards in terms of publishing libraries for release
candidates and producing a build shortly after the final version is released.
A number of us have reached out to the Spire project to ask that they follow
the community best practices, but with no real luck so far. (For the record,
Spire _is_ a fantastic library: it just needs to have a bit more attention
paid to its infrastructure, tooling and publication.)


## Installation ##

For Java 8, use this line as your dependency:

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils" % "2.4.0"

For Java 6 or Java 7, use this line as your dependency:

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils-java6" % "2.4.0"


### Twitter Utilities ###

Since 1.7, the Twitter utilities are included through a separate jar. If you
need these utilities, include this as well.

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils-twitter" % "2.4.0"


### Akka Utilities ###

For utilities related to Akka and Akka Streaming, use this include. The Akka
utilities require Java 8 and Akka 2.4.

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils-akka" % "2.4.0"


### DB Utilities ###

Some DB lightweight utilities, based on pure JDBC, are included in the core
module. However, advanced utilties or those requiring libraries are in a
separate module. These modules require Java 8 and can be included as such:

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils-db" % "2.4.0"
