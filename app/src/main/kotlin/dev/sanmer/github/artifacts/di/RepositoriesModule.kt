package dev.sanmer.github.artifacts.di

import dev.sanmer.github.artifacts.repository.DbRepository
import dev.sanmer.github.artifacts.repository.DbRepositoryImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val RepositoriesModule = module {
    includes(DatabaseModule)
    single<DbRepositoryImpl>() bind DbRepository::class
}