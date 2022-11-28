package client

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Muestra
import models.mensajes.Request
import models.mensajes.Response
import mu.KotlinLogging
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.InetAddress
import java.net.Socket

private val log = KotlinLogging.logger {}
private val json = Json

private val HOST = InetAddress.getLocalHost()
private const val PORT = 6969

class Androide(val nombre: String) : Runnable {
    var produccion = true

    override fun run() {
        var numMuestra = 1
        while (produccion) {
            try {
                // Creamos el socket
                val socket = Socket(HOST, PORT)

                // Preparamos la entrada-salida de Request-Response
                val entrada = DataInputStream(socket.getInputStream())
                val salida = DataOutputStream(socket.getOutputStream())

                // Enviamos una peticion al servidor (muestra)
                val request = Request<Muestra>(
                    Muestra(numMuestra),
                    Request.Type.SEND
                )

                val sendRequest = json.encodeToString(request)
                log.debug { "Peticion: $sendRequest" }
                salida.writeUTF(sendRequest)

                // Esperamos la respuesta del servidor/gestor
                val receiveResponse = entrada.readUTF()
                val response = json.decodeFromString<Response<Muestra>>(receiveResponse)

                when (response.type) {
                    Response.Type.OK -> {
                        println("$nombre: ha enviado muestra -> ${response.content} ")
                        numMuestra++
                    }

                    Response.Type.STOP -> {
                        produccion = false
                    }

                    else -> {
                        log.error { "Error al enviar la muestra" }
                    }
                }

                // Cerramos tod0
                entrada.close()
                salida.close()
                socket.close()

                // Tiempo de produccion
                Thread.sleep(1500)

            } catch (ex: IOException) {
                log.error { "Error al atender al cliente: ${ex.message}" }
            }
        }
        println("Androide $nombre terminado")
    }
}