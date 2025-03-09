package core

sealed class SelectKeyword {

    companion object {
        val all = listOf(Select, From, Join, Where, GroupBy, Having, OrderBy, Offset, Limit)
    }

    abstract val regex: Regex

    data object Select: SelectKeyword() {
        override val regex = Regex("SELECT", RegexOption.IGNORE_CASE)
    }

    data object From: SelectKeyword() {
        override val regex = Regex("FROM", RegexOption.IGNORE_CASE)
    }

    data object Join: SelectKeyword() {
        override val regex = Regex("(INNER|LEFT|RIGHT|FULL) JOIN", RegexOption.IGNORE_CASE)
    }

    data object Where: SelectKeyword() {
        override val regex = Regex("WHERE", RegexOption.IGNORE_CASE)
    }

    data object GroupBy: SelectKeyword() {
        override val regex = Regex("GROUP BY", RegexOption.IGNORE_CASE)
    }

    data object Having: SelectKeyword() {
        override val regex = Regex("HAVING", RegexOption.IGNORE_CASE)
    }

    data object OrderBy: SelectKeyword() {
        override val regex = Regex("Order By", RegexOption.IGNORE_CASE)
    }

    data object Limit: SelectKeyword() {
        override val regex = Regex("LIMIT", RegexOption.IGNORE_CASE)
    }

    data object Offset: SelectKeyword() {
        override val regex = Regex("OFFSET", RegexOption.IGNORE_CASE)
    }

}
