package dev.tekofx.bibliotecat.model

import dev.tekofx.bibliotecat.R


enum class StatusColor(val value: Int) {
    GREEN(R.color.green_open),
    YELLOW(R.color.yellow_soon),
    ORANGE(R.color.orange_depends),
    RED(R.color.red_closed),
    GRAY(R.color.gray_600),
}