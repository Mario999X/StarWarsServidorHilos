package server

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Muestra
import models.mensajes.Request
import models.mensajes.Response
import monitor.Servidor
import mu.KotlinLogging
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

private val log = KotlinLogging.logger {}
private val json = Json

class GestionClientes(private val s: Socket, private val monitor: Servidor) : Runnable {

    // Preparamos la entrada-salida de Request-Response
    private val entrada = DataInputStream(s.getInputStream())
    private val salida = DataOutputStream(s.getOutputStream())

    override fun run() {

        // Leemos la peticion
        val receiveRequest = entrada.readUTF()

        val request = json.decodeFromString<Request<Muestra>>(receiveRequest)
        log.debug { "Peticion recibida: $request" }

        // Actuamos segun la peticion
        when (request.type) {
            Request.Type.GET -> {
                // Obtenemos la muestra
                val muestra = monitor.get()
                log.debug { "\t$muestra recibida de monitor" }

                // Creamos la respuesta
                val response = Response(
                    muestra,
                    Response.Type.OK
                )

                // Enviamos la respuesta
                val sendResponse = json.encodeToString(response)
                log.debug { "Respuesta: $sendResponse" }
                salida.writeUTF(sendResponse)
            }

            Request.Type.SEND -> {
                // Agregamos la muestra
                monitor.put(request.content as Muestra)

                // Creamos la respuesta
                val response = Response(
                    request.content,
                    Response.Type.OK
                )

                // Enviamos la respuesta
                val sendResponse = json.encodeToString(response)
                log.debug { "Respuesta $sendResponse" }
                salida.writeUTF(sendResponse)
            }

            else -> {}
        }
    }
}