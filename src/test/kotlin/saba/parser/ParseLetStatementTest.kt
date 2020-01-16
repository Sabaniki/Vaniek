package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.statement.LetStatement
import saba.lexer.Lexer

class ParseLetStatementTest : ShouldSpec({
	
	class TestSet(
		val nameValue: String,
		val type: String
	)
	
	
	val input = """
	let x: Int = 5;
	let y: Int = 10;
	let foobar: Int = 838383;
	"""
	val lexer = Lexer(input)
	val parser = Parser(lexer)
	val program = parser.parseProgram()
	
	"parserErrorの数はゼロ" { parser.errors.isEmpty() shouldBe true }
	"program.statements.sizeは3" { program.statements.size shouldBe 3 }
	
	val tests = listOf(
		TestSet("x", "Int"),
		TestSet("y", "Int"),
		TestSet("foobar", "Int")
	)
	for ((i, test) in tests.withIndex()) {
		"${i}番目のtokenLiteralは 'let' "{ program.statements[i].tokenLiteral() shouldBe "let" }
		"${i}番目のstatementのname.valueはPairの片割れに等しい"{ (
				program.statements[i] as? LetStatement)?.name?.value shouldBe test.nameValue
		}
		"${i}番目のstatementのname.tokenLiteral()はPairの片割れに等しい"{
			(program.statements[i] as? LetStatement)?.name?.tokenLiteral() shouldBe test.nameValue
		}
	}
})


