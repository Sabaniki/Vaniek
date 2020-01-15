package saba.ast.expresssion

import saba.token.Token

class InfixExpression(
	val token: Token,   // 演算子トークン、例えば'+'
	val leftValue: Expression,
	val operator: String,
	val rightValue: Expression
): Expression {
	override fun tokenLiteral() = token.literal
	
	override fun string() = "(" + leftValue.string() + operator + rightValue.string() + ")"
}
