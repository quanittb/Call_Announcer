package com.mobiai.base.basecode.extensions

import android.widget.ScrollView
import android.view.View

fun ScrollView.scrollToBottom() {
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val delta = bottom - (scrollY+ height)
    smoothScrollBy(0, delta)
}


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.isGone() : Boolean {
    return this.visibility == View.GONE
}

fun View.isInvisible() : Boolean {
    return this.visibility == View.INVISIBLE
}

fun View.isVisible() : Boolean {
    return this.visibility == View.VISIBLE
}

fun getVisibleState() : Int {
    return View.VISIBLE
}

fun getGoneState() : Int {
    return View.GONE
}

fun getInvisibleState() : Int {
    return View.INVISIBLE
}