package com.github.dsbezerra.cashflow.di

import org.koin.core.module.Module

val appModules: List<Module>
    get() = listOf(
        platformModule,
        databaseModule,
        repositoryModule,
        useCaseModule,
        viewModelModule,
    )
