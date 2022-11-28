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
import java.io.File
import java.net.InetAddress
import java.net.Socket
import java.nio.file.Paths

private val log = KotlinLogging.logger {}
private val json = Json

private val HOST = InetAddress.getLocalHost()
private const val PORT = 6969

class Terminal(val nombre: String, private val maxMuestra: Int = 5, val espera: Int = (1000..1500).random()) : Runnable {
    private var muestrasTomadas = 0

    // FICHERO
    private val userDir = System.getProperty("user.dir")
    private val pathFile = Paths.get(userDir + File.separator + "data").toString()
    private val file = File(pathFile + File.separator + "manifiesto.txt")

    override fun run() {
        while (muestrasTomadas <= maxMuestra){
            // Creamos el socket
            val socket = Socket(HOST, PORT)

            // Preparamos la entrada-salida de Request-Response
            val entrada = DataInputStream(socket.getInputStream())
            val salida = DataOutputStream(socket.getOutputStream())

            // Enviamos una peticion al servidor
            val request = Request<Muestra>(
                null,
                Request.Type.GET
            )
            val sendRequest = json.encodeToString(request)
            log.debug { "Peticion: $sendRequest" }
            salida.writeUTF(sendRequest)

            // Esperamos la respuesta del server
            val receiveResponse = entrada.readUTF()

            val response = json.decodeFromString<Response<Muestra>>(receiveResponse)
            if (response.type == Response.Type.OK) {
                val muestra = response.content as Muestra
                println("$nombre -> Muestra recibida: $muestra")
                if (muestra.porcentajePureza > 60){
                    println("\t$nombre -> Escribe en fichero: $muestra")
                    file.appendText("Terminal: $nombre -> $muestra \n")
                    muestrasTomadas++
                }
            }

            // cerramos tod0
            entrada.close()
            salida.close()
            socket.close()

            // Esperamos el tiempo
            Thread.sleep(espera.toLong())
        }
        log.debug { "$nombre llego al limite de escritura" }
    }
}