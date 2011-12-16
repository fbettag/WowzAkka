name := "wowzakka"

version := "1.0"

scalaVersion := "2.9.1"
 
resolvers ++= Seq(
  "Repo Maven" at "http://repo1.maven.org/maven2/",
  "Scala Tools Snapshot" at "http://scala-tools.org/repo-releases/",
  "Scala Tools Snapshot" at "http://scala-tools.org/repo-snapshots/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/ivy-releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Bryan J Swift Repository" at "http://repos.bryanjswift.com/maven2/"
)

// if you have issues pulling dependencies from the scala-tools repositories (checksums don't match), you can disable checksums
//checksums := Nil

libraryDependencies ++= Seq(
  "org.scala-tools.testing" % "specs_2.9.0" % "1.6.8" % "test", // For specs.org tests
  "junit" % "junit" % "4.8" % "test->default", // For JUnit 4 testing
  "com.novocode" % "junit-interface" % "0.4.0" % "test"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor" % "2.0-M1"
)
