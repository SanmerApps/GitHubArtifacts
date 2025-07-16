package dev.sanmer.github.artifacts.di

import dev.sanmer.github.artifacts.ui.screen.home.HomeViewModel
import dev.sanmer.github.artifacts.ui.screen.repo.AddRepoViewModel
import dev.sanmer.github.artifacts.ui.screen.repo.RepoViewModel
import dev.sanmer.github.artifacts.ui.screen.setting.SettingViewModel
import dev.sanmer.github.artifacts.ui.screen.token.AddTokenViewModel
import dev.sanmer.github.artifacts.ui.screen.token.TokenViewModel
import dev.sanmer.github.artifacts.ui.screen.workflow.WorkflowViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ViewModels = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::WorkflowViewModel)
    viewModelOf(::SettingViewModel)
    viewModelOf(::TokenViewModel)
    viewModelOf(::AddTokenViewModel)
    viewModelOf(::RepoViewModel)
    viewModelOf(::AddRepoViewModel)
}