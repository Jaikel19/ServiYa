package com.example.shared.di

import com.example.shared.di.modules.sharedModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
  return startKoin {
    config?.invoke(this)
    modules(sharedModule)
  }
}
