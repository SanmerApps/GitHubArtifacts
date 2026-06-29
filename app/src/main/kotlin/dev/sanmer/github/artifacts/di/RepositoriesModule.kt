package dev.sanmer.github.artifacts.di

import dev.sanmer.github.artifacts.repository.DbRepository
import dev.sanmer.github.artifacts.repository.DbRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val Repositories = module {
    singleOf(::DbRepositoryImpl) { bind<DbRepository>() }
}