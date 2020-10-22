package saba

import org.junit.Test
import saba.ast.FunctionLiteral
import saba.ast.expresssion.IfExpression
import saba.ast.statement.ExpressionStatement
import saba.lexer.Lexer
import saba.parser.Parser
import kotlin.test.assertEquals

fun main() {
    val input = """fn() { x + y; }"""
    val lexer = Lexer(input)
    val parser = Parser(lexer)
    val program = parser.parseProgram()
    val statement = program.statements[0] as? ExpressionStatement
//     val literal = statement?.expression as? FunctionLiteral
    println(statement.toString())
}
