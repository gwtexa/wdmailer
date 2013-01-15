wdmailer
========

IMAP IDLE monitoring library

This module is javamail specific. It handles i/o operations with IMAP server.

Depends on common emsg module 
Contains converters to emsg objects 

In order to create eclipse project:
install sbteclipse plugin: in ~/.sbt/plugins/plugins.sbt add:
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.1")

sbt reload
sbt eclipse
