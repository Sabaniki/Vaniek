package saba.ast.statement

import saba.token.Token

class BlockStatement(
	val token: Token
): Statement {
	val statements= mutableListOf<Statement>()
	override fun tokenLiteral() = token.literal
	override fun toString(): String {
		var string = ""
		statements.map { string += it.toString()}
		return string
	}
}