package saba.ast

import saba.token.Token

class IntegerLiteral(
	val token: Token,
	val value: Int
): Expression, Node {
	
	override fun tokenLiteral() = token.literal
	
	override fun string() = token.literal
	
}
