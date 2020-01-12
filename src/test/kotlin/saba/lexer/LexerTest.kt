package saba.lexer

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec
import saba.token.Token
import saba.token.TokenType

class LexerTest : ShouldSpec(
	{
		val input = """
				let five = 5;
				let ten = 10.00;

				let add = fn(x, y) {
					x + y;
				};

				let result = add(five, ten);

				!-/*5;
				5 < 10 > 5;

				if (5 < 10) {
					return true;
				}
				else {
					return false;
				}

				10 == 10;
				10 != 9;
				let a = 10.0;
		"""
		val lexer = Lexer(input)
		"TypeとLiteralが合致するかどうか" {
			val tests = listOf(
				// let five = 5;
				Token(TokenType.LET, "let"),
				Token(TokenType.IDENT, "five"),
				Token(TokenType.ASSIGN, "="),
				Token(TokenType.INT, "5"),
				Token(TokenType.SEMICOLON, ";"),
				
				// let ten = 10.00;
				Token(TokenType.LET, "let"),
				Token(TokenType.IDENT, "ten"),
				Token(TokenType.ASSIGN, "="),
				Token(TokenType.FLOAT, "10.00"),
				Token(TokenType.SEMICOLON, ";"),
				
				//	let add = fn(x, y) {
				//       x + y;
				//  };
				Token(TokenType.LET, "let"),
				Token(TokenType.IDENT, "add"),
				Token(TokenType.ASSIGN, "="),
				Token(TokenType.FUNCTION, "fn"),
				Token(TokenType.LPAREN, "("),
				Token(TokenType.IDENT, "x"),
				Token(TokenType.COMMA, ","),
				Token(TokenType.IDENT, "y"),
				Token(TokenType.RPAREN, ")"),
				Token(TokenType.LBRACE, "{"),
				Token(TokenType.IDENT, "x"),
				Token(TokenType.PLUS, "+"),
				Token(TokenType.IDENT, "y"),
				Token(TokenType.SEMICOLON, ";"),
				Token(TokenType.RBRACE, "}"),
				Token(TokenType.SEMICOLON, ";"),
				
				// let result = add(five, ten);
				Token(TokenType.LET, "let"),
				Token(TokenType.IDENT, "result"),
				Token(TokenType.ASSIGN, "="),
				Token(TokenType.IDENT, "add"),
				Token(TokenType.LPAREN, "("),
				Token(TokenType.IDENT, "five"),
				Token(TokenType.COMMA, ","),
				Token(TokenType.IDENT, "ten"),
				Token(TokenType.RPAREN, ")"),
				Token(TokenType.SEMICOLON, ";"),
				
				// !-/*5;
				Token(TokenType.BANG, "!"),
				Token(TokenType.MINUS, "-"),
				Token(TokenType.SLASH, "/"),
				Token(TokenType.ASTERISK, "*"),
				Token(TokenType.INT, "5"),
				Token(TokenType.SEMICOLON, ";"),
				
				
				// 5 < 10 > 5;
				Token(TokenType.INT, "5"),
				Token(TokenType.LT, "<"),
				Token(TokenType.INT, "10"),
				Token(TokenType.GT, ">"),
				Token(TokenType.INT, "5"),
				Token(TokenType.SEMICOLON, ";"),
				
				// if (5 < 10) {
				//		return true;
				//	}
				Token(TokenType.IF, "if"),
				Token(TokenType.LPAREN, "("),
				Token(TokenType.INT, "5"),
				Token(TokenType.LT, "<"),
				Token(TokenType.INT, "10"),
				Token(TokenType.RPAREN, ")"),
				Token(TokenType.LBRACE, "{"),
				Token(TokenType.RETURN, "return"),
				Token(TokenType.TRUE, "true"),
				Token(TokenType.SEMICOLON, ";"),
				Token(TokenType.RBRACE, "}"),
				
				// 	else {
				//		return false;
				//	}
				Token(TokenType.ELSE, "else"),
				Token(TokenType.LBRACE, "{"),
				Token(TokenType.RETURN, "return"),
				Token(TokenType.FALSE, "false"),
				Token(TokenType.SEMICOLON, ";"),
				Token(TokenType.RBRACE, "}"),
				
				// 10 == 10;
				Token(TokenType.INT, "10"),
				Token(TokenType.EQ, "=="),
				Token(TokenType.INT, "10"),
				Token(TokenType.SEMICOLON, ";"),
				
				// 10 != 9;
				Token(TokenType.INT, "10"),
				Token(TokenType.NOT_EQ, "!="),
				Token(TokenType.INT, "9"),
				Token(TokenType.SEMICOLON, ";"),
			
				// let a = 10..0
				Token(TokenType.LET, "let"),
				Token(TokenType.IDENT, "a"),
				Token(TokenType.ASSIGN, "="),
				Token(TokenType.FLOAT, "10.0"),
				Token(TokenType.SEMICOLON, ";")
			)
			
			for ((i, test) in tests.withIndex()) {
				val token = lexer.nextToken()
				should("$i 番目のTypeは") {
					token.type shouldBe test.type
				}
				should("$i 番目のLiteralは") {
					token.literal shouldBe test.literal
				}
			}
		}
	}
)