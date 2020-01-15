package saba.ast.statement

import saba.ast.expresssion.Expression
import saba.ast.Node
import saba.token.Token

class ReturnStatement(
	val token: Token,
	val returnValue: Expression?
): Node, Statement {
	override fun tokenLiteral() = token.literal
	
	override fun toString(): String {
		var infoString = tokenLiteral() + " "
		
		infoString += returnValue?.toString()
		infoString += ";"
		
		return infoString
	}
}