package saba.parser

enum class Priority(val i: Int) {
	LOWEST(0),
	EQUALS(1),  // ==
	LESS_OR_GREATER(2), // > または <
	SUM(3), // +
	PRODUCT(4),     // *
	PREFIX(5),  // -X または !X
	CALL(6) // myFunction(X)
}