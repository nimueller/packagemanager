package de.salocin.packagemanager.io

interface Process<T> {

    suspend fun execute(): List<T>
}
