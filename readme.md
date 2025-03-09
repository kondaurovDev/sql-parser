# Parser of Select Queries

## What is This?

This Kotlin-based parser is designed to parse only SELECT queries in SQL. Since the parser does not need to handle DELETE or UPDATE queries, I decided to focus exclusively on SELECT queries.

I implemented a naive parsing algorithm—much like how a person might break down a query on paper. The process involves splitting the SQL string into groups of expressions (SELECT, FROM, WHERE, etc.) and then assembling these expressions to build a [Select Query](src/main/kotlin/core/SelectQuery.kt) class object.

## How to Run

Just run `Main.kt`.

You can also check and run the unit tests.

## How It Works

A raw SQL string is exploded by [QueryStatements](src/main/kotlin/core/QueryStatements.kt).

These statements are then used to build a structured [Select Query](src/main/kotlin/core/SelectQuery.kt).

## Notes

- The WHERE/HAVING clause can consist of complex conditions joined in various ways, so I decided to return the entire WHERE/HAVING clause as a string.