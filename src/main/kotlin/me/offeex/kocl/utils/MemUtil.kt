package me.offeex.kocl.utils

object MemUtil {
	fun longBytes(n: Number) = n.toLong() * Long.SIZE_BYTES
	fun intBytes(n: Number) = n.toLong() * Int.SIZE_BYTES
	fun shortBytes(n: Number) = n.toLong() * Short.SIZE_BYTES
}