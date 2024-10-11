package dev.alterationx10.app.unduplicator

import scala.util.CommandLineParser
import java.security.DigestInputStream
import java.security.MessageDigest

object Unduplicator {

  case class FileInfo(
      md5: String,
      path: os.Path
  )

  extension (path: os.Path) {
    def md5 = {
      val _md5: MessageDigest = MessageDigest.getInstance("MD5")
      val dis = new DigestInputStream(os.read.inputStream(path), _md5)
      dis.readAllBytes()
      dis.close()
      _md5.digest.map("%02x".format(_)).mkString
    }
  }

  def findDuplicates(dir: os.Path, shouldDelete: Boolean): Unit = {
    val thisDir =
      os.list(dir)

    val thisDirDuplicates =
      thisDir
        .filter(os.isFile)
        .map(p => FileInfo(p.md5, p))
        .groupBy(_.md5)
        .filter(_._2.length > 1)
        .map({ case (k, v) => k -> v.sortBy(_.path.baseName.length()) })

    thisDirDuplicates.foreach { dup =>
      println(s"❗️ Found duplicates:")
      dup._2
        .foreach(d => println(s"\t${d.md5} ${d.path}"))
      if shouldDelete then {
        println(s"\t🧨 Deleting duplicates")
        dup._2.drop(1).foreach { fi =>
          os.remove(fi.path)
          println(s"\t💥 Deleted ${fi.path}")
        }
      }
    }

    thisDir
      .filter(os.isDir)
      .foreach(p => findDuplicates(p, shouldDelete = shouldDelete))

  }

  @main
  def main(args: String*): Unit = {

    val workingDir = os.pwd / os.RelPath(
      args.filterNot(_.startsWith("-")).headOption.getOrElse(".")
    )

    if (!os.isDir(workingDir)) then
      println(s"$workingDir is not a directory!")
      System.exit(1)

    println(s"🔎 Looking for duplicates in $workingDir")

    val shouldDelete = args.exists(_.contains("-delete"))

    if shouldDelete then {
      println(
        s"You have passed an option to automatically delete files! Continue? [y/N]"
      )
      val shouldContinue = Option(scala.io.StdIn.readLine())
        .exists(_.toLowerCase().startsWith("y"))
      if !shouldContinue then System.exit(0)
    }

    findDuplicates(workingDir, shouldDelete)

    println(s"👋 All done!")

  }

}
