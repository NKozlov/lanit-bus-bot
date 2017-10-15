package pro.nkozlov.telegram.bots.lanitbus

import com.typesafe.config.ConfigFactory

trait ConfigContext {
  private val conf = ConfigFactory.load()
  val token: String = conf.getString("lanit-bus-bot.telegram.bot.api.token")
  val scheduleFolder: String = conf.getString("lanit-bus-bot.schedule.folder")
  val srcExcelFile: String = conf.getString("lanit-bus-bot.source.excel")
  val buttonOffice: String = conf.getString("lanit-bus-bot.button.office")
  val buttonMetro: String = conf.getString("lanit-bus-bot.button.metro")
}

object ConfigContextImpl extends ConfigContext
