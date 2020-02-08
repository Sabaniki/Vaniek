package saba.ast

import saba.ast.expresssion.Expression
import saba.token.Token

class Boolean (
	val token: Token,
	val value: kotlin.Boolean
): Expression {
	override fun tokenLiteral() = token.literal
	
	override fun toString() = token.literal
}