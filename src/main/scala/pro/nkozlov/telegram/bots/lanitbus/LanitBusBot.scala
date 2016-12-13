package pro.nkozlov.telegram.bots.lanitbus

import info.mukel.telegrambot4s._
import api._
import methods._
import models._
import Implicits._
import com.typesafe.scalalogging.LazyLogging

object LanitBusBot extends TelegramBot with Polling with Commands {

  import BusSchedule._

  def token = "257669222:AAEhSlFWkxv651Cy9IXIsLjbNsfWQI_MttE"

  on("/rizhskaya_office", "Рижская-Офис (маршрутки).") { implicit msg =>
    args =>
      reply(
        "_Маршрутки от м. Рижская до офиса (Мурманский)_\n" + scheduleFromRizhskaya().map(list => list(1)).mkString(", ") + "",
        ParseMode.Markdown
      )
  }

  on("/marina_office", "Марьина Роща-Офис (маршрутки).") { implicit msg =>
    args =>
      reply(
        "_Маршрутки от м. Марьина Роща до офиса (Мурманский)_\n" + scheduleFromMarinaRoszha().map(list => list(1)).mkString(", "),
        ParseMode.Markdown
      )
  }

  on("/office_rizhskaya", "Офис-Рижская (маршрутки).") { implicit msg =>
    args =>
      reply(
        "_Маршрутки от офиса (Мурманский) до м. Рижская_\n" + scheduleOfficeToRizhskaya().map(list => list(1)).mkString(", "),
        ParseMode.Markdown
      )
  }

  on("/office_marina", "Офис-Марьина Р (маршрутки).") { implicit msg =>
    args =>
      logger.debug("execute command '/офис-марьина' from {}", msg)
      val date = msg.date
      val chatId = msg.sender
      reply(
        "_Маршрутки от офиса (Мурманский) до м. Марьина Роща_\n" + scheduleOfficeToMarinaRoszha().map(list => list(1)).mkString(", "),
        ParseMode.Markdown
      )
  }

  on("/to_office", "Ближайший транспорт до офиса.") { implicit msg =>
    args =>
      reply(
        "_Ближайший транспорт (по времени) от метро до офиса (маршрутки и автобусы)_\n" +
          ""
      )
  }

  on("/to_metro", "Ближайший транспорт до метро.") { implicit msg =>
    args =>
      val sch = scheduleToMetro(new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(msg.date.toLong * 1000)))

      def toRizhskayaM =
        if (sch.head.exists(list => list.head contains "М"))
          sch.head.filter(list => list.head contains "М").take(2).map(list => list(1)).mkString(", ")
        else "Маршрутки уже спят :)"

      def toRizhskayaA =
        if (sch.head.exists(list => list.head contains "А"))
          sch.head.filter(list => list.head contains "А").take(2).map(list => list(1)).mkString(", ")
        else "Автобусы уже спят :)"

      def toMarinaRoszhaM =
        if (sch(1).exists(list => list.head contains "М"))
          sch(1).filter(list => list.head contains "М").take(2).map(list => list(1)).mkString(", ")
        else "Маршрутки уже спят :)"

      def toMarinaRoszhaA =
        if (sch(1).exists(list => list.head contains "А"))
          sch(1).filter(list => list.head contains "А").take(2).map(list => list(1)).mkString(", ")
        else "Автобусы уже спят :)"

      reply(
        "_Ближайший транспорт (по времени) от офиса до метро (маршрутки и автобусы)_\n" +
          "*До м. Рижская*\n" +
          "\uD83D\uDE90(М): " + toRizhskayaM + "\n" +
          "\uD83D\uDE8C(А): " + toRizhskayaA + "\n\n" +

          "*До м. Марьина Роща*\n" +
          "\uD83D\uDE90(М): " + toMarinaRoszhaM + "\n" +
          "\uD83D\uDE8C(А): " + toMarinaRoszhaA + "\n\n",
        ParseMode.Markdown

      )
  }

}





