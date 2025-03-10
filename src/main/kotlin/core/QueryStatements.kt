package core

data class QueryStatements(
    val statements: List<Pair<SelectKeyword, String>>
) {

    companion object {

        fun create(query: String): QueryStatements {

            val normalizedQuery = query.replace(Regex("\n|\\s{2,}"), " ")

            val positions = mutableListOf<Pair<SelectKeyword, Int>>()

            SelectKeyword.all.forEach { word ->
                val matches = word.regex.findAll(normalizedQuery)
                val nonEnclosedMatch = matches.find {
                    !isSubQuery(it.range.first, normalizedQuery)
                }
                if (nonEnclosedMatch != null) positions.add(Pair(word, nonEnclosedMatch.range.first))
            }

            if (positions.map { it.second } != positions.map { it.second }.sorted()) {
                throw Error("Invalid sequence of keywords. Expected: ${SelectKeyword.all.joinToString(" -> ") { it.toString() }}")
            }

            val result = mutableListOf<Pair<SelectKeyword, String>>()

            for (i in positions.indices) {
                if (i == positions.size - 1) {
                    result.add(Pair(positions[i].first, normalizedQuery.substring(positions[i].second).trim()))
                } else {
                    result.add(Pair(positions[i].first, normalizedQuery.substring(positions[i].second, positions[i + 1].second - 1).trim()))
                }
            }

            return QueryStatements(result)
        }

    }

    fun getColumns(): List<String> {
        val content = statements.find { it.first == SelectKeyword.Select }?.second?.substring("SELECT".length)?.trim()
            ?: throw Error("Select not found")

        return splitByComma(content)
    }

    fun getFromSources(): List<String> {
        val content = statements.find { it.first == SelectKeyword.From }?.second?.substring("FROM".length)?.trim()
            ?: throw Error("From not found")

        return splitByComma(content)
    }

    fun getWhere(): String? {
        return statements.find { it.first == SelectKeyword.Where }?.second?.substring("where".length)?.trim()
    }

    fun getHaving(): String? {
        return statements.find { it.first == SelectKeyword.Having }?.second?.substring("having".length)?.trim()
    }

    fun getGroupBy(): List<String>? {
        val content = statements.find { it.first == SelectKeyword.GroupBy }?.second?.substring("group By".length)?.trim()

        if (content == null) return null

        return content.split(",").map { it.trim() }
    }

    fun getOrderBy(): List<String>? {
        val content = statements.find { it.first == SelectKeyword.OrderBy }?.second?.substring("order By".length)?.trim()

        if (content == null) return null

        return content.split(",").map { it.trim() }
    }

    fun getJoins(): List<String>? {
        val joins = statements.filter {
            SelectKeyword.Join.regex.find(it.second) != null
        }

        if (joins.isEmpty()) return null

        return joins.map { it.second }
    }

    fun getLimit(): Int? {
        val content = statements.find { it.first == SelectKeyword.Limit }?.second?.substring("limit".length)?.trim()

        if (content == null) return null

        return content.toInt()
    }

    fun getOffset(): Int? {
        val content = statements.find { it.first == SelectKeyword.Offset }?.second?.substring("offset".length)?.trim()

        if (content == null) return null

        return content.toInt()
    }



}

private fun splitByComma(input: String): List<String> {

    val result = mutableListOf<String>()

    val commaIndices = Regex(",").findAll(input)
        .filter { !isSubQuery(it.range.first, input) }
        .map { it.range.first }
        .toList()

    if (commaIndices.isEmpty()) {
        result.add(input)
        return result
    }

    result.add(input.substring(0, commaIndices[0]).trim())

    for (i in 0 until commaIndices.size - 1) {
        result.add(input.substring(commaIndices[i] + 1, commaIndices[i + 1]).trim())
    }

    result.add(input.substring(commaIndices.last() + 1).trim())

    return result

}

private fun isSubQuery(position: Int, text: String): Boolean {
    var bracketLevel = 0

    for (i in 0 until position) {
        when (text[i]) {
            '(' -> bracketLevel++
            ')' -> bracketLevel--
        }
    }

    return bracketLevel > 0
}
