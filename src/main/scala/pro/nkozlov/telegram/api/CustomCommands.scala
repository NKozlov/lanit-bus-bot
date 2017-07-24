package pro.nkozlov.telegram.api

import info.mukel.telegrambot4s.api.Extractors.textTokens
import info.mukel.telegrambot4s.api.declarative.{Action, Commands, ToCommand}
import info.mukel.telegrambot4s.models.Message

/**
  * Custom command without check '/' prefix.
  */
trait CustomCommands extends Commands {

  def onCustomCommand[T: ToCommand](commands: T*)(action: Action[Message]): Unit = {
    require(commands.nonEmpty, "At least one command required")
    val toCommandImpl = implicitly[ToCommand[T]]
    val variants = commands.map(toCommandImpl.apply)

    require(variants.forall(_.forall(c => !c.isWhitespace)),
      "Commands cannot contain whitespace")

    onMessage { implicit msg =>
      using(textTokens) { tokens =>
        val cmd = tokens.head
        // Filter only commands
        val target = ToCommand.cleanCommand(cmd)
        if (variants.contains(target))
          action(msg)

      }
    }
  }
}
