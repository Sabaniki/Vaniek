package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.lexer.Lexer

class ParseReturnStatementTest: ShouldSpec({
	val input = """
	return 5;
	return 10;
	return 993322;
	"""
	val lexer = Lexer(input)
	val parser = Parser(lexer)
	val program = parser.parseProgram()
	
	"parserErrorの数はゼロ" { parser.errors.isEmpty() shouldBe true }
	"program.statements.sizeは3" { program.statements.size shouldBe 3 }
	
//	val tests = listOf(
//		Pair("x", program.statements[0]),
//		Pair("y", program.statements[1]),
//		Pair("foobar", program.statements[2])
//	)
	for ((i, statement) in program.statements.withIndex()) {
		"${i}番目のtokenLiteralは 'return' "{ statement.tokenLiteral() shouldBe "return" }
	}
})