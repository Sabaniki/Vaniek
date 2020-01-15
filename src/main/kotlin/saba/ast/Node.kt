package saba.ast

interface Node {
	fun tokenLiteral(): String
	override fun toString(): String
}