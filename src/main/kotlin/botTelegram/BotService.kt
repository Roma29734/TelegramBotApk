package botTelegram


import data.more.BotRepository
import data.more.RetrofitClient
import data.more.RetrofitType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import utils.Constant.exrate
import utils.Constant.keep
import utils.Constant.movie
import utils.Constant.news
import utils.Constant.startText
import utils.Constant.trained
import utils.Constant.walap
import utils.Constant.weather
import utils.LoadingProgressState

@Suppress("NAME_SHADOWING")
@Service
class BotService : TelegramLongPollingBot() {

    private val valueRetrofitClient = RetrofitClient.getRetrofit(RetrofitType.VALUE)
    private val repository = BotRepository(valueApi = RetrofitClient.getWeatherApi(valueRetrofitClient))

    private var _statemenu = MutableStateFlow(LoadingProgressState.MENU_STATE)

    @Value("\${telegram.botName}")
    private val botName: String = ""

    @Value("\${telegram.token}")
    private val token: String = ""

    @Value("\${telegram.valueKey}")
    private val valueKey: String = ""


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

                var responseTextInput = ""

                if (message.hasText()) {
                    println("messageText ${message.text}")
                    when (val messageText = message.text) {
                        "/start" -> {
                            responseTextInput = startText
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "trained" -> {
                            responseTextInput = trained
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "walap" -> {
                            responseTextInput = walap
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "news" -> {
                            responseTextInput = news
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "weather" -> {
                            responseTextInput = weather
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "movie" -> {
                            responseTextInput = movie
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "keep" -> {
                            responseTextInput = keep
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "exrate" -> {
                            responseTextInput = exrate
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "Android Application GitHub" -> {
                            responseTextInput = "Android Application"
                            _statemenu.update { LoadingProgressState.ANDROID_APP_STATE }
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "Emulator store" -> {
                            responseTextInput =
                                "This feature is under development\uD83D\uDC68\uD83C\uDFFC\u200D\uD83D\uDCBB"
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "Actual today course" -> {
                            responseTextInput = "Выбирете валюту"
                            _statemenu.update { LoadingProgressState.COURSE_STATE }
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "\uD83D\uDD1A Назад" -> {
                            responseTextInput = "Menu"
                            _statemenu.update { LoadingProgressState.MENU_STATE }
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }

                        "USD/RUB" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                val lResult = getValue("USD/RUB")
                                println("lresult - $lResult ")
                                responseTextInput = lResult
                                sendMessage(chatId = chatId, responseText = responseTextInput)
                            }
                        }

                        "EUR/RUB" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                val lResult = getValue("EUR/RUB")
                                println("lresult - $lResult ")
                                responseTextInput = lResult
                                sendMessage(chatId = chatId, responseText = responseTextInput)
                            }
                        }

                        else -> {
                            responseTextInput = "Пожалуйста напиште команду, или выберете действие кнопки"
                            sendMessage(chatId = chatId, responseText = responseTextInput)
                        }
                    }
                } else {
                    responseTextInput = "Пишите текст!"
                    sendMessage(chatId = chatId, responseText = responseTextInput)
                }
            }
        }
    }

    private fun sendMessage(chatId: Long, responseText: String) {
        println("sendMessage chatId $responseText")
        val responseMessage = SendMessage(chatId.toString(), responseText)

        responseMessage.enableMarkdown(true)

        CoroutineScope(Dispatchers.Default).launch {

            when(_statemenu.value) {
                LoadingProgressState.MENU_STATE -> {
                    responseMessage.replyMarkup = getReplyMarkup(
                        listOf(
                            listOf("Android Application GitHub"),
                            listOf("Emulator store"),
                            listOf("Actual today course"),
                        )
                    )
                    execute(responseMessage)
                }

                LoadingProgressState.ANDROID_APP_STATE -> {
                    responseMessage.replyMarkup = getReplyMarkup(
                        listOf(
                            listOf("\uD83D\uDD1A Назад"),
                            listOf("trained", "walap"),
                            listOf("news", "weather"),
                            listOf("movie", "keep"),
                            listOf("exrate")
                        )
                    )
                    execute(responseMessage)
                }

                LoadingProgressState.EMULATOR_STORE_STATE -> {
                }

                LoadingProgressState.COURSE_STATE -> {
                    responseMessage.replyMarkup = getReplyMarkup(
                        listOf(
                            listOf("\uD83D\uDD1A Назад"),
                            listOf("USD/RUB"),
                            listOf("EUR/RUB"),
                        )
                    )
                    execute(responseMessage)
                }
            }
        }
    }

    private suspend fun getValue(symbol: String): String {
        println("botrepository $symbol 1h $valueKey")
        return try {
            val result = repository.readId(
                symbol = symbol,
            )
            result.response[0].l
        } catch (e: Exception) {
            "Возникла ошибка, пожалуйста попробуйте еще раз $e"
        }
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