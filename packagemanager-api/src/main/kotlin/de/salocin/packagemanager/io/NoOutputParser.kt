package de.salocin.packagemanager.io

object NoOutputParser : OutputParser<String> {

    override fun parseLine(line: String): String {
        return line
    }
}
