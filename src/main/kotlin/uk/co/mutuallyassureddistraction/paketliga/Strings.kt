package uk.co.mutuallyassureddistraction.paketliga

import dev.kord.core.entity.Member
import java.time.ZonedDateTime
import uk.co.mutuallyassureddistraction.paketliga.dao.entity.DEFAULT_CARRIER
import uk.co.mutuallyassureddistraction.paketliga.matching.FindGamesResponse
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessTime
import uk.co.mutuallyassureddistraction.paketliga.matching.time.GuessWindow
import uk.co.mutuallyassureddistraction.paketliga.matching.time.toUserFriendlyString

/** Extensions */
val ContributeExtensionMessage =
    """
:postal_horn: Discord guessing game. How to contribute :postal_horn:
                        
* Source code is hosted at: <https://github.com/OhDearMoshe/discord-paketliga>
* Found a bug? Please raise an issues or a PR
* Have a feature request? Please raise an issues or a PR
* Want to suggest an improvement? Please raise an issue or PR
* Snark? Direct to OhDearMoshe then go contemplate your life choices
"""
        .trimIndent()

val CreditsExtensionMessage =
    """
:postal_horn: Discord guessing game. Credits :postal_horn:
                        
* For Shreddz, who was a better postmaster general than this bot ever could be
* For Mike, who bitched this bot into existence
* For Z, who did most of the work really
* And for DavidTheWin and Jin who also did a lot of the work
* For Moshe, who did contribute at some point he swears
* For Kjmci for product consultation
* For Wraith, TooDeep and DangerZone for excellent QA
* For Capybara whom did yet more excellent QA
"""
        .trimIndent()

/** Game Validation */
const val DeliveryWindowStartAfterEndError =
    "<:notstonks:905102685827629066> Start of the delivery window must be before the end of the delivery window"

const val GuessDeadlineAfterWindowStartError =
    "<:ohno:760904962108162069> Deadline for guesses must be before the delivery window opens"

const val GuessDeadlineInPastError = "<:pikachu:918170411605327924> Deadline for guesses can't be in the past."

const val GameValidatorGameIsNullError = "Inactive or invalid game ID. Double-check and try again"

const val ChangingAnotherUsersGameError = "Mr Pump stops you from interfering with another persons mail"

const val GameNotActiveError = "Game (already) over, man. Should have sent this update first class"

const val UpdateDidNotChangeAnythingError = "<:thonk:344120216227414018> You didn't change anything"

/** Guess Validator */
fun guessValidatorGameIsNullError(gameId: Int) = "Guessing failed, there is no active game with game ID #$gameId"

const val GuessingInOwnGameError = "Mr Pump forbids you from guessing in your own game."

// TODO: guessing window usage here referring to deadline is inconsistent
fun guessingWindowClosedError(gameId: Int) =
    "*\\*womp-womp*\\* Too late, the guessing window has closed for game ID #$gameId"

fun guessOutsideOfGuessWindow(windowStart: ZonedDateTime, windowClose: ZonedDateTime) =
    "Guesses must be between ${windowStart.toUserFriendlyString()} and ${windowClose.toUserFriendlyString()}"

/** Game End Service */
const val GameEndServiceGameIsNullError = "No games found."

const val DeliveryTimeOutsideWindowVoidReason = "Delivery time is outside of delivery window"

const val DeliveryTimeOutsideWindowGameVoidMessage =
    "Package was delivered outside of the window, we're all losers this time"

fun deliveryTimeInThePastErrorMessage(zonedDeliveryDateTime: ZonedDateTime) =
    "Delivery time interpreted as being invalid: ${zonedDeliveryDateTime.toUserFriendlyString()}, Try adding a qualifier like “11:11 **this morning**” or “5:45pm **today**” to help Dr Pakidge understand it more clearly."

const val GameEndServiceGenericErrorMessage =
    "Something went wrong. Check your inputs and try again, or just shout at @OhDearMoshe"

fun failedToEndGameDueToDeliveryTimeOutOfWindowErrorMessage(zonedDeliveryDateTime: ZonedDateTime, gameId: Int) =
    "Ending game failed, delivery time #$zonedDeliveryDateTime is not between start and closing window of game #$gameId"

/** Game Upsert Service */
fun gameAnnouncementMessage(
    gameName: String?,
    member: Member?,
    username: String,
    gameId: Int,
    guessWindow: GuessWindow,
    carrier: String?,
): String {
    var userCarrier = ""
    if (carrier != null && carrier != DEFAULT_CARRIER) {
        userCarrier = "$carrier "
    }
    var announceEmoji = ":postal_horn:"
    if (gameId.toString().endsWith("69")) {
        announceEmoji = "<:noice:831631016891514950>"
    }
    return "$announceEmoji ${gameNameStringMaker(gameName, gameId)} | ${member?.mention ?: username}'s ${userCarrier}package is arriving " +
        "between ${guessWindow.startAsHumanFriendlyString()} and " +
        "${guessWindow.endAsHumanFriendlyString()}. " +
        "Guesses accepted until ${guessWindow.deadlineAsHumanFriendlyString()}"
}

fun gameUpdateMessage(
    gameId: Int,
    guessWindow: GuessWindow,
    carrier: String?,
    member: Member?,
    username: String,
): String {
    var userCarrier = ""
    if (carrier != null && carrier != DEFAULT_CARRIER) {
        userCarrier = "$carrier "
    }
    return ":postal_horn: #$gameId has been updated | ${member?.mention ?: username}'s ${userCarrier}package is now arriving between " +
        "${guessWindow.startAsHumanFriendlyString()} and ${guessWindow.endAsHumanFriendlyString()}. " +
        "Guesses accepted until ${guessWindow.deadlineAsHumanFriendlyString()}"
}

fun gameUpdatedOnlyCarrier(gameId: Int, carrier: String, member: Member?, username: String) =
    ":postal_horn: #$gameId has been updated | ${member?.mention ?: username}'s package is now delivered by $carrier"

const val GameCreationErrorMessage = "<:pressf:692833208382914571> You done goofed. Check your inputs and try again."

// We need username for non-server users that are using this command, if any (hence the nullable Member)
// Kinda unlikely, but putting this here just in case
private fun gameNameStringMaker(gameName: String?, gameId: Int): String = "$gameName (#$gameId)"

/** Guess Upsert Service */
fun guessCreationMessage(userMention: String, guessTime: GuessTime, gameId: Int): String {
    var emoji = "<:sickos:918170456190775348>"
    if (guessTime.isMakingAWish()) {
        emoji = ":stars:"
    }
    return "$emoji $userMention has guessed ${guessTime.toHumanString()} for game ID #$gameId"
}

const val GuessCreationErrorMessage = "<:pressf:692833208382914571> You done goofed. Check your inputs and try again."

const val GuessTakenErrorMessage = "You silly <:sausage:275920818339315712> another user has already guessed that time"

fun gameNotValidOrActiveErrorMessage(gameId: Int) =
    "*\\*womp-womp*\\* Game ID #$gameId is not valid or is no longer active"

/** Void Game Service */
fun voidGameServiceGameIsNullError(gameId: Int) = "Game $gameId was not found"

const val VoidGameServiceChangingAnotherUsersGameError = "Mr Pump prevents you from interfering with another game"

fun gameVoidedMessage(gameId: Int) = "Game $gameId has been voided"

fun findGuessWindow(response: FindGamesResponse): String {
    return "Arriving between ${response.startAsHumanFriendlyString()} and ${response.endAsHumanFriendlyString()} " +
        ".\n Guesses accepted until  ${response.guessesCloseAsHumanFriendlyString()}"
}

fun findGuessGameName(response: FindGamesResponse, user: String): String {
    return "ID #${response.gameId}: Game by $user - ${response.gameName}"
}

val startGameStrings =
    listOf(
        ":postal_horn: NEITHER RAIN NOR SNOW NOR GLOM OF NIT CAN STAY THESE MESENGERS ABOT THEIR DUTY :postal_horn:",
        "We do not talk about collusion",
        "We do NOT talk about collusion",
        "Guess who's back you fucks",
        "Rumours of my demise have been greatly exaggerated",
        "Mike restarted his Plex server, I'm just a victim",
        "HELLO LITTLE CITRA",
        "Blobby blobby blob blob",
        "Eh, of course you insinuate something like that in your usual patronizing manner",
        "Evri dog has its day",
        "Got caught chefposting and had to be restarted",
        "Noot Noot!!! <:nootnoot:340091832924897282> ",
        "Understandable. Have a nice day",
        "Factory resetting my phone… If I don’t make it back tell TV chef I love him",
        "HA HA! BUSINESS",
        "Well now I’m not doing it",
        "It was DNS",
        "Choo-choo motherfucker!",
        "Someone put 20p in the meter",
        "Yeah. I’m thinking I’m back",
        "Is that a packidge in your pocket or are you just happy to see me",
    )

// Need to get this from Git and automate this at some point
val ReleaseNotes =
    """
        V0.0.10
        * Added Leaderboard sorting based on wins : played ratio
    """
        .trimIndent()
