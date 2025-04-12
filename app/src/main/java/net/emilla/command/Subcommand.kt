package net.emilla.command

data class Subcommand<A : Enum<A>>(@JvmField val action: A, @JvmField val instruction: String?)
