package pro.nkozlov.telegram.bots.lanitbus

import com.typesafe.scalalogging.LazyLogging

/**
  * todo Document type Bootstrap
  */
object Bootstrap extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("Telegram LanitBusBot start.")

    LanitBusBot.run()

  }

}
