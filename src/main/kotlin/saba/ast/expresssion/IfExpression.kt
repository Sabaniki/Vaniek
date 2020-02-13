package saba.ast.expresssion

import saba.ast.statement.BlockStatement
import saba.token.Token

class IfExpression(
	val token: Token,
	val condition: Expression,
	val consequence: BlockStatement,
	val alternative: BlockStatement?
): Expression {
	override fun tokenLiteral() = token.literal
	
	override fun toString(): String {
		var string = "if($condition) $consequence"
		if (alternative != null) string += "else $alternative"
		return string
	}
}