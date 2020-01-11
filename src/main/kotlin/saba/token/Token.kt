package saba.token

class Token(val type: TokenType, val literal: String)

object TokenKeywords {
	private val keywords = mapOf(
		"fn" to TokenType.FUNCTION,
		"let" to TokenType.LET,
		"true" to TokenType.TRUE,
		"false" to TokenType.FALSE,
		"if" to TokenType.IF,
		"else" to TokenType.ELSE,
		"return" to TokenType.RETURN
	)
	
	fun lookUpIdent(ident: String): TokenType {
		return keywords[ident] ?: TokenType.IDENT
	}
}