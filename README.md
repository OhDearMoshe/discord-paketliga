# Pretty much all of the work here was actually done by https://github.com/christiantowb who deff deserves all credit

# PKL - Paketliga for Discord
![img.png](img.png)

PKL is a bot for gamifying package deliveries.

## Usage
Replace the placeholder in `.env` file, namely the `BOT_TOKEN`, `SERVER_ID`,`POSTGRES_USERNAME`, `POSTGRES_JDBC_URL` and `POSTGRES_PASSWORD`. As this is not a global bot (for now?) and we haven't really configure it to have different database for different servers, then the settings should really be personalised for each server that is going to use the bot, hence, different database as well.

Afterwards, just run up `Main.kt` and the bot will be good to go.

### Local running
Assuming you are using the docker compose file configs for postgres you can use the following command to run the migration
```./gradlew flywayMigrate -Dflyway.url=jdbc:postgresql://127.0.0.1:666/PKL -Dflyway.user=mypreciousadmin -Dflyway.password=localtestingonlypassword -Dflyway.locations=filesystem:migrations```

## Parameter info

### Date Format
Note: Any time-related parameters are equipped with Natural Language date/time parser. While it is possible to just go with "<b>18:00</b>" as input, the bot would prefer a much verbose input. Here are some examples:
- <b>"Today 18:00"</b> - Will translate to today's date at 18:00
- <b>"24 May 2023 at 18:00"</b> - Self-explanatory
- <b>"18:00"</b> - Ambiguous, could be for tomorrow or today depends on whether the time has passed or not
- <b>"24 May at 19:00"</b> - If the month has passed, this could be May next year
- etc, try it yourself, the result may be different depends on your input

## List of available commands

### `/paketliga`
Create a PKL game, with parameters:
- `startwindow` - Start window of the delivery time
- `closewindow` - Closing window of the delivery time
- `guessesclose (Optional)` - Guessing deadline for guessers. If none provided it will give a default value of an hour from current time. Or if window begins before then, five minutes before window start
- `gamename (optional)` - Game name for the created game.

### `/findgames`
Find <b>active</b> game(s), with parameters:
- `gamecreator (optional)` - The creator of the game
- `gameid (optional)` - ID of the game
- `gamename (optional)` - Name of the game

Leaving all the parameters empty will resulted in the bot listing all active games.

### `/guessgame`
Guess an active game, with parameters:
- `gameid` - ID of the game
- `guesstime` - Guessed delivery time

Guessing a game twice with same game from a same user will resulted in updated guess instead of a new one. </br>
Guessing a game with guess time outside delivery window will resulted in failed command.

### `/findguess`
Find guess(es) from game(s), with parameters:
- `guessid (optional)` - ID of the guess
- `gameid (optional)` - ID of the game

Leaving all the parameters empty will resulted in empty response (for now)

### `/updategame`
Update an active game, with parameters:
- `gameid` - ID of the game
- `startwindow (optional)` - Start window of the delivery time
- `closewindow (optional)` - Closing window of the delivery time
- `guessesclose (optional)` - Guessing deadline for guessers

Leaving all optional parameters empty will resulted in no games to be updated.<br/>
Updating a game will resulted in all guessers to be notified (if any) to update their guesses.

### `/endgame`
End an active game, with parameters:
- `gameid` - ID of the game
- `deliverytime` - Actual delivery time for the game

Ending a game with delivery time outside the window will resulted in failed command.
Ending a game will notify the winning guessers.

### `/leaderboard`
Show leaderboard sorted by total points descending, with parameters:
- `username (optional)` - Username of the user

Leaving the parameter empty will resulted in all (played) users to be shown.
The fields shown are: played, win, lost, and total points.

## Contributing / Bug report
Please open an Issue / PR, and we'll address it soon. 

## Credits
To Chris, who did the hard bits
To Mike, who bitched this into existence




