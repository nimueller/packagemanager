package de.salocin.packagemanager.io

object PlainOutputParser : OutputParser<String> {

    override fun parseLine(line: String): String {
        return line
    }
}
