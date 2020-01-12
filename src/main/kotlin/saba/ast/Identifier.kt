package saba.ast

import saba.token.Token

class Identifier(
	val token: Token,
	val value: String
): Expression, Node {
	override fun expressionNode() {}
	
	override fun tokenLiteral() = token.literal
	
	override fun string() = value
}