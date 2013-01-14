import sbt._
import Keys._

object WdmailerBuild extends Build {
    lazy val root = Project(id = "wdmailer",
                            base = file(".")) dependsOn(wdemsg)

    lazy val wdemsg = RootProject(file("../wdemsg"))

    //lazy val sbthello = RootProject(file("../sbthello"))

}


