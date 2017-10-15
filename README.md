# lanit-bus-bot
Telegram Bot for schedule of Lanit Bus.
Бот доступен в Telegram: http://telegram.me/LanitBusBot
 
# Build
Add the Artima Maven Repository as a resolver in ~/.sbt/1.0/global.sbt (or ~/.sbt/0.13/global.sbt, if you are using SBT 0.13.x), like this:
```resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"```

For build bot, execute command `sbt clean universal:packageBin`.

# Install
- `conf/application.conf` add api key to `telegram.bot.api.token`
- In `lib/` folder create link `lanit-bus-bot.jar` to `lanit-bus-bot-2.1-2.12.3.jar`

# Run
- `./bot.sh start` launch bot
- `./bot.sh stop` stop bot
- `./bot.sh restart` restart bot
- `./bot.sh status` check status

If you setting service for bot, use nex commands:
- `sudo systemctl start lanit-bus-bot` launch bot
- `sudo systemctl stop lanit-bus-bot` stop bot

(`lanit-bus-bot` is folder or link to folder, which contains bot files)
