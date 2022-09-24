package com.friendschat.textdetection.dipatchers

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class AppDispatchers(
    val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    val mainDispatcherImmediate: CoroutineDispatcher = Dispatchers.Main.immediate,
    val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
)