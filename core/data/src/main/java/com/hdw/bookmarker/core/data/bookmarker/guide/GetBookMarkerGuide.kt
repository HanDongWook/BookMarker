package com.hdw.bookmarker.core.data.bookmarker.guide

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface GetBookMarkerGuide {
    fun getGuide(): String
}