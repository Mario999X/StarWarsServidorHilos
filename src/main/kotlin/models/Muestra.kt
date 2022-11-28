package models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
class Muestra(
    private val id: Int,
    val porcentajePureza: Int = (10..80).random(),
    val fecha: String = LocalDateTime.now().toString()
){
    override fun toString(): String {
        return "Muestra(id=$id, porcentajePureza=$porcentajePureza, fecha='$fecha')"
    }
}
