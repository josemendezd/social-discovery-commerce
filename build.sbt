name := "Boozology"

version := "1.2-SNAPSHOT"

libraryDependencies ++= Seq(
  javaCore,
      javaJdbc,
      javaEbean,
		 "be.objectify" %% "deadbolt-java" % "2.2-RC4",
		"be.objectify" %% "deadbolt-scala" % "2.2-RC2",
		// Comment this for local development of the Play Authentication core
		"com.feth"      %%  "play-authenticate" % "0.5.0-SNAPSHOT",
		"postgresql" % "postgresql" % "9.1-901-1.jdbc4",
		"com.amazonaws" % "aws-java-sdk" % "1.3.11",
		"com.maxmind.geoip2" % "geoip2" % "0.4.1",
		"com.stripe" % "stripe-java" % "1.5.1",
		 "org.avaje.ebeanorm" % "avaje-ebeanorm-api" % "3.1.1"
)     


resolvers += Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns)

resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns)

coffeescriptOptions := Seq("bare")

javascriptEntryPoints <<= (sourceDirectory in Compile)(base =>
   ((base / "assets" ** "*.js") --- (base / "assets" ** "_*")).get
)
	 
		
play.Project.playJavaSettings
