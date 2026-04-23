package com.example.shared.di.modules

import org.koin.dsl.module

val sharedModule = module {
    includes(
        dataModule,
        domainModule,
        presentationModule,
        networkModule,
        platformModule()
    )
}