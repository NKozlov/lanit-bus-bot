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
      val sch = scheduleToOffice(new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date(msg.date.toLong * 1000)))

      def fromRizhskayaM =
        if (sch.head.exists(list => list.head contains "М"))
          sch.head.filter(list => list.head contains "М").take(2).map(list => list(1)).mkString(", ")
        else "Маршрутки уже спят :)"

      def fromRizhskayaA =
        if (sch.head.exists(list => list.head contains "А"))
          sch.head.filter(list => list.head contains "А").take(2).map(list => list(1)).mkString(", ")
        else "Автобусы уже спят :)"

      def fromMarinaRoszhaM =
        if (sch(1).exists(list => list.head contains "М"))
          sch(1).filter(list => list.head contains "М").take(2).map(list => list(1)).mkString(", ")
        else "Маршрутки уже спят :)"

      reply(
        "_Ближайший транспорт (по времени) до офиса (маршрутки и автобусы)_\n" +
          "*От м. Рижская*\n" +
          "\uD83D\uDE90(М): " + fromRizhskayaM + "\n" +
          "\uD83D\uDE8C(А): " + fromRizhskayaA + "\n\n" +

          "*От м. Марьина Роща*\n" +
          "\uD83D\uDE90(М): " + fromMarinaRoszhaM + "\n",
        ParseMode.Markdown
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

      reply(
        "_Ближайший транспорт (по времени) до метро (маршрутки и автобусы)_\n" +
          "*До м. Рижская*\n" +
          "\uD83D\uDE90(М): " + toRizhskayaM + "\n" +
          "\uD83D\uDE8C(А): " + toRizhskayaA + "\n\n" +

          "*До м. Марьина Роща*\n" +
          "\uD83D\uDE90(М): " + toMarinaRoszhaM + "\n",
        ParseMode.Markdown

      )
  }

  on("/support") { implicit msg =>
    args =>
      val firstName = msg.from.get.firstName
      val lastName = msg.from.get.lastName.get
      val userName = msg.from.get.username.get

      reply(
        s"""
           |Автор - Козлов Никита.
           |
           |Исходный код выложен на [GitHub](https://github.com/NKozlov/lanit-bus-bot).
           |
           |Предложения и замечания по работе бота можно отправить на email: [kozlov.bots@gmail.com](mailto:kozlov.bots@gmail.com) (тема письма "lanit-bus-bot <вопрос/предложение/дефект>")
        """.stripMargin,
        ParseMode.Markdown
      )
  }

  on("/start") { implicit msg =>
    args =>
      val firstName = msg.from.get.firstName
      val lastName = msg.from.get.lastName.get
      val userName = msg.from.get.username.get

      reply(
        s"""
           |Приветствую тебя, $firstName $lastName (@$userName)!
           |Я покажу тебе актуальное расписание маршруток компании ЛАНИТ в любое время, просто попроси ;)
           |
           |Основные команды:
           |```
           |/to_office - Ближайший транспорт до офиса.
           |/to_metro - Ближайший транспорт до метро.
           |```
           |Набери `/help` для знакомства с остальными командами.
           |
           |Автор бота - Козлов Никита (@pravprod).
           |Исходный код выложен на [GitHub](https://github.com/NKozlov/lanit-bus-bot).
           |Предложения и замечания по работе бота можно отправить на email: [kozlov.bots@gmail.com](mailto:kozlov.bots@gmail.com) (тема письма "lanit-bus-bot <вопрос/предложение/дефект>")
           |v0.1
        """.stripMargin,
        ParseMode.Markdown
      )
  }

}





