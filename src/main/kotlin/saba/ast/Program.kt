package saba.ast

import saba.ast.statement.Statement

class Program(
	val statements: MutableList<Statement>
) : Node {
	override fun tokenLiteral() = if (statements.isNotEmpty()) statements[0].tokenLiteral() else ""
	
	override fun toString(): String {
		var infoString = ""
		for (statement in statements) infoString += statement.toString()
		return infoString
	}
	
}