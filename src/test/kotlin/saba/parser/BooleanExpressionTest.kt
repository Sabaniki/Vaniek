package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.Boolean
import saba.ast.IntegerLiteral
import saba.ast.statement.ExpressionStatement
import saba.lexer.Lexer

class BooleanExpressionTest : ShouldSpec({
	val input = "false;"
	val lexer = Lexer(input)
	val parser = Parser(lexer)
	val program = parser.parseProgram()
	val statement = program.statements[0] as? ExpressionStatement
	val expression = statement?.expression as? Boolean
	val boolean = expression
	
	"parserErrorの数はゼロ" { parser.errors.size shouldBe 0 }
	for ((i, error) in parser.errors.withIndex()) {
		"parserError[${i}]の内容は空" {
			error shouldBe ""
		}
	}
	"program.statements.sizeは1" { program.statements.size shouldBe 1 }
	
	"statementはExpresionStatement" { (statement == null) shouldBe false }
	"expressionはIntegerLiteral" { (expression == null) shouldBe false }
	
	"ident.valueは true" { boolean?.value shouldBe true }
	"ident.tokenLiteralは 'true'" { boolean?.tokenLiteral() shouldBe "true" }
})
