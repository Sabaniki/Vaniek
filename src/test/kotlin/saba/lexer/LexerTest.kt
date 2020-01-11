package saba.lexer

import org.junit.Test
import saba.token.TokenType
import kotlin.test.assertEquals

class LexerTest {

}

fun main() {
	val lexer = Lexer("let a = 10;")
//	val token1 = lexer.nextToken()
//	val token2 = lexer.nextToken()
//	val token3 = lexer.nextToken()
//	val token4 = lexer.nextToken()
//	val token5 = lexer.nextToken()
	// lexer.nextToken()
	var type = TokenType.ILLEGAL
	while (type != TokenType.EOF) {
		val token = lexer.nextToken()
		type = token.type
		println("Type: ${token.type}, Literal: ${token.literal}")
	}
}