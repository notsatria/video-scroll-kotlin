package com.notsatria.videoscroll.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}
