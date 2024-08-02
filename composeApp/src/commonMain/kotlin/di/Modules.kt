package di

import repository.JubilareRepository
import repository.JubilareRepositoryImpl
import viewmodel.JubilareViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    singleOf(::JubilareRepositoryImpl).bind<JubilareRepository>()
    viewModelOf(::JubilareViewModel)
}