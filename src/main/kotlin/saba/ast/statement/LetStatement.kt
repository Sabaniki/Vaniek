package saba.ast.statement

import saba.ast.Expression
import saba.ast.Identifier
import saba.ast.Node
import saba.token.Token

class LetStatement(
	val token: Token, // TokenType.LET トークン
	val name: Identifier,
	val value: Expression?
): Node, Statement {
	override fun tokenLiteral() = token.literal
	
	override fun string(): String {
		var infoString = tokenLiteral() + " " + name.string() + " = "
		infoString += value?.string()
		infoString += ";"
		
		return infoString
	}
}