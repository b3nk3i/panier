import Dependencies.{CompilerPlugins, Libraries}

name := "panier"

version := "0.1"

scalaVersion := "2.13.2"

lazy val root = (project in file("."))
  .settings(
    scalafmtOnCompile := true,
    autoAPIMappings := true,
    scalacOptions ++= Seq("-Ymacro-annotations"),
    libraryDependencies ++= List(
      CompilerPlugins.betterMonadicFor,
      CompilerPlugins.contextApplied,
      CompilerPlugins.kindProjector,
      Libraries.cats,
      Libraries.catsEffect,
      Libraries.newtype,
      Libraries.googleStorage
    )
  )