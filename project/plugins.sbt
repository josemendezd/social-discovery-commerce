// Comment to get more information during initialization
logLevel := Level.Debug

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

//libraryDependencies += "org.javassist" % "javassist" % "3.18.2-GA"

// Use the Play sbt plugin for Play projects
//addSbtPlugin("com.typesafe.play" % "sbt-plugin" % Option(System.getProperty("play.version")).getOrElse("2.2.3"))
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.3")