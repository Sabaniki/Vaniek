package saba.ast

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.ast.statement.LetStatement
import saba.token.Token
import saba.token.TokenType

class LetStatementTest : ShouldSpec({
	"let文のastを作れているかどうか" {
		val program = Program(
			statements = listOf(
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
						value = "myVar"
					),
					value = Identifier(
						token = Token(
							type = TokenType.IDENT,
							literal = "anotherVar"
						),
						value = "anotherVar"
					)
				)
			)
		)
		
		program.string() shouldBe "let myVar = anotherVar;"
	}
})