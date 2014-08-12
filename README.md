# Sarah’s Scala Utilities #

This project simply contains a bunch of miscellaneous Scala utility code.  It
contains components that are relatively independent and that are likely to be
reused without modification across a wide variety of projects.

Some code may be included that is only useful in specialized situations if has
minimal dependencies, is likely to be well contained, and has the generality
of utility code.

No guarantees are offered, but feel free to use this if it is helpful to you.
If you find any bugs, please send me a message or a pull request.  Like many
utility libraries, this code is *not* meant to be abuse proof.  If you try to
do something that seems like it might be a bad idea, you're on your own. :-)


## Installation ##

To use Log4s, add the following to your SBT build:

    libraryDependencies += "org.gerweck.scala" %% "gerweck-utils" % "0.9.3"
