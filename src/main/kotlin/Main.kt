import kotlinx.serialization.json.Json
import core.*;

val prettyJson = Json {
    prettyPrint = true
}

fun main() {

    val exampleSql = """
        SELECT author.name, count(book.id), sum(book.cost), (SELECT * from table1) as table1
        FROM customers, (SELECT * FROM author) as author
        LEFT JOIN book ON (author.id = book.author_id)
        WHERE author.name like 'a*'
        GROUP BY author.name 
        HAVING COUNT(*) > 1 AND SUM(book.cost) > 500
        offset 5
        LIMIT 10
    """.trimIndent()

    val structured = SelectQuery.create(exampleSql)

    val json = prettyJson.encodeToString(structured)

    println(json)


}