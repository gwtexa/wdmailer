name := "wdmailer"

version := "1.0"

scalaVersion := "2.9.2"

libraryDependencies += "javax.mail" % "mail" % "1.4.5"

libraryDependencies += "org.scalatest" % "scalatest_2.9.2" % "1.9.1" % "test"


mainClass in (Compile, run) := Some("wd.mailer.imap.ImapClient")
