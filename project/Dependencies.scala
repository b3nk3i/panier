import sbt._

object Dependencies {

  object Versions {
    val googleCloud = "1.108.0"

    val cats = "2.1.1"
    val catsEffect = "2.1.3"
    val newtype = "0.4.4"

    val betterMonadicFor = "0.3.1"
    val contextApplied = "0.1.4"
    val kindProjector = "0.11.0"
    val macroParadise = "2.1.1"
  }

  object Libraries {
    val cats          = "org.typelevel" %% "cats-core" % Versions.cats
    val catsEffect    = "org.typelevel" %% "cats-effect" % Versions.catsEffect
    val newtype       = "io.estatico" %% "newtype" % Versions.newtype
    val googleStorage = "com.google.cloud" % "google-cloud-storage" % Versions.googleCloud
  }

  object CompilerPlugins {
    val betterMonadicFor = compilerPlugin(
      "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
    )
    val contextApplied = compilerPlugin(
      "org.augustjune" %% "context-applied" % Versions.contextApplied
    )
    val kindProjector = compilerPlugin(
      "org.typelevel" %% "kind-projector" % Versions.kindProjector cross CrossVersion.full
    )
    val macroParadise = compilerPlugin(
      "org.scalamacros" % "paradise" % Versions.macroParadise cross CrossVersion.full
    )
  }

}
