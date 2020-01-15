package saba.ast.statement

import saba.ast.expresssion.Expression
import saba.ast.Node
import saba.token.Token

class ExpressionStatement(
	val token: Token,   // 式の最初のトークン
	val expression: Expression?
) : Node, Statement {
	override fun tokenLiteral() = token.literal
	
	override fun toString() = expression?.toString() ?: ""
}