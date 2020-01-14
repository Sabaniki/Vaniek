package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.IntegerLiteral
import saba.ast.expresssion.Expression
import saba.ast.expresssion.PrefixExpression
import saba.ast.statement.ExpressionStatement
import saba.lexer.Lexer

class ParsingPrefixExpressionsTest : ShouldSpec({
	
	class PrefixTest(
		val input: String,
		val operator: String,
		val integerValue: Int
	)
	
	fun testIntegerLiteral(expression: Expression?, value: Int, i: Int) {
		val integerLiteral = expression as? IntegerLiteral
		"${i}番目のexpressionはIntegerLiteral"{ (integerLiteral == null) shouldBe false }
		"${i}番目のintegerLiteral.valueと期待されるvalueは等しい"{ integerLiteral?.value shouldBe value }
		"${i}番目のintegerLiteral.tokenLiteral()はvalue.toString()と等しい"{ integerLiteral?.tokenLiteral() shouldBe value.toString() }
	}
	
	val prefixTests = listOf(
		PrefixTest("!5", "!", 5),
		PrefixTest("-15", "-", 15)
	)
	for ((i, prefixTest) in prefixTests.withIndex()) {
		val input = prefixTest.input
		val lexer = Lexer(input)
		val parser = Parser(lexer)
		val program = parser.parseProgram()
		val statement = program.statements[0] as? ExpressionStatement
		val expression = statement?.expression as? PrefixExpression
		val prefixExpression = expression
		
		"${i}番目のparserErrorの数はゼロ" { parser.errors.size shouldBe 0 }
		for ((j, error) in parser.errors.withIndex()) {
			"${i}番目のparserError[${j}]の内容は空" {
				error shouldBe ""
			}
		}
		"${i}番目のprogram.statements.sizeは1" { program.statements.size shouldBe 1 }
		
		"${i}番目のstatementはExpresionStatement" { (statement == null) shouldBe false }
		"${i}番目のexpressionはPrefixExpression" { (expression == null) shouldBe false }
		
		"${i}番目のprefixExpressionのオペレータは${prefixTest.operator}" {
			prefixExpression?.operator shouldBe prefixTest.operator
		}
		
		testIntegerLiteral(prefixExpression?.right, prefixTest.integerValue, i)
	}
})

