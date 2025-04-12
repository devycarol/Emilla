@file:JvmName("Hashes")

package net.emilla.util

@JvmName("one")
fun hash1(o: Any) = hash1(o.hashCode())
@JvmName("one")
fun hash1(n: Int) = 0x1F + n
@JvmName("two")
fun hash2(a: Any, b: Any) = hash2(a.hashCode(), b.hashCode())
@JvmName("two")
fun hash2(a: Int, b: Int) = 0x1F * (0x1F + a) + b
