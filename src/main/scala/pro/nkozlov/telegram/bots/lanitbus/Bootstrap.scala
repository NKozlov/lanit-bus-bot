package pro.nkozlov.telegram.bots.lanitbus

import com.typesafe.scalalogging.LazyLogging

/**
  * todo Document type Bootstrap
  */
object Bootstrap extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("Telegram LanitBusBot start, v={}", "2.1")

    LanitBusBot.run()

  }

}
