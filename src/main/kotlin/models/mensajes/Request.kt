package models.mensajes

import kotlinx.serialization.Serializable

@Serializable
data class Request<T>(
    val content: T?,
    val type: Type

) {
    enum class Type {
        SEND, GET, ERROR // etc..
    }
}
