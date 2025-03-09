# Parser of Select Queries

## How to Run
- Just run `Main.kt`.
- You can also check and run the unit tests.

## How It Works
- A raw SQL string is exploded by [QueryStatements](src/main/kotlin/core/QueryStatements.kt).
- It is then used to build a structured [Select Query](src/main/kotlin/core/QueryStatements.kt).

## Notes on Parser Operation
The WHERE clause can consist of complex conditions joined in various ways, so I decided to return the entire WHERE clause as a string.