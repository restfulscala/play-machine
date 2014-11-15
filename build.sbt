import com.typesafe.sbt.SbtGit._
import play.PlayScala

versionWithGit

// Optionally:
git.baseVersion := "0.1.0"

name := "play-machine"

libraryDependencies ++= Seq(
  "org.restfulscala" %% "play-siren" % "0.2.0",
  "com.chuusai" %% "shapeless" % "2.0.0",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test"
)

val root = project in file(".") enablePlugins PlayScala

scalaVersion := "2.11.4"

licenses := Seq("MIT" -> url("http://opensource.org/licenses/MIT"))

seq(bintrayResolverSettings:_*)

resolvers += bintray.Opts.resolver.mavenRepo("restfulscala")
