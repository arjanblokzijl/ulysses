import java.net.URL
import sbt._
import sbt.CompileOrder._
import java.util.jar.Attributes.Name._
import java.io.File
import scala.Array

abstract class UlyssesDefaults(info: ProjectInfo) extends DefaultProject(info) with OverridableVersion
        with AutoCompilerPlugins {
  // val scalaTools2_8_0Snapshots = Resolver.url("2.8.0 snapshots") artifacts "http://scala-tools.org/repo-snapshots/org/scala-lang/[module]/2.8.0-SNAPSHOT/[artifact]-[revision].[ext]"
  val scalaToolsSnapshots = "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

  private val encodingUtf8 = List("-encoding", "UTF-8")

  override def compileOptions =
    encodingUtf8.map(CompileOption(_)) :::
             CompileOption("-no-specialization") ::
            target(Target.Java1_5) :: Unchecked :: super.compileOptions.toList

  override def packageOptions = ManifestAttributes((IMPLEMENTATION_TITLE, "Ulysses"), (IMPLEMENTATION_URL, "http://github.com/ulysses"), (IMPLEMENTATION_VENDOR, "The Ulysses Project"), (SEALED, "true")) :: Nil

  override def documentOptions = encodingUtf8.map(SimpleDocOption(_))

  override def managedStyle = ManagedStyle.Maven

  override def packageDocsJar = defaultJarPath("-javadoc.jar")

  override def packageSrcJar = defaultJarPath("-sources.jar")

  override def packageTestSrcJar = defaultJarPath("-test-sources.jar")

  lazy val sourceArtifact = Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)

  lazy val docsArtifact = Artifact(artifactID, "docs", "jar", Some("javadoc"), Nil, None)

  val specsDependency = "org.scala-tools.testing" %% "specs" % "1.6.5-SNAPSHOT" % "test" withSources

  val scalacheckDependency = "org.scala-tools.testing" % "scalacheck_2.8.0.RC2" % "1.8-SNAPSHOT" % "test" withSources

  val configgy = "Configgy" at "http://www.lag.net/repo"
  
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageDocs, packageSrc, packageTestSrc)

}

/**
 * Replaces 'SNAPSHOT' in the project version with the contents of the system property 'build.timestamp',
 * if provided.
 */
trait OverridableVersion extends Project {
  lazy val buildTimestamp = system[String]("build.timestamp")

  override def version = {
    val realVersion = super.version
    val v = realVersion.toString
    val SnapshotVersion = """(.+)-SNAPSHOT""".r
    (buildTimestamp.get, realVersion.toString) match {
      case (Some(timestamp), SnapshotVersion(base)) => OpaqueVersion(base + "-" + timestamp)
      case _ => realVersion
    }
  }
}

final class UlyssesProject(info: ProjectInfo) extends ParentProject(info) with OverridableVersion {
  // Sub-projects
  lazy val core = project("core", "ulysses-core", new Core(_))
//  lazy val stm = project("stm", "ulysses-stm", new Stm(_), core)
  lazy val full = project("full", "ulysses-full", new Full(_), core)
  lazy val allModules = Seq(core)

  val publishTo = "Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/snapshots/"
  Credentials(Path.userHome / ".ivy2" / ".credentials", log)

  private def noAction = task {None}

  lazy val retrieveAdditionalSources = task {
      import FileUtilities._
      val scalaToolsSnapshots = "http://scala-tools.org/repo-snapshots"
      val explicitScalaVersion = buildScalaVersion.replaceAll("""\+""", "353")
      val source = new URL(scalaToolsSnapshots + "/org/scala-lang/scala-library/2.8.0-SNAPSHOT/scala-library-" + explicitScalaVersion + "-sources.jar")
      val dest = (UlyssesProject.this.info.bootPath / ("scala-" + buildScalaVersion) /  "lib" / "scala-library-sources.jar" asFile)
      download(source, dest, log)
      log.info("downloaded: %s to %s".format(source.toExternalForm, dest))
      None
    } describedAs ("download sources for scala library.")

  // This is built from scalacheck trunk, 20100413. Replace with a managed dependency
  // once one is published next time.
  def scalacheckJar = "lib" / "scalacheck_2.8.0.RC1.jar"

  val parentPath = path _

  class Core(info: ProjectInfo) extends UlyssesDefaults(info)

//  class Stm(info: ProjectInfo) extends UlyssesDefaults(info) {
//    val junit = "junit" % "junit" % "4.7" % "test"
//
//    val dataBinder = "DataBinder" at "http://databinder.net/repo"
//    val akkaRepo = "Akka Maven Repository" at "http://scalablesolutions.se/akka/repository"
//    /* akka dependencies */
//    val akkaCore = "se.scalablesolutions.akka" % "akka-core_2.8.0.Beta1"    % "0.8.1" % "compile"
//    val akkaKernel = "se.scalablesolutions.akka" % "akka-kernel_2.8.0.Beta1"  % "0.8.1" % "compile"
//    val akkaUtil = "se.scalablesolutions.akka" % "akka-util_2.8.0.Beta1"  % "0.8.1" % "compile"
//  }

  class ScalacheckBinding(info: ProjectInfo) extends UlyssesDefaults(info) {
    override def compileClasspath = super.compileClasspath +++ scalacheckJar
  }

  class Full(info: ProjectInfo) extends UlyssesDefaults(info) {
    override def compileClasspath = super.compileClasspath +++ scalacheckJar

    def packageFullAction = packageFull dependsOn(fullDoc)

    def packageFull = {
      val allJars = Path.lazyPathFinder(Seq(core).map(_.outputPath)).## ** GlobFilter("*jar")
      val p = parentPath
      val extra = p("README") +++ p("etc").## ** GlobFilter("*")
      val sourceFiles = allJars +++ extra +++ (((outputPath ##) / "doc") ** GlobFilter("*"))
      zipTask(sourceFiles, outputPath / ("ulysses-full_" + buildScalaVersion + "-" + version.toString + ".zip") )
    } describedAs("Zip all artifacts")

    private def noAction = task {None}

    override def publishLocalAction = noAction dependsOn packageFullAction

    override def publishAction = noAction dependsOn packageFullAction

    def deepSources = Path.finder { topologicalSort.flatMap { case p: ScalaPaths => p.mainSources.getFiles } }
  	lazy val fullDoc = scaladocTask("scalaz", deepSources, docPath, docClasspath, documentOptions)


  }
}
