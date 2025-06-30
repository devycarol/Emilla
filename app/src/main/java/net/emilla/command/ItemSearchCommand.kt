package net.emilla.command

import net.emilla.struct.sort.SearchItem

interface ItemSearchCommand {
    fun use(item: SearchItem)
    fun items(): Collection<SearchItem>
}
