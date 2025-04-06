package net.emilla.math

internal sealed interface CalcToken {
    sealed interface InfixToken : CalcToken
    sealed interface BitwiseToken : CalcToken

    object LParen : InfixToken, BitwiseToken
    object RParen : InfixToken, BitwiseToken
}
