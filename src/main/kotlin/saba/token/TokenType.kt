package saba.token

enum class TokenType {
	ILLEGAL,
	EOF,
	
	// 識別子＋リテラル
	IDENT, //add, foobar, x, y, ...
	INT,   //123456...
	FLOAT,
	
	// 演算子
	ASSIGN,
	PLUS,
	MINUS,
	BANG,
	ASTERISK,
	SLASH,
	
	EQ,
	NOT_EQ,
	LT,
	GT,
	
	// デリミタ
	COMMA,
	SEMICOLON,
	COLON,
	
	LPAREN,
	RPAREN,
	LBRACE,
	RBRACE,
	
	// キーワード
	FUNCTION,
	LET,
	TRUE,
	FALSE,
	IF,
	ELSE,
	RETURN,
	
	TYPE
}