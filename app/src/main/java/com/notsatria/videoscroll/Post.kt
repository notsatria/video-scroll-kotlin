package com.notsatria.videoscroll

class Post(
    val title: String,
    val description: String,
    val videoUrl: String
) {
    constructor() : this("", "", "")
}
