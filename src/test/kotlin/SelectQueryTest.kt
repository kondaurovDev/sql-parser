package core

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class SelectQueryTest {

    @Test
    fun testImplicitJoin() {

        val query = """
            SELECT A.*, B.*, C.a 
            FROM A, B, C
        """.trimIndent()

        val actual = SelectQuery.create(query)
        val expected = SelectQuery(
            columns = listOf(Column.Table("A.*"), Column.Table("B.*"), Column.Table("C.a")),
            from = listOf(
                Source.Table("A"),
                Source.Table("B"),
                Source.Table("C")
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun testWithJoins() {
        val query = """
            SELECT sub.field1 as f1
            FROM (SELECT * from D) as sub
            LEFT JOIN table1 on table1.id = sub.a1
        """.trimIndent()

        val actual = SelectQuery.create(query)
        val expected = SelectQuery(
            columns = listOf(Column.Table("sub.field1 as f1")),
            from = listOf(
                Source.SubQuery(
                    SelectQuery(
                        listOf(Column.Table("*")),
                        listOf(Source.Table("D"))
                    ), "sub"
                ),
            ),
            joins = listOf(
                Join(Join.JoinType.left, "table1", "table1.id = sub.a1")
            )
        )
        assertEquals(expected, actual)
    }

}