package saba.ast

class Program(
	val statements: List<Statement>
) : Node {
	override fun tokenLiteral() = if (statements.isNotEmpty()) statements[0].tokenLiteral() else ""
	
	override fun string(): String {
		var infoString = ""
		for (statement in statements) infoString += statement.string()
		return infoString
	}
	
}