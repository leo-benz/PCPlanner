package di

import dependencies.JubilareRepository
import dependencies.JubilareRepositoryImpl
import dependencies.JubilareViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    singleOf(::JubilareRepositoryImpl).bind<JubilareRepository>()
    viewModelOf(::JubilareViewModel)
}