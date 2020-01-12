package saba.parser

import saba.lexer.Lexer
import saba.token.Token
import saba.token.TokenType
import saba.ast.Expression
import saba.ast.Identifier
import saba.ast.Program
import saba.ast.statement.LetStatement
import saba.ast.statement.ReturnStatement
import saba.ast.statement.Statement

class Parser(val lexer: Lexer) {
	var currentToken: Token? = null
	var peekToken: Token? = null
	val prefixParseFns = mutableMapOf<TokenType, () -> Expression>()
	val infixParseFns = mutableMapOf<TokenType, (Expression) -> Expression>()
	val errors = mutableListOf<String>()
	
	init {  // 2つトークンを読み込む。currentTokenとpeekTokenの両方がセットされる。
		repeat(2) { nextToken() }
	}
	
	fun nextToken() {
		currentToken = peekToken
		peekToken = lexer.nextToken()
	}
	
	fun parseProgram(): Program {
		val program = Program(mutableListOf())
		while (currentTokenIs(TokenType.EOF)) {
			val statement = parseStatement()
			if (statement != null) program.statements.plus(statement)
			nextToken()
		}
		return program
	}
	
	fun parseStatement(): Statement? {
		return when (currentToken?.type) {
			TokenType.LET -> parseLetStatement()
			TokenType.RETURN -> parseReturnStatement()
			else -> null
		}
	}
	
	private fun parseReturnStatement(): Statement {
		nextToken()
		
		// TODO: セミコロンに遭遇するまで式を読み飛ばしてしまっている
		while (currentTokenIs(TokenType.SEMICOLON)) nextToken()
		
		return ReturnStatement(
			token = currentToken ?: Token(TokenType.ILLEGAL, ""),
			returnValue = null  // TODO: 本当はここにちゃんとした値が入る
		)
	}
	
	private fun parseLetStatement(): Statement? {
		if (!expectPeek(TokenType.IDENT)) return null
		if (!expectPeek(TokenType.ASSIGN)) return null
		
		// TODO: セミコロンに遭遇するまで式を読み飛ばしてしまっている
		while (currentTokenIs(TokenType.SEMICOLON)) nextToken()
		
		return LetStatement(
			token = currentToken ?: Token(TokenType.ILLEGAL, ""),
			name = Identifier(
				token = currentToken ?: Token(TokenType.ILLEGAL, ""),
				value = currentToken?.literal ?: ""
			),
			value = null    // TODO: 本当はここにちゃんとした値が入る
		)
	}
	
	fun currentTokenIs(tokenType: TokenType) = currentToken?.type == tokenType
	
	fun peekTokenIs(tokenType: TokenType) = peekToken?.type == tokenType
	
	fun expectPeek(tokenType: TokenType) = if (peekTokenIs(tokenType)) {
		nextToken()
		true
	}
	else {
		peekError(tokenType)
		false
	}
	
	fun peekError(tokenType: TokenType){
		val errorMessage = "expected next token to be $tokenType, got ${peekToken?.type} instead"
		errors.add(errorMessage)
	}
	
	fun registerPrefix(tokenType: TokenType, function: () -> Expression) {
		prefixParseFns[tokenType] = function
	}
	
	fun registerInfix(tokenType: TokenType, function: (Expression) -> Expression) {
		infixParseFns[tokenType] = function
	}
}