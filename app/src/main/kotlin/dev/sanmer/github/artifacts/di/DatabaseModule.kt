package dev.sanmer.github.artifacts.di

import dev.sanmer.github.artifacts.database.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val DatabaseModule = module {
    single {
        AppDatabase.build(androidContext())
    }

    single {
        get<AppDatabase>().token()
    }

    single {
        get<AppDatabase>().repo()
    }
}