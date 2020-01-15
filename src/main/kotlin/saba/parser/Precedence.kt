package saba.parser

enum class Precedence {
	LOWEST,
	EQUALS,  // ==
	LESS_OR_GREATER, // > または <
	SUM, // +
	PRODUCT,     // *
	PREFIX,  // -X または !X
	CALL // myFunction(X)
}