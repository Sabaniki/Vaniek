package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.IntegerLiteral
import saba.ast.expresssion.Expression
import saba.lexer.Lexer

class OperatorPrecedenceParsingTest : ShouldSpec({
	class OperatorTest(
		val input: String,
		val expected: String
	)
	
	fun testIntegerLiteral(term: Term, expression: Expression?, value: Int, i: Int) {
		val integerLiteral = expression as? IntegerLiteral
		val side = if (term == Term.L) "左辺" else "右辺"
		"${i}番目の${side}のexpressionはIntegerLiteral"{ (integerLiteral == null) shouldBe false }
		"${i}番目の${side}のintegerLiteral.valueと期待されるvalueは等しい"{ integerLiteral?.value shouldBe value }
		"${i}番目の${side}のintegerLiteral.tokenLiteral()はvalue.toString()と等しい"{ integerLiteral?.tokenLiteral() shouldBe value.toString() }
	}
	
	val operatorTests = listOf(
		OperatorTest("-a * b", "((-a) * b)"),
		OperatorTest("!-a", "(!(-a))"),
		OperatorTest("a + b + c", "((a + b) + c)"),
		OperatorTest("a + b - c", "((a + b) - c)"),
		OperatorTest("a * b * c", "((a * b) * c)"),
		OperatorTest("a * b / c", "((a * b) / c)"),
		OperatorTest("a + b / c", "(a + (b / c))"),
		OperatorTest("a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"),
		OperatorTest("3 + 4; -5 * 5", "(3 + 4)((-5) * 5)"),
		OperatorTest("5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))"),
		OperatorTest("5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))"),
		OperatorTest("3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))")
	
	)
	for ((i, operatorTest) in operatorTests.withIndex()) {
		val input = operatorTest.input
		val lexer = Lexer(input)
		val parser = Parser(lexer)
		val program = parser.parseProgram()
		
		"${i}番目のparserErrorの数はゼロ" { parser.errors.size shouldBe 0 }
		for ((j, error) in parser.errors.withIndex()) {
			"${i}番目のparserError[${j}]の内容は空" {
				error shouldBe ""
			}
		}
		"${i}番目の構文解析結果は${operatorTest.expected}" { program.toString() shouldBe operatorTest.expected }
	}
})

