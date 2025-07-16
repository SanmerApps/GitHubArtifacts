package dev.sanmer.github.artifacts.di

import dev.sanmer.github.artifacts.database.AppDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val Database = module {
    singleOf(AppDatabase::build)

    single {
        get<AppDatabase>().token()
    }

    single {
        get<AppDatabase>().repo()
    }
}