package com.github.dsbezerra.cashflow.core.di

import org.koin.core.module.Module

val appModules: List<Module>
    get() = listOf(
        loggingModule,
        platformModule,
        databaseModule,
        repositoryModule,
        useCaseModule,
        viewModelModule,
    )
