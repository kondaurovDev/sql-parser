package core

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class QueryStatementsTest {

    @Test
    fun testQueryStatements() {

        val query = """
            SELECT author.name, count(book.id), sum(book.cost) 
            from author 
            LEFT JOIN book ON (author.id = book.author_id)
            Where author.name like 'a*' and book.cost > 1
            GROUP BY author.name 
            HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
            order By a,b
            LIMIT 10
        """.trimIndent()
        val actual = QueryStatements.create(query)

        assertContentEquals(
            listOf(
                "SELECT author.name, count(book.id), sum(book.cost)",
                "from author",
                "LEFT JOIN book ON (author.id = book.author_id)",
                "Where author.name like 'a*' and book.cost > 1",
                "GROUP BY author.name",
                "HAVING COUNT(*) > 1 AND SUM(book.cost) > 500",
                "order By a,b",
                "LIMIT 10"
            ),
            actual.statements.map { it.second }
        )

        assertContentEquals(
            listOf("author.name", "count(book.id)", "sum(book.cost)"),
            actual.getColumns()
        )

        assertContentEquals(
            listOf("author"),
            actual.getFromSources()
        )

        assertContentEquals(
            listOf("LEFT JOIN book ON (author.id = book.author_id)"),
            actual.getJoins()
        )

        assertEquals(
            "author.name like 'a*' and book.cost > 1",
            actual.getWhere()
        )

        assertContentEquals(
            listOf("a", "b"),
            actual.getOrderBy()
        )

    }


}