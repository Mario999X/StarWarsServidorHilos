package models.mensajes

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val content: T?, // Contenido de la respuesta
    val type: Type, // Tipo de respuesta

) {
    enum class Type {
        OK, STOP, ERROR
    }
}
