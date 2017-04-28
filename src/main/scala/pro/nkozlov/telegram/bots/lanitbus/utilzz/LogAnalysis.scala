package pro.nkozlov.telegram.bots.lanitbus.utilzz

import java.io.File

/**
  * todo Document type LogAnalysis
  */
object LogAnalysis {

  def main(args: Array[String]): Unit = {

    LogAnalysis.printUniqueCountUsers()

  }

  def printUniqueCountUsers(): Unit = {
    val source = scala.io.Source.fromFile(new File("/Users/NKozlov/Downloads/scala-logging.log"))
    val regex = """.* Message\(\d+,Some\(User\((\d+),[a-zA-Z]+,(.*)""".r

    def accumulate(i: BufferedIterator[String], acc: Set[String]): Set[String] = {

      var mySet: Set[String] = acc
      while (i.hasNext) {
        val line = i.next()
        if (line.matches(regex.toString())) {
          regex.findAllIn(line).matchData foreach {
            m =>
              mySet = mySet + m.group(1)
          }
        }
      }
      mySet
    }

    print(accumulate(source.getLines().buffered, Set()).size)
  }

}
