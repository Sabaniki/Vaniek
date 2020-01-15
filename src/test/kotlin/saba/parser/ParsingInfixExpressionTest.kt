package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.IntegerLiteral
import saba.ast.expresssion.Expression
import saba.ast.expresssion.InfixExpression
import saba.ast.statement.ExpressionStatement
import saba.lexer.Lexer

enum class Term { L, R }


class ParsingInfixExpressionTest : ShouldSpec({
	class InfixTest(
		val input: String,
		val leftValue: Int,
		val operator: String,
		val rightValue: Int
	)
	
	fun testIntegerLiteral(term: Term, expression: Expression?, value: Int, i: Int) {
		val integerLiteral = expression as? IntegerLiteral
		val side = if (term == Term.L) "左辺" else "右辺"
		"${i}番目の${side}のexpressionはIntegerLiteral"{ (integerLiteral == null) shouldBe false }
		"${i}番目の${side}のintegerLiteral.valueと期待されるvalueは等しい"{ integerLiteral?.value shouldBe value }
		"${i}番目の${side}のintegerLiteral.tokenLiteral()はvalue.toString()と等しい"{ integerLiteral?.tokenLiteral() shouldBe value.toString() }
	}
	
	val infixTests = listOf(
		InfixTest("1 + 1;", 1, "+", 1),
		InfixTest("1 - 1;", 1, "-", 1),
		InfixTest("1 * 1;", 1, "*", 1),
		InfixTest("1 / 1;", 1, "/", 1),
		InfixTest("1 > 1;", 1, ">", 1),
		InfixTest("1 < 1;", 1, "<", 1),
		InfixTest("1 == 1;", 1, "==", 1),
		InfixTest("1 != 1;", 1, "!=", 1)
	)
	for ((i, infixTest) in infixTests.withIndex()) {
		val input = infixTest.input
		val lexer = Lexer(input)
		val parser = Parser(lexer)
		val program = parser.parseProgram()
		val statement = program.statements[0] as? ExpressionStatement
		val expression = statement?.expression as? InfixExpression
		val infixExpression = expression
		
		"${i}番目のparserErrorの数はゼロ" { parser.errors.size shouldBe 0 }
		for ((j, error) in parser.errors.withIndex()) {
			"${i}番目のparserError[${j}]の内容は空" {
				error shouldBe ""
			}
		}
		"${i}番目のprogram.statements.sizeは1" { program.statements.size shouldBe 1 }
		
		"${i}番目のstatementはExpresionStatement" { (statement == null) shouldBe false }
		"${i}番目のexpressionはinfixExpression" { (expression == null) shouldBe false }
		
		testIntegerLiteral(Term.L, infixExpression?.leftExpression, infixTest.leftValue, i)
		"${i}番目のinfixExpressionのオペレータは${infixTest.operator}" {
			infixExpression?.operator shouldBe infixTest.operator
		}
		testIntegerLiteral(Term.R, infixExpression?.rightExpression, infixTest.rightValue, i)
		
	}
})

