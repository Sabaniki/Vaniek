import java.util.*

class Lexer {
	private fun getAtom(s: String, i: Int): String {
		var j = i
		while (j < s.length) {
			if (Character.isLetter(s[j])) {
				j++
			}
			else {
				return s.substring(i, j)
			}
		}
		return s.substring(i, j)
	}
	
	fun lex(input: String): List<Token> {
		val result: MutableList<Token> = ArrayList()
		var i = 0
		while (i < input.length) {
			print(input[i].toString())
			when (input[i]) {
				'(' -> {
					result.add(Token(TokenKind.LPAREN, "("))
					i++
				}
				')' -> {
					result.add(Token(TokenKind.RPAREN, ")"))
					i++
				}
				'{' -> {
					result.add(Token(TokenKind.LBRANCE, "{"))
					i++
				}
				'}' -> {
					result.add(Token(TokenKind.RBRANCE, "}"))
					i++
				}
				else -> {
					if (Character.isWhitespace(input[i])) {
						i++
					}
					else {
						val atom = getAtom(input, i)
						i += atom.length
						result.add(Token(TokenKind.ATOM, atom))
					}
				}
			}
		}
		return result
	}
}