package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import io.kotlintest.specs.ShouldSpec
import io.kotlintest.specs.WordSpec
import saba.ast.Identifier
import saba.ast.IntegerLiteral
import saba.ast.expresssion.Expression
import saba.ast.expresssion.InfixExpression
import saba.ast.expresssion.PrefixExpression
import saba.ast.statement.ExpressionStatement
import saba.lexer.Lexer
import kotlin.Exception

class InfixExpressionTest : ShouldSpec({
	fun testIntegerLiteral(expression: Expression?, value: Int, i: Int, LR: String) {
		val integerLiteral = expression as? IntegerLiteral
		"${i}番目の${LR}辺のexpressionはIntegerLiteral"{ (integerLiteral == null) shouldBe false }
		"${i}番目の${LR}辺のintegerLiteral.valueと期待されるvalueは等しい"{ integerLiteral?.value shouldBe value }
		"${i}番目の${LR}辺のintegerLiteral.tokenLiteral()はvalue.toString()と等しい"{ integerLiteral?.tokenLiteral() shouldBe value.toString() }
	}
	
	fun testIdentifier(expression: Expression?, value: String, i: Int, LR: String) {
		val identifier = expression as? Identifier
		"${i}番目の${LR}辺のexpressionはIntegerLiteral"{ (identifier == null) shouldBe false }
		"${i}番目の${LR}辺のintegerLiteral.valueと期待されるvalueは等しい"{ identifier?.value shouldBe value }
		"${i}番目の${LR}辺のintegerLiteral.tokenLiteral()はvalue.toString()と等しい"{ identifier?.tokenLiteral() shouldBe value }
	}
	
	fun testBooleanLiteral(expression: Expression?, value: Boolean, i: Int, LR: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
	
	class InfixTest(
		val input: String,
		val leftValue: Any,
		val operator: String,
		val rightValue: Any
	)
	
	val infixTests = listOf(
		InfixTest("5 + 5;", 5, "+", 5),
		InfixTest("5 - 5;", 5, "-", 5),
		InfixTest("5 * 5;", 5, "*", 5),
		InfixTest("5 / 5;", 5, "/", 5),
		InfixTest("5 > 5;", 5, ">", 5),
		InfixTest("5 < 5;", 5, "<", 5),
		InfixTest("5 == 5;", 5, "==", 5),
		InfixTest("5 != 5;", 5, "!=", 5),
		InfixTest("foo + bar;", "foo", "+", "bar"),
		InfixTest("foo - bar;", "foo", "-", "bar"),
		InfixTest("foo * bar;", "foo", "*", "bar"),
		InfixTest("foo / bar;", "foo", "/", "bar"),
		InfixTest("foo > bar;", "foo", ">", "bar"),
		InfixTest("foo < bar;", "foo", "<", "bar"),
		InfixTest("foo == bar;", "foo", "==", "bar"),
		InfixTest("foo != bar;", "foo", "!=", "bar")
//		InfixTest("true == true;", true, "==", true),
//		InfixTest("true != false;", true, "!=", false),
//		InfixTest("false = false;", false, "==", false)
	)
	
	for ((i, infixTest) in infixTests.withIndex()) {
		val input = infixTest.input
		val lexer = Lexer(input)
		val parser = Parser(lexer)
		val program = parser.parseProgram()
		val statement = program.statements[0] as? ExpressionStatement
		val infixExpression = statement?.expression as? InfixExpression
		
		"${i}番目のparserErrorの数はゼロ" { parser.errors.size shouldBe 0 }
		for ((j, error) in parser.errors.withIndex()) {
			"${i}番目のparserError[${j}]の内容は空" {
				error shouldBe ""
			}
		}
		"${i}番目のprogram.statements.sizeは1" { program.statements.size shouldBe 1 }
		
		"${i}番目のstatementはExpresionStatement" { (statement == null) shouldBe false }
		
		testInfixExpression(statement?.expression, infixTest.leftValue, infixTest.operator, infixTest.rightValue, i)
	}
})
