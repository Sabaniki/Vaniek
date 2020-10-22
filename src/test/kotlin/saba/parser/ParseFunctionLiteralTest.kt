package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.FunctionLiteral
import saba.ast.Identifier
import saba.ast.IntegerLiteral
import saba.ast.expresssion.Expression
import saba.ast.expresssion.IfExpression
import saba.ast.expresssion.InfixExpression
import saba.ast.statement.ExpressionStatement
import saba.lexer.Lexer

class ParseFunctionLiteralTest : ShouldSpec({
    val input = "fn(x, y) { x + y; }"
    val lexer = Lexer(input)
    val parser = Parser(lexer)
    val program = parser.parseProgram()
    val statement = program.statements[0] as? ExpressionStatement
    val expression = statement?.expression as? FunctionLiteral
    val functionLiteral = expression
    val expressionStatement = functionLiteral?.body?.statements?.get(0) as? ExpressionStatement

    fun testIntegerLiteral(expression: Expression?, value: Int, i: Int, LR: String) {
        val integerLiteral = expression as? IntegerLiteral
        "${i}番目の${LR}辺のexpressionはIntegerLiteral"{ (integerLiteral == null) shouldBe false }
        "${i}番目の${LR}辺のintegerLiteral.valueと期待されるvalueは等しい"{ integerLiteral?.value shouldBe value }
        "${i}番目の${LR}辺のintegerLiteral.tokenLiteral()はvalue.toString()と等しい"{ integerLiteral?.tokenLiteral() shouldBe value.toString() }
    }

    fun testIdentifier(expression: Expression?, value: String, i: Int, LR: String) {
        val identifier = expression as? Identifier
        "${i}番目の${LR}辺のexpressionはIdentifier"{ (identifier == null) shouldBe false }
        "${i}番目の${LR}辺のIdentifier.valueと期待されるvalueは等しい"{ identifier?.value shouldBe value }
        "${i}番目の${LR}辺のIdentifier.tokenLiteral()はvalue.toString()と等しい"{ identifier?.tokenLiteral() shouldBe value }
    }

    fun testBooleanLiteral(expression: Expression?, value: Boolean, i: Int, LR: String) {
        val boolean = expression as? saba.ast.Boolean
        "${i}番目の${LR}辺のexpressionはBoolean"{ (boolean == null) shouldBe false }
        "${i}番目の${LR}辺のBoolean.valueと期待されるvalueは等しい"{ boolean?.value shouldBe value }
        "${i}番目の${LR}辺のBoolean.tokenLiteral()はvalue.toString()と等しい"{ boolean?.tokenLiteral() shouldBe value.toString() }
    }

    fun testLiteralExpression(expression: Expression?, expected: Any, i: Int, LR: String) {
        when (expected::class.java.kotlin) {
            Int::class.java.kotlin -> testIntegerLiteral(expression, expected as Int, i, LR)
            String::class.java.kotlin -> testIdentifier(expression, expected as String, i, LR)
            Boolean::class.java.kotlin -> testBooleanLiteral(expression, expected as Boolean, i, LR)
            else -> throw Exception("arienn!ww")
        }
    }

    fun testInfixExpression(expression: Expression?, left: Any, operator: String, right: Any, i: Int) {
        val infixExpression = expression as? InfixExpression
        "${i}番目のexpressionはInfixExpression"{ (infixExpression == null) shouldBe false }
        testLiteralExpression(infixExpression?.leftExpression, left, i, "左")
        "${i}番目のoperatorは$operator"{
            infixExpression?.operator shouldBe operator
        }
        testLiteralExpression(infixExpression?.rightExpression, right, i, "右")
    }

    "parserErrorの数はゼロ" { parser.errors.size shouldBe 0 }
    for ((i, error) in parser.errors.withIndex()) {
        "parserError[${i}]の内容は空" {
            error shouldBe ""
        }
    }
    "program.statements.sizeは1" { program.statements.size shouldBe 1 }
    "statementはExpresionStatement" { (statement == null) shouldBe false }
    "expressionはFunctionLiteral" { (expression == null) shouldBe false }
    "parametersは2" { functionLiteral?.parameters?.size shouldBe 2 }
    testLiteralExpression(functionLiteral?.parameters?.get(0), "x", 1, "L" )
    testLiteralExpression(functionLiteral?.parameters?.get(1), "y", 2, "R" )
    "statementsの数は1" { functionLiteral?.body?.statements?.size shouldBe 1 }
    "body-statementはExpresionStatement" { (expressionStatement == null) shouldBe false }
    testInfixExpression(expressionStatement?.expression, "x", "+", "y", 1)
})
