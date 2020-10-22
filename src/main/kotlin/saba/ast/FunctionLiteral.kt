package saba.ast

import saba.ast.expresssion.Expression
import saba.ast.statement.BlockStatement
import saba.token.Token

class FunctionLiteral(
    val token: Token,
    val parameters: List<Identifier>,
    val body: BlockStatement
):Expression, Node {
    override fun tokenLiteral() = token.literal

    override fun toString(): String {
        var string = ""
        string += tokenLiteral() + "("
        parameters.forEach { string += ("$it, ") }
        if(parameters.isNotEmpty())string = string.dropLast(2)
        string += ")"
        string += body.toString()
        return string
    }
}