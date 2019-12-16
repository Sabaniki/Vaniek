import java.lang.Exception

class Tokenizer(var token: Token) {
	
	// Consumes the current token if it matches `op`.
	private fun consume(op: Char): Boolean {
		if (token.kind != TokenKind.RESERVED || token.tokenString[0] != op) return false
		token = token.next ?: throw Exception("token.next is null at consume")
		return true
	}
	
	// Ensure that the current token is `op`.
	private fun ecpect(op: Char) {
		if (token.kind != TokenKind.RESERVED || token.tokenString[0] != op) error("expected: $op")
		token = token.next ?: throw Exception("token.next is null at expect")
	}
	
	// Ensure that the current token is TK_NUM.
	private fun expectNunber(): Int {
		if (token.kind != TokenKind.NUM)
			error("expected a number")
		val value = token.value
		token = token.next ?: throw Exception("token is null at expectNumber")
		return value ?: throw Exception("value is null at expectNumber")
	}
	
	private fun atEOF() = token.kind == TokenKind.EOF
	
	fun tokenize(point: String): Token {
		val head = Token()
		head.next = null
		var cur = head
		for (p in point) {
			if (p.isWhitespace()) continue
			
			// Punctuator
			if(p == '+' || p == '-') {
				cur = Token().apply {
					kind = TokenKind.RESERVED
					next = cur
					
				}
			}
			
		}
	}
}