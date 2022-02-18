package de.salocin.packagemanager.io

/**
 * A parser, which maps a [String] to an output type [T].
 */
interface OutputParser<T> {

    /**
     * Parses a single line to output type [T]. If the line couldn't be parsed, returns null.
     */
    fun parseLine(line: String): T?

    /**
     * Maps each line that could be parsed using [parseLine] to output type [V]. If the conversation isn't possible, the
     * underlying mapping [block] may return null.
     */
    fun <V> mapEachMatchTo(block: (T) -> V?): OutputParser<V> {
        return object : OutputParser<V> {

            override fun parseLine(line: String): V? {
                return this@OutputParser.parseLine(line)?.let(block)
            }
        }
    }
}
