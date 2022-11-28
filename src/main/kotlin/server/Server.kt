package server

import monitor.Servidor
import mu.KotlinLogging
import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.nio.file.Paths
import java.util.concurrent.Executors

/* Enunciado del ejercicio original
* R2D2 y BB8, recogen muestras de Endor y se envian a un servidor, donde Luke y Leia las recogen,
* esas muestras son escritas en un manifiesto.txt si cumplen cierto requisito.
*
* Realizando un analisis del encunciado sacamos lo siguiente:
*   Androides R2D2 y BB8 (Productores X2)
*   Muestras (porcentaje de pureza) (Producto)
*   Servidor (SC/Monitor)
*   Terminales Luke y Leia (Consumidores x2)
*
* Lo importante a recordar:
*   Las muestras tenian un porcentaje de pureza, que se mueve entre 10 y 80; solo si el numero es mayor a 60 se escribe
* en el txt
*   Los Androides mandan las muestras con un descanso de 1,50 segundos entre ellas
*   Las terminales toman las muestras con un descanso entre 1 y 1,50 segundos de forma aleatoria entre ellas
*
*   Las terminales tienen un max de muestras a tomar, 5
*   El servidor tiene un limite para las muestras almacenadas, 8
* --------------------------------------------------------
* */

private val log = KotlinLogging.logger {}
private const val PUERTO = 6969

fun main() {
    // FICHERO
    val userDir = System.getProperty("user.dir")
    val pathFile = Paths.get(userDir + File.separator + "data").toString()
    val file = File(pathFile + File.separator + "manifiesto.txt")
    file.writeText("")

    // Datos del servidor
    val servidor: ServerSocket
    var cliente: Socket

    // Monitor
    val serverMonitor = Servidor()

    // Necesitamos una pool de hilos para manejar a los clientes
    val pool = Executors.newFixedThreadPool(10)

    // Arrancamos el servidor
    try {
        servidor = ServerSocket(PUERTO)
        log.debug { "Arrancando Servidor en: ${servidor.inetAddress}:${servidor.localPort}" }

        while (true) {
            log.debug { "Servidor esperando..." }

            // Aceptamos al cliente
            cliente = servidor.accept()
            log.debug { "Peticion de cliente -> " + cliente.inetAddress + " --- " + cliente.port }

            // Lo cedemos al gestor de clientes
            val gc = GestionClientes(cliente, serverMonitor)
            pool.execute(gc)

            log.debug { "Cliente -> " + cliente.inetAddress + " --- " + cliente.port + " desconectado." }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}