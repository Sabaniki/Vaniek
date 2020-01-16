package saba.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.statement.LetStatement
import saba.token.Token
import saba.token.TokenType

class LetStatementTest : ShouldSpec({
	"let文のastを作れているかどうか" {
		val program = Program(
			statements = mutableListOf(
				LetStatement(
					token = Token(
						type = TokenType.LET,
						literal = "let"
					),
					name = Identifier(
						token = Token(
							type = TokenType.IDENT,
							literal = "myVar"
						),
						value = "myVar",
						type = "testType"
					),
					value = Identifier(
						token = Token(
							type = TokenType.IDENT,
							literal = "anotherVar"
						),
						value = "anotherVar",
						type = "testType2"
					)
				)
			)
		)
		program.statements.add(
			LetStatement(
				token = Token(
					type = TokenType.LET,
					literal = "let"
				),
				name = Identifier(
					token = Token(
						type = TokenType.IDENT,
						literal = "myVar2"
					),
					value = "myVar2",
					type = "testType"
				),
				value = Identifier(
					token = Token(
						type = TokenType.IDENT,
						literal = "anotherVar2"
					),
					value = "anotherVar2",
					type = "testType2"
				)
			)
		)
		"programの文字列はlet myVar = anotherVar;let myVar2 = anotherVar2;である"{ program.toString() shouldBe "let myVar = anotherVar;let myVar2 = anotherVar2;" }
		"program.statements.sizeは2"{ program.statements.size shouldBe 2 }
	}
})