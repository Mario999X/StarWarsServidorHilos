package monitor

import models.Muestra
import mu.KotlinLogging
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val log = KotlinLogging.logger {}

class Servidor(private val maxMuestras: Int = 8) {

    // Almacenamiento de muestras
    private val servidor: MutableList<Muestra> = mutableListOf()

    // Cerrojo con condiciones
    private val lock: ReentrantLock = ReentrantLock()
    private val servidorConsumeCondition: Condition = lock.newCondition() // Existen muestras para consumir
    private val servidorProduceCondition: Condition = lock.newCondition() // No se puede consumir cuando no hay muestras

    fun get(): Muestra{
        lock.withLock {
            while (servidor.size == 0){
                // Se esperan los consumidores [Terminal]
                servidorConsumeCondition.await()
            }
            val muestra = servidor.removeFirst()
            // Avisamos a los productores que se puede producir
            servidorProduceCondition.signalAll()

            log.debug { "$muestra consumida" }
            return muestra
        }
    }

    fun put(item: Muestra){
        lock.withLock {
            while (servidor.size == maxMuestras){
                // Si estamos al maximo, los productores esperan
                servidorProduceCondition.await()
            }
            log.debug { "Guardando $item" }
            servidor.add(item)

            // Como existe como minimo 1 muestra, avisamos a los consumidores
            servidorConsumeCondition.signalAll()
        }
    }
}