package pro.nkozlov.telegram.bots.lanitbus

import com.typesafe.config.ConfigFactory

trait ConfigContext {
  private val conf = ConfigFactory.load()
  val token: String = conf.getString("lanit-bus-bot.telegram.bot.api.token")
  val scheduleFolder: String = conf.getString("lanit-bus-bot.schedule.folder")
  val srcExcelFile: String = conf.getString("lanit-bus-bot.source.excel")
}

object ConfigContextImpl extends ConfigContext
