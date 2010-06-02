import sbt._
import sbt.CompileOrder._

abstract class UlyssesDefaults(info: ProjectInfo) extends DefaultProject(info) {
  val scalaToolsSnapshots = "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/"

  def specsDependency = "org.scala-tools.testing" % "specs_2.8.0.RC2" % "1.6.5-SNAPSHOT" % "test" withSources
  def scalacheckDependency = "org.scala-tools.testing" % "scalacheck_2.8.0.RC2" % "1.8-SNAPSHOT" % "test" withSources
  override def includeTest(s: String) = { s.endsWith("Check") || s.endsWith("Spec") }

}
class UlyssesProject(info: ProjectInfo) extends ParentProject(info) {
  // Sub-projects

  lazy val core = project("ulysses-core", "ulysses-core", new Core(_))
  //  lazy val stm = project("ulysses-stm", "ulysses-stm", new Stm(_), core)
//  lazy val allModules = Seq(core)

  class Core(info: ProjectInfo) extends UlyssesDefaults(info) {
    val specs = specsDependency
    val scalacheck = scalacheckDependency
  }

}
