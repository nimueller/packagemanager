package de.salocin.packagemanager.io

class RegexOutputParser(private val regex: Regex) : OutputParser<ParsedLine> {

    override fun parseLine(line: String): ParsedLine? {
        return regex.matchEntire(line)?.let { result ->
            ParsedLine(result.groups.mapNotNull { group ->
                group?.value
            })
        }
    }

    fun takeGroup(group: Int): OutputParser<String> {
        return mapEachMatchTo { line ->
            line[group]
        }
    }

    fun takeGroups(vararg groups: Int): OutputParser<ParsedLine> {
        return mapEachMatchTo { matchedGroups ->
            ParsedLine(matchedGroups.value.filterIndexed { index, _ -> index in groups })
        }
    }

    fun takeAllGroups(): OutputParser<ParsedLine> {
        return mapEachMatchTo { matchedGroups ->
            ParsedLine(matchedGroups.value.filterIndexed { index, _ -> index != 0 })
        }
    }
}
