package de.salocin.packagemanager.fake

import de.salocin.packagemanager.io.OutputParser
import de.salocin.packagemanager.io.ParsedLine
import kotlin.reflect.KClass

/**
 * Maps each line that could be parsed using [OutputParser.parseLine] to output type [T].
 */
inline fun <reified T : Any> OutputParser<ParsedLine>.mapEachMatch(): OutputParser<T> {
    return mapEachMatchTo(T::class)
}

/**
 * Maps each line that could be parsed using [OutputParser.parseLine] to output type [T].
 */
fun <T : Any> OutputParser<ParsedLine>.mapEachMatchTo(targetClass: KClass<T>): OutputParser<T> {
    return mapEachMatchTo { parsedLine ->
        parsedLine.buildValueClassInstance(targetClass)
    }
}
