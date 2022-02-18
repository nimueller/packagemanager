package de.salocin.packagemanager

interface ProgressObserver {

    suspend fun notifyProgressChange(progress: Int)

    suspend fun notifyMaxProgressChange(maxProgress: Int)

    suspend fun notifyMessageChange(message: String)
}
