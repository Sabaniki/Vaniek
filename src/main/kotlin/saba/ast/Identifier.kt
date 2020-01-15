package saba.ast

import saba.ast.expresssion.Expression
import saba.token.Token

class Identifier(
	val token: Token,
	val value: String
): Expression, Node {
	
	override fun tokenLiteral() = token.literal
	
	override fun toString() = value
}