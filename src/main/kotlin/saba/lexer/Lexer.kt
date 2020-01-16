package saba.lexer

import saba.token.TokenType
import saba.token.Token
import saba.token.TokenKeywords

class Lexer(private val input: String) {
	var position = 0
	var readPosition = 0
	var ch: Char = Char.MIN_VALUE
	var beforeToken = Token(TokenType.ILLEGAL, "")
	
	init {
		readChar()
	}
	
	private fun readChar() {
		ch = if (readPosition >= input.length) {
			Char.MIN_VALUE
		}
		else {
			input[readPosition]
		}
		
		position = readPosition
		readPosition++
	}
	
	private fun readLiteral(): String {
		val startPosition = position
		while (ch.isLetter()) { // TODO: 変数の一部などにアンダースコアがあっちゃだめ
			readChar()
		}
		return input.slice(startPosition until position)
	}
	
	fun readIdentifier(): Pair<TokenType, String> {
		val literal = readLiteral()
		return TokenKeywords.lookUpIdent(literal) to literal
	}
	
	fun readType(): Pair<TokenType, String> {
		val literal = readLiteral()
		return TokenType.TYPE to literal
	}
	
	fun readNumber(): Pair<TokenType, String> {
		var tokenResult = TokenType.INT
		val startPosition = position
		
		while (ch.isDigit()) {
			readChar()
		}
		if (ch == '.') {
			readChar()
			if (ch == '.') {
				readChar()
				tokenResult = TokenType.ILLEGAL
			}
		}
		while (ch.isDigit()) {
			readChar()
			tokenResult = TokenType.FLOAT
		}
		val literal = input.slice(startPosition until position)
		
		return tokenResult to literal
	}
	
	fun peekChar(): Char {
		return if (readPosition >= input.length) {
			Char.MIN_VALUE
		}
		else {
			input[readPosition]
		}
	}
	
	fun makeTwoCharToken(tokenType: TokenType): Token {
		val startChar = ch
		readChar()
		val literal = startChar.toString() + ch.toString()
		return Token(tokenType, literal)
	}
	
	fun nextToken(): Token {
		skipWhiteSpace()
		
		val token = when (ch) {
			'=' ->
				if (peekChar() == '=') makeTwoCharToken(TokenType.EQ) else Token(TokenType.ASSIGN, ch.toString())
			'!' ->
				if (peekChar() == '=') makeTwoCharToken(TokenType.NOT_EQ) else Token(TokenType.BANG, ch.toString())
			'+' ->
				Token(TokenType.PLUS, ch.toString())
			'-' ->
				Token(TokenType.MINUS, ch.toString())
			'/' ->
				Token(TokenType.SLASH, ch.toString())
			'*' ->
				Token(TokenType.ASTERISK, ch.toString())
			'<' ->
				Token(TokenType.LT, ch.toString())
			'>' ->
				Token(TokenType.GT, ch.toString())
			';' ->
				Token(TokenType.SEMICOLON, ch.toString())
			':' ->
				Token(TokenType.COLON, ch.toString())
			'(' ->
				Token(TokenType.LPAREN, ch.toString())
			')' ->
				Token(TokenType.RPAREN, ch.toString())
			',' ->
				Token(TokenType.COMMA, ch.toString())
			'{' ->
				Token(TokenType.LBRACE, ch.toString())
			'}' ->
				Token(TokenType.RBRACE, ch.toString())
			Char.MIN_VALUE ->
				Token(TokenType.EOF, "")
			else ->
				when {
					ch.isLetter() -> {
						val (type, literal) =
							if (beforeToken.type == TokenType.COLON) readType()
							else readIdentifier()
						return Token(type, literal)
					}
					ch.isDigit() -> {
						val (type, literal) = readNumber()
						return Token(type, literal)
					}
					else -> Token(TokenType.ILLEGAL, ch.toString())
				}
		}
		readChar()
		beforeToken = token
		return token
	}
	
	fun skipWhiteSpace() {
		while (ch.isWhitespace()) readChar()
	}
}