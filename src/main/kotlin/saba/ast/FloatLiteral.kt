package saba.ast

import saba.ast.expresssion.Expression
import saba.token.Token

class FloatLiteral(
	val token: Token,
	val value: Float
): Expression, Node {
	
	override fun tokenLiteral() = token.literal
	
	override fun toString() = token.literal
	
}
