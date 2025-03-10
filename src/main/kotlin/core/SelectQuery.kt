package core

import kotlinx.serialization.Serializable

@Serializable
data class SelectQuery(
    val columns: List<Column>,
    val from: List<Source>,
    val joins: List<Join>? = null,
    val where: String? = null,
    val groupBy: List<String>? = null,
    val having: String? = null,
    val orderBy: List<String>? = null,
    val limit: Int? = null,
    val offset: Int? = null
) {

    companion object {

        fun create(query: String): SelectQuery {

            val statements = QueryStatements.create(query)

            return SelectQuery(
                columns = statements.getColumns().map { Column.fromString(it) },
                from = statements.getFromSources().map { Source.fromString(it) },
                joins = statements.getJoins()?.map { Join.fromString(it) },
                where = statements.getWhere(),
                groupBy = statements.getGroupBy(),
                having = statements.getHaving(),
                orderBy = statements.getOrderBy(),
                limit = statements.getLimit(),
                offset = statements.getOffset()
            )

        }


    }

}

@Serializable
data class Join(
    val type: JoinType,
    val tableName: String,
    val on: String
) {

    companion object {
        fun fromString(input: String): Join {
            val parts = input.split(" ON ", ignoreCase = true)
            if (parts.size != 2) throw Error("Invalid join structure '$input'")
            val leftPart = parts[0].split(" ")
            if (leftPart.size != 3) throw Error("Invalid join structure '$input'. Left part must consist of three words")
            val joinType = JoinType.entries.find { it.name.equals(leftPart[0], true) }
            if (joinType == null) throw Error("Invalid join structure '$input'. Invalid join type")
            var on = parts[1]
            if (on.startsWith("(") && on.endsWith(")")) on = on.substring(1, on.length - 1)
            return Join(
                type = joinType,
                tableName = leftPart[2],
                on
            )
        }
    }

    enum class JoinType {
        inner, left, right, full
    }

}

@Serializable
sealed class Column {

    abstract val alias: String?

    @Serializable
    data class Table(
        val expression: String,
        override val alias: String? = null
    ): Column()

    @Serializable
    data class SubQuery(
        val query: SelectQuery,
        override val alias: String
    ): Column()

    companion object {
        fun fromString(input: String): Column {
            return if (input.startsWith("(")) {
                val (query, alias) = parseSubQuery(input)
                SubQuery(query, alias)
            } else {
                Table(input)
            }
        }
    }

}

@Serializable
sealed class Source {

    abstract val alias: String?

    @Serializable
    data class Table(
        val tableName: String,
        override val alias: String? = null
    ): Source()

    @Serializable
    data class SubQuery(
        val query: SelectQuery,
        override val alias: String
    ): Source()

    companion object {
        fun fromString(input: String): Source {
            return if (input.startsWith("(")) {
                val (query, alias) = parseSubQuery(input)
                SubQuery(query, alias)
            } else {
                Table(input)
            }
        }
    }
}

private fun parseSubQuery(input: String): Pair<SelectQuery, String> {
    val parts = Regex("\\(.*\\) as \\w+").find(input)?.value?.split(" as ") ?: throw Error("SubQuery with alias was expected, but got '$input'")
    val subquery = parts[0].substring(1, parts[0].length - 1)
    return Pair(SelectQuery.create(subquery), parts[1])
}
