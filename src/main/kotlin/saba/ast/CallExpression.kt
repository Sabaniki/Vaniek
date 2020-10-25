package saba.ast

import saba.ast.expresssion.Expression
import saba.token.Token

class CallExpression(
    val token: Token, // '(' トークン
    val function: Expression, // Identifier または FunctionLiteral
    val arguments: List<Expression>
):Expression, Node {
    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        var string = ""
        val args = mutableListOf<String>()
        arguments.forEach{args.add(it.toString())}
        string += function.toString()
        string += "(" + args.toTypedArray().joinToString() + ")"
        return string
    }
}