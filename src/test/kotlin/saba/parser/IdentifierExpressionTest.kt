package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.Identifier
import saba.ast.statement.ExpressionStatement
import saba.lexer.Lexer

class IdentifierExpressionTest : ShouldSpec({
	val input = "foobar;"
	val lexer = Lexer(input)
	val parser = Parser(lexer)
	val program = parser.parseProgram()
	val statement = program.statements[0] as? ExpressionStatement
	val expression = statement?.expression as? Identifier
	val ident = expression
	
	"parserErrorの数はゼロ" { parser.errors.isEmpty() shouldBe true }
	"program.statements.sizeは1" { program.statements.size shouldBe 1 }
	
	"statementはExpresionStatement" { (statement == null) shouldBe false }
	"expressionはIdentifier" { (expression == null) shouldBe false }
	
	"ident.valueは 'foobar'" { ident?.value shouldBe "foobar" }
	"ident.tokenLiteralは 'foobar'" { ident?.tokenLiteral() shouldBe "foobar" }
})