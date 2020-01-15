package saba.parser

import saba.lexer.Lexer
import saba.token.Token
import saba.token.TokenType
import saba.ast.expresssion.Expression
import saba.ast.Identifier
import saba.ast.IntegerLiteral
import saba.ast.Program
import saba.ast.expresssion.InfixExpression
import saba.ast.expresssion.PrefixExpression
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
	val precedences = mapOf(
		TokenType.EQ to Precedence.EQUALS,
		TokenType.NOT_EQ to Precedence.EQUALS,
		TokenType.LT to Precedence.LESS_OR_GREATER,
		TokenType.GT to Precedence.LESS_OR_GREATER,
		TokenType.PLUS to Precedence.SUM,
		TokenType.MINUS to Precedence.SUM,
		TokenType.SLASH to Precedence.PRODUCT,
		TokenType.ASTERISK to Precedence.PRODUCT
	)
	
	init {
		registerPrefix(TokenType.IDENT, ::parseIdentifier)
		registerPrefix(TokenType.INT, ::parseIntegerLiteral)
		registerPrefix(TokenType.BANG, ::parsePrefixExpression)
		registerPrefix(TokenType.MINUS, ::parsePrefixExpression)
		
		registerInfix(TokenType.PLUS, ::parseInfixExpression)
		registerInfix(TokenType.MINUS, ::parseInfixExpression)
		registerInfix(TokenType.SLASH, ::parseInfixExpression)
		registerInfix(TokenType.ASTERISK, ::parseInfixExpression)
		registerInfix(TokenType.EQ, ::parseInfixExpression)
		registerInfix(TokenType.NOT_EQ, ::parseInfixExpression)
		registerInfix(TokenType.LT, ::parseInfixExpression)
		registerInfix(TokenType.GT, ::parseInfixExpression)
		
		// 2つトークンを読み込む。currentTokenとpeekTokenの両方がセットされる。
		repeat(2) { nextToken() }
	}
	
	private fun parseIdentifier() = Identifier(checkedCurrentToken(), checkedCurrentToken().literal)
	
	private fun parseIntegerLiteral(): Expression {
		val value = checkedCurrentToken().literal.toIntOrNull()
		if (value == null) {
			errors.add("could not parse ${checkedCurrentToken().literal}")
			return IntegerLiteral(Token(TokenType.ILLEGAL, ""), Int.MIN_VALUE)
		}
		
		return IntegerLiteral(
			checkedCurrentToken(),
			checkedCurrentToken().literal.toInt()
		)
	}
	
	private fun parsePrefixExpression(): Expression {
		val beforeToken = checkedCurrentToken()
		nextToken()
		return PrefixExpression(beforeToken, beforeToken.literal, parseExpression(Precedence.PREFIX))
	}
	
	private fun parseInfixExpression(leftExpression: Expression): Expression {
		val beforeToken = checkedCurrentToken()
		val precedence = currentPrecedence()
		nextToken()
		return InfixExpression(beforeToken, leftExpression, beforeToken.literal, parseExpression(precedence))
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
		val statement = ExpressionStatement(checkedCurrentToken(), parseExpression(Precedence.LOWEST))
		
		if (peekTokenIs(TokenType.SEMICOLON)) nextToken()
		return statement
	}
	
	private fun parseExpression(precedence: Precedence): Expression? {
		val prefix = prefixParseFns[currentToken?.type]
		var leftExpression = if (prefix == null) {
			noPrefixParseFnError(checkedCurrentToken())
			return null
		}
		else prefix()
		
		while (!peekTokenIs(TokenType.SEMICOLON) && precedence < peekPrecedence()) {
			val infix = infixParseFns[checkedPeekToken().type] ?: return leftExpression
			nextToken()
			leftExpression = infix(leftExpression)
		}
		return leftExpression
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
		val token = checkedCurrentToken()
		
		if (!expectPeek(TokenType.IDENT)) return null
		
		val name = Identifier(checkedCurrentToken(), checkedCurrentToken().literal)
		
		if (!expectPeek(TokenType.ASSIGN)) return null
		
		val statement = LetStatement(
			token,
			name,
			null    // TODO: 本当はここにちゃんとした値が入る
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
	
	private fun checkedPeekToken() =
		peekToken ?: Token(
			type = TokenType.ILLEGAL,
			literal = ""
		)
	
	private fun peekPrecedence() = precedences[checkedPeekToken().type] ?: Precedence.LOWEST
	private fun currentPrecedence() = precedences[checkedCurrentToken().type] ?: Precedence.LOWEST
	
	private fun registerPrefix(tokenType: TokenType, function: () -> Expression) {
		prefixParseFns[tokenType] = function
	}
	
	private fun registerInfix(tokenType: TokenType, function: (Expression) -> Expression) {
		infixParseFns[tokenType] = function
	}
	
	private fun noPrefixParseFnError(token: Token) =
		errors.add("no prefix parse function for for { Token: ${token.type}, Literal ${token.literal}}")
	
}