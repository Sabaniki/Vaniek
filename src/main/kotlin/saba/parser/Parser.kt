package saba.parser

import saba.ast.Boolean
import saba.ast.FloatLiteral
import saba.lexer.Lexer
import saba.token.Token
import saba.token.TokenType
import saba.ast.expresssion.Expression
import saba.ast.Identifier
import saba.ast.IntegerLiteral
import saba.ast.Program
import saba.ast.expresssion.IfExpression
import saba.ast.expresssion.InfixExpression
import saba.ast.expresssion.PrefixExpression
import saba.ast.statement.*

class Parser(val lexer: Lexer) {
	private var currentToken: Token? = null
	private var peekToken: Token? = null
	private val prefixParseFns = mutableMapOf<TokenType, () -> Expression?>()
	private val infixParseFns = mutableMapOf<TokenType, (Expression) -> Expression>()
	val errors = mutableListOf<String>()
	private val precedences = mapOf(
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
		registerPrefix(TokenType.IF, ::parseIfExpression)
		registerPrefix(TokenType.LPAREN, ::parseGroupedExpression)
		registerPrefix(TokenType.IDENT, ::parseIdentifier)
		registerPrefix(TokenType.INT, ::parseIntegerLiteral)
		registerPrefix(TokenType.FLOAT, ::parseFloatLiteral)
		registerPrefix(TokenType.TRUE, ::parseBoolean)
		registerPrefix(TokenType.FALSE, ::parseBoolean)
		registerPrefix(TokenType.BANG, ::parsePrefixExpression)
		registerPrefix(TokenType.MINUS, ::parsePrefixExpression)
		registerPrefix(TokenType.PLUS, ::parsePrefixExpression)
		
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
	
	private fun parseIfExpression(): Expression? {
		val token = checkedCurrentToken()
		if (!expectPeek(TokenType.LPAREN)) return null
		nextToken()
		val condition = parseExpression(Precedence.LOWEST) ?: return null
		if (!expectPeek(TokenType.RPAREN) || !expectPeek(TokenType.LBRACE)) return null
		val ifBlock = parseBlockStatement()
		var elseBlock: BlockStatement? = null
		if (expectPeek(TokenType.ELSE)) {
			if (peekTokenIs(TokenType.LBRACE)) {
				nextToken()
				elseBlock = parseBlockStatement()
			}
		}
		return IfExpression(token, condition, ifBlock, elseBlock)
	}
	
	private fun parseBlockStatement(): BlockStatement {
		val block = BlockStatement(checkedCurrentToken())
		nextToken()
		while (!currentTokenIs(TokenType.RBRACE) && !currentTokenIs(TokenType.EOF)) {
			parseStatement()?.let { block.statements.add(it) }
			nextToken()
		}
		return block
	}
	
	private fun parseGroupedExpression(): Expression? {
		nextToken()
		val expression = parseExpression(Precedence.LOWEST)
		if (!expectPeek(TokenType.RPAREN)) return null
		return expression
	}
	
	private fun parseBoolean(): Expression = Boolean(checkedCurrentToken(), (currentTokenIs(TokenType.TRUE)))
	
	private fun parseIdentifier() = Identifier(checkedCurrentToken(), checkedCurrentToken().literal, "") //TODO
	
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
	
	private fun parseFloatLiteral(): Expression {
		val value = checkedCurrentToken().literal.toFloatOrNull()
		if (value == null) {
			errors.add("could not parse ${checkedCurrentToken().literal}")
			return FloatLiteral(Token(TokenType.ILLEGAL, ""), Float.MIN_VALUE)
		}
		
		return FloatLiteral(
			checkedCurrentToken(),
			checkedCurrentToken().literal.toFloat()
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
	
	private fun nextToken() {
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
	
	private fun parseStatement(): Statement? {
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
			leftExpression = infix(leftExpression ?: return null)
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
		
		val identifierToken = checkedCurrentToken()
		val typeLiteral: String
		
		if (expectPeek(TokenType.COLON)) {
			nextToken() // Type
			typeLiteral = checkedCurrentToken().literal
		}
		else return null
		
		val name = Identifier(identifierToken, identifierToken.literal, typeLiteral)// ← TODO
		
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
	
	private fun currentTokenIs(tokenType: TokenType) = currentToken?.type == tokenType
	
	private fun peekTokenIs(tokenType: TokenType) = peekToken?.type == tokenType
	
	private fun expectPeek(tokenType: TokenType) =
		if (peekTokenIs(tokenType)) {
			nextToken()
			true
		}
		else {
			peekError(tokenType)
			false
		}
	
	private fun peekError(tokenType: TokenType) {
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
	
	private fun registerPrefix(tokenType: TokenType, function: () -> Expression?) {
		prefixParseFns[tokenType] = function
	}
	
	private fun registerInfix(tokenType: TokenType, function: (Expression) -> Expression) {
		infixParseFns[tokenType] = function
	}
	
	private fun noPrefixParseFnError(token: Token) =
		errors.add("no prefix parse function for { Token: ${token.type}, Literal ${token.literal}}")
}