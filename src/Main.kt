fun main(args: Array<String>) {
	if (args.isEmpty()) {
		println("Usage: java Lexer \"((some Scheme) (code to) lex)\".")
		return
	}
	val lexer = Lexer()
	val tokens = lexer.lex(args[0])
	for (token in tokens) {
		println(token)
	}
}