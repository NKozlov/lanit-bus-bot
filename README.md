# lanit-bus-bot
Telegram Bot for schedule of Lanit Bus.
Бот доступен в Telegram: http://telegram.me/LanitBusBot
 
# Build
Add the Artima Maven Repository as a resolver in ~/.sbt/1.0/global.sbt (or ~/.sbt/0.13/global.sbt, if you are using SBT 0.13.x), like this:
```resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"```

For build bot, execute command `sbt clean universal:packageBin`.

# Run
- `./bot.sh start` launch bot
- `./bot.sh stop` stop bot
- `./bot.sh restart` restart bot
- `./bot.sh status` check status
