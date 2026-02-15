package com.hdw.bookmarker.util.logger

import timber.log.Timber

class TimberDebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String = "${element.fileName}:${element.lineNumber}"

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val messageWithLocation = if (tag != null) "[$tag] $message" else message
        val fileName = tag?.substringBefore(":")
        super.log(priority = priority, tag = fileName, message = messageWithLocation, t = t)
    }
}
