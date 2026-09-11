package dev.sanmer.github.artifacts.di

import dev.sanmer.github.artifacts.ui.screen.home.HomeViewModel
import dev.sanmer.github.artifacts.ui.screen.token.EditTokenViewModel
import dev.sanmer.github.artifacts.ui.screen.token.TokenViewModel
import dev.sanmer.github.artifacts.ui.screen.workflow.WorkflowViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val ViewModelsModule = module {
    includes(RepositoriesModule, GitHubModule)
    viewModel<HomeViewModel>()
    viewModel<WorkflowViewModel>()
    viewModel<TokenViewModel>()
    viewModel<EditTokenViewModel>()
}