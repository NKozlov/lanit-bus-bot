package pro.nkozlov.telegram.bots.lanitbus

import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.api.declarative.Commands
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models.{KeyboardButton, ReplyKeyboardMarkup}
import pro.nkozlov.telegram.api.CustomCommands

object LanitBusBot extends TelegramBot with Polling with Commands with CustomCommands {
  val TO_OFFICE_BUTTON: String = ConfigContextImpl.buttonOffice
  val TO_METRO_BUTTON: String = ConfigContextImpl.buttonMetro
  val HELP = "/help"
  val VERSION = "2.1"

  import BusSchedule._

  def token: String = ConfigContextImpl.token

  def replyKeyboard = ReplyKeyboardMarkup(List(List(KeyboardButton(TO_OFFICE_BUTTON), KeyboardButton(TO_METRO_BUTTON)), List(KeyboardButton(HELP))), true)

  onCommand("/rizhskaya_office") { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)
    reply(
      "_Маршрутки от м. Рижская до офиса (Мурманский)_\n" + scheduleFromRizhskaya().map(list => list(1)).mkString(", ") + "",
      ParseMode.Markdown,
      replyMarkup = replyKeyboard
    )
  }

  onCommand("/marina_office") { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)
    reply(
      "_Маршрутки от м. Марьина Роща до офиса (Мурманский)_\n" + scheduleFromMarinaRoszha().map(list => list(1)).mkString(", "),
      ParseMode.Markdown,
      replyMarkup = replyKeyboard
    )
  }

  onCommand("/office_rizhskaya") { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)
    reply(
      "_Маршрутки от офиса (Мурманский) до м. Рижская_\n" + scheduleOfficeToRizhskaya().map(list => list(1)).mkString(", "),
      ParseMode.Markdown,
      replyMarkup = replyKeyboard
    )
  }

  onCommand("/office_marina") { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)
    val date = msg.date
    val chatId = msg.source
    reply(
      "_Маршрутки от офиса (Мурманский) до м. Марьина Роща_\n" + scheduleOfficeToMarinaRoszha().map(list => list(1)).mkString(", "),
      ParseMode.Markdown,
      replyMarkup = replyKeyboard
    )
  }

  onCustomCommand("/to_office", TO_OFFICE_BUTTON) { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)
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
        "\uD83D\uDE8C(№239): " + fromRizhskayaA + "\n\n" +

        "*От м. Марьина Роща*\n" +
        "\uD83D\uDE90(М): " + fromMarinaRoszhaM + "\n",
      ParseMode.Markdown,
      replyMarkup = replyKeyboard
    )
  }

  onCustomCommand("/to_metro", TO_METRO_BUTTON) { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)
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
        "\uD83D\uDE8C(№239): " + toRizhskayaA + "\n\n" +

        "*До м. Марьина Роща*\n" +
        "\uD83D\uDE90(М): " + toMarinaRoszhaM + "\n",
      ParseMode.Markdown,
      replyMarkup = replyKeyboard
    )
  }

  onCommand("/support") { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)

    reply(
      s"""
         |Автор - Козлов Никита (@pravprod).
         |
           |Исходный код выложен на [GitHub](https://github.com/NKozlov/lanit-bus-bot).
         |
           |Предложения и замечания по работе бота можно отправить на email: [kozlov.bots@gmail.com](mailto:kozlov.bots@gmail.com) (тема письма "lanit-bus-bot <вопрос/предложение/дефект>")
         |Оцените бота тут: https://storebot.me/bot/lanitbusbot
         |v$VERSION
        """.stripMargin,
      ParseMode.Markdown,
      replyMarkup = replyKeyboard
    )
  }

  onCommand("/start") { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)
    val firstName = msg.from.get.firstName

    reply(
      s"""
         |Приветствую тебя, $firstName!
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
         |v$VERSION
        """.stripMargin,
      ParseMode.Markdown,
      replyMarkup = replyKeyboard
    )
  }

  onCommand("help") { implicit msg =>
    logger.debug("execute command '{}' from {}", msg.text.get, msg)
    reply(
      s"""
         |/rizhskaya_office - Рижская-Офис (маршрутки).
         |/office_rizhskaya - Офис-Рижская (маршрутки).
         |/marina_office - Марьина Роща-Офис (маршрутки).
         |/office_marina - Офис-Марьина Р (маршрутки).
         |/to_office - Ближайший транспорт до офиса.
         |/to_metro - Ближайший транспорт до метро.
         |/support - Вопросы и предложения.
         |/help - список команд.
       """.stripMargin,
      replyMarkup = replyKeyboard
    )
  }

}





