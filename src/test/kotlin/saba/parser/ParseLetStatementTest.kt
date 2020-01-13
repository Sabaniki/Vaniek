package saba.parser

import arrow.core.Left
import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.statement.LetStatement
import saba.ast.statement.Statement
import saba.lexer.Lexer

class ParseLetStatementTest : ShouldSpec({
	
	val input = """
	let x = 5;
	let y = 10;
	let foobar = 838383;
	"""
	val lexer = Lexer(input)
	val parser = Parser(lexer)
	val program = parser.parseProgram()
	
	"parserErrorの数はゼロ" { parser.errors.isEmpty() shouldBe true }
	"program.statements.sizeは3" { program.statements.size shouldBe 3 }
	
	val tests = listOf(
		Pair("x", program.statements[0]),
		Pair("y", program.statements[1]),
		Pair("foobar", program.statements[2])
	)
	for ((i, test) in tests.withIndex()) {
		"${i}番目のtokenLiteralは 'let' "{ test.second.tokenLiteral() shouldBe "let" }
		"${i}番目のstatementのname.valueはPairの片割れに等しい"{ (test.second as LetStatement).name.value shouldBe test.first }
		"${i}番目のstatementのname.tokenLiteral()はPairの片割れに等しい"{ (test.second as LetStatement).name.tokenLiteral() shouldBe test.first }
	}
})


