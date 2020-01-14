package saba.ast.expresssion

import saba.token.Token

class PrefixExpression(
	val token: Token,   //　前置トークン、例えば'!'
	val operator: String,
	val right: Expression?
) : Expression {
	override fun tokenLiteral() = token.literal
	
	override fun string() = "(" + operator + right?.string() + ")"
}
