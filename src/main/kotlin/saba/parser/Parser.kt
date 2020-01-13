package saba.parser

import saba.lexer.Lexer
import saba.token.Token
import saba.token.TokenType
import saba.ast.Expression
import saba.ast.Identifier
import saba.ast.Program
import saba.ast.statement.ExpressionStatement
import saba.ast.statement.LetStatement
import saba.ast.statement.ReturnStatement
import saba.ast.statement.Statement

class Parser(val lexer: Lexer) {
	var currentToken: Token? = null
	var peekToken: Token? = null
	val prefixParseFns = mutableMapOf<TokenType, () -> Expression>()
	val infixParseFns = mutableMapOf<TokenType, (Expression) -> Expression>()
	val errors = mutableListOf<String>()
	
	init {
		// 2つトークンを読み込む。currentTokenとpeekTokenの両方がセットされる。
		repeat(2) { nextToken() }
	}
	
	private fun parseIdentifier(): () -> Expression = {
		Identifier(checkedCurrentToken(), checkedCurrentTokenLiteral())
	}
	
	fun nextToken() {
		currentToken = peekToken
		peekToken = lexer.nextToken()
	}
	
	fun parseProgram(): Program {
		val program = Program(mutableListOf())
		while (!currentTokenIs(TokenType.EOF)) {
			val statement = parseStatement()
			if (statement != null) program.statements.add(statement)
			nextToken()
		}
		return program
	}
	
	fun parseStatement(): Statement? {
		return when (currentToken?.type) {
			TokenType.LET -> parseLetStatement()
			TokenType.RETURN -> parseReturnStatement()
			else -> parseExpressionStatement()
		}
	}
	
	private fun parseExpressionStatement(): ExpressionStatement {
		val statement = ExpressionStatement(checkedCurrentToken(), parseExpression())
		
		if (peekTokenIs(TokenType.SEMICOLON)) nextToken()
		return statement
	}
	
	private fun parseExpression(): Expression? {
		val prefix = prefixParseFns[currentToken?.type] ?: return null
		return prefix()
	}
	
	private fun parseReturnStatement(): Statement {
		val returnStatement = ReturnStatement(
			checkedCurrentToken(),
			returnValue = null  // TODO: 本当はここにちゃんとした値が入る
		)
		nextToken()
		
		// TODO: セミコロンに遭遇するまで式を読み飛ばしてしまっている
		while (!currentTokenIs(TokenType.SEMICOLON)) nextToken()
		
		return returnStatement
	}
	
	private fun parseLetStatement(): Statement? {
		val token = currentToken
		
		if (!expectPeek(TokenType.IDENT)) return null
		
		val name = Identifier(checkedCurrentToken(), checkedCurrentTokenLiteral())
		
		if (!expectPeek(TokenType.ASSIGN)) return null
		
		val statement = LetStatement(
			token = token ?: Token(TokenType.ILLEGAL, ""),
			name = name,
			value = null    // TODO: 本当はここにちゃんとした値が入る
		)
		
		// TODO: セミコロンに遭遇するまで式を読み飛ばしてしまっている
		while (!currentTokenIs(TokenType.SEMICOLON)) {
			nextToken()
		}
		
		return statement
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
	
	fun peekError(tokenType: TokenType) {
		val errorMessage = "expected next token to be $tokenType, got ${peekToken?.type} instead"
		errors.add(errorMessage)
	}
	
	private fun checkedCurrentToken() =
		currentToken ?: Token(
			type = TokenType.ILLEGAL,
			literal = ""
		)
	
	private fun checkedCurrentTokenLiteral() = currentToken?.literal ?: ""
	
	fun registerPrefix(tokenType: TokenType, function: () -> Expression) {
		prefixParseFns[tokenType] = function
	}
	
	fun registerInfix(tokenType: TokenType, function: (Expression) -> Expression) {
		infixParseFns[tokenType] = function
	}
}