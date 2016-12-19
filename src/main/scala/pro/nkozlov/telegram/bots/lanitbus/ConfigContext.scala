package pro.nkozlov.telegram.bots.lanitbus

import com.typesafe.config.ConfigFactory

/**
  * todo Document type ConfigContext
  */
object ConfigContext {

  private val conf = ConfigFactory.load()

  val token: String = conf.getString("lanit-bus-bot.telegram.bot.api.token")

}
