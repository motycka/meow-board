package messageboard.motyka.cz

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun main() {
    embeddedServer(
        factory = Netty,
        port = System.getenv("PORT").let { if (it.isNullOrBlank()) 8088 else it.toInt() },
//        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = false
            ignoreUnknownKeys = false
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = false
        })
    }

    install(CORS) {
        anyHost()
        anyMethod()
        allowHeader("Content-Type")
    }

    install(RequestValidation) {
        validate<NewMessage> { it.validate() }
    }

    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, cause.reasons.joinToString())
        }
    }

    routing {
        route("/messages") {
            get {
                val parameters = call.request.queryParameters

                // TODO paging

                call.respond(
                    message = Messages.take(100),
                    typeInfo = typeInfo<List<Message>>()
                )
            }

            post {
                val message = call.receive<NewMessage>().toMessage()
                Messages.add(message)
                call.respond(message = message, typeInfo = typeInfo<Messages>())
            }

            delete {
                Messages.clear()
                call.respond(HttpStatusCode.OK, typeInfo<String>())
            }
        }
    }
}

object Messages : MutableList<Message> by mutableListOf(
    Message(
        time = getFormattedTime(),
        author = "Moni",
        message = "Čauky mňauky!"
    )
)

private fun getFormattedTime(): String {
    return ZonedDateTime.now().withZoneSameInstant(ZoneId.of("Europe/Prague"))
        .format(DateTimeFormatter.ofPattern("dd. MM. yyyy HH:mm:ss"))
}

private fun NewMessage.toMessage() = Message(
    time = getFormattedTime(),
    author = author,
    message = message
)

private fun NewMessage.validate(): ValidationResult {
    return when {
        author.isBlank() -> ValidationResult.Invalid("Author must not be blank")
        message.isBlank() -> ValidationResult.Invalid("Message must not be blank")
        else -> ValidationResult.Valid
    }
}

@Serializable
data class NewMessage(
    val author: String,
    val message: String,
)

@Serializable
data class Message(
    val time: String? = null,
    val author: String,
    val message: String,
)

