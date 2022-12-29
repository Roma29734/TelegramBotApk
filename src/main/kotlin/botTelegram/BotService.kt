package botTelegram

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import utils.Constant.keep
import utils.Constant.movie
import utils.Constant.news
import utils.Constant.startText
import utils.Constant.trained
import utils.Constant.weather

@Service
class BotService : TelegramLongPollingBot() {

    @Value("\${telegram.botName}")
    private val botName: String = ""

    @Value("\${telegram.token}")
    private val token: String = ""

    override fun getBotToken(): String {
        return token
    }

    override fun getBotUsername(): String {
        return botName
    }

    override fun onUpdateReceived(update: Update?) {
        if (update != null) {
            if (update.hasMessage()) {
                val message = update.message
                val chatId = message.chatId

                val responseText = if (message.hasText()) {
                    when (val messageText = message.text) {
                        "/start" -> startText
                        "trained" -> trained
                        "news" -> news
                        "weather" -> weather
                        "movie" -> movie
                        "keep" -> keep
                        else -> "Пожалуйста напиште команду, или выберете действие кнопки"
                    }
                } else {
                    "Долбаеб, пиши текст!"
                }
                sendMessage(chatId = chatId, responseText = responseText)
            }
        }
    }

    private fun sendMessage(chatId: Long, responseText: String) {
        val responseMessage = SendMessage(chatId.toString(), responseText)


        responseMessage.enableMarkdown(true)

        responseMessage.replyMarkup = getReplyMarkup(
            listOf(
                listOf("trained", "news"),
                listOf("weather", "movie"),
                listOf("keep"),
            )
        )

        execute(responseMessage)
    }

    private fun getReplyMarkup(allButton: List<List<String>>): ReplyKeyboardMarkup {
        val markup = ReplyKeyboardMarkup()
        markup.keyboard = allButton.map { rowButton ->
            val row = KeyboardRow()
            rowButton.forEach { rowButton -> row.add(rowButton) }
            row
        }
        return markup
    }
}