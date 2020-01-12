package saba.parser

import io.kotlintest.shouldBe
import io.kotlintest.specs.ShouldSpec

class ParserTest : ShouldSpec({
	fun checkParserErrors(parser: Parser) {
		val errors = parser.errors
		shouldBe { errors.isEmpty() }
	}
})