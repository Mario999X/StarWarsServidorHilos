package client

import java.util.concurrent.Executors

// El cliente cuenta con los requisitos para el ejercicio

fun main(){

    val pool = Executors.newFixedThreadPool(4)

    val androides = listOf(
        Androide("R2D2"),
        Androide("BB8")
    )
    val terminales = listOf(
        Terminal("Luke"),
        Terminal("Leia")
    )

    androides.forEach {
        pool.execute(it)
    }

    terminales.forEach {
        pool.execute(it)
    }

    pool.shutdown()
}
