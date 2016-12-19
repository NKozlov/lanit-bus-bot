package pro.nkozlov.telegram.bots.lanitbus

import info.mukel.telegrambot4s._
import api._
import methods._
import Implicits._

object LanitBusBot extends TelegramBot with Polling with Commands {

  import BusSchedule._

  def token: String = ConfigContext.token

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
          sch.head.filter(list => list.head contains "М").take(4).map(list => list(1)).mkString(", ")
        else "Маршрутки уже спят :)"

      def fromRizhskayaA =
        if (sch.head.exists(list => list.head contains "А"))
          sch.head.filter(list => list.head contains "А").take(4).map(list => list(1)).mkString(", ")
        else "Автобусы уже спят :)"

      def fromMarinaRoszhaM =
        if (sch(1).exists(list => list.head contains "М"))
          sch(1).filter(list => list.head contains "М").take(4).map(list => list(1)).mkString(", ")
        else "Маршрутки уже спят :)"

      reply(
        "_Ближайший транспорт до офиса_\n" +
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
          sch.head.filter(list => list.head contains "М").take(4).map(list => list(1)).mkString(", ")
        else "Маршрутки уже спят :)"

      def toRizhskayaA =
        if (sch.head.exists(list => list.head contains "А"))
          sch.head.filter(list => list.head contains "А").take(4).map(list => list(1)).mkString(", ")
        else "Автобусы уже спят :)"

      def toMarinaRoszhaM =
        if (sch(1).exists(list => list.head contains "М"))
          sch(1).filter(list => list.head contains "М").take(4).map(list => list(1)).mkString(", ")
        else "Маршрутки уже спят :)"

      reply(
        "_Ближайший транспорт из офиса_\n" +
          "*До м. Рижская*\n" +
          "\uD83D\uDE90(М): " + toRizhskayaM + "\n" +
          "\uD83D\uDE8C(А): " + toRizhskayaA + "\n\n" +

          "*До м. Марьина Роща*\n" +
          "\uD83D\uDE90(М): " + toMarinaRoszhaM + "\n",
        ParseMode.Markdown

      )
  }

  on("/support", "Вопросы и предложения") { implicit msg =>
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
           |Оцените бота тут: https://storebot.me/bot/lanitbusbot
           |v0.1
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
           |Оцените бота тут: https://storebot.me/bot/lanitbusbot
           |v0.1
        """.stripMargin,
        ParseMode.Markdown
      )
  }

}





