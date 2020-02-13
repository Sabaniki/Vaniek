package saba.ast.statement

import saba.token.Token

class BlockStatement(
	val token: Token,
	val statements: MutableList<Statement>
): Statement {
	override fun tokenLiteral() = token.literal
	
	override fun toString(): String {
		var string = ""
		statements.map { it -> string += it.toString()}
		return string
	}
}