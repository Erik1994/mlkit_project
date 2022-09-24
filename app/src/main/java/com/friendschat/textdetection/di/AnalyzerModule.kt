package com.friendschat.textdetection.di

import com.friendschat.textdetection.analyzer.ImageTextAnalyer
import com.friendschat.textdetection.dipatchers.AppDispatchers
import org.koin.dsl.module

val analyzerModule = module {
    factory { AppDispatchers() }
    single { ImageTextAnalyer(appDispatchers = get()) }
}