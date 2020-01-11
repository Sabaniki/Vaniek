package saba.repl

import saba.lexer.Lexer
import saba.token.TokenType

class Repl {
	private val prompt = ">> "
	fun start() {
		while (true) {
			print(prompt)
			val line = readLine()
			if (line == "!!exit" || line == null) return
			val lexer = Lexer(line)
			
			var type = TokenType.ILLEGAL
			while (true) {
				val token = lexer.nextToken()
				type = token.type
				if (type == TokenType.EOF) break
				println("Type: ${token.type}, Literal: \"${token.literal}\"")
			}
		}
	}
}