package saba.ast.expresssion

import saba.token.Token

class InfixExpression(
	val token: Token,   // 演算子トークン、例えば'+'
	val leftExpression: Expression?,
	val operator: String,
	val rightExpression: Expression?
) : Expression {
	override fun tokenLiteral() = token.literal
	
	override fun toString() = "($leftExpression $operator $rightExpression)"
}
