package com.example.sijar.di

import com.example.sijar.api.model.repository.AuthRepository
import com.example.sijar.api.model.repository.DashboardRepository
import com.example.sijar.api.model.repository.ItemRepository
import com.example.sijar.api.model.repository.PeminjamanRepository
import com.example.sijar.api.model.repository.ProfileRepository
import com.example.sijar.api.model.repository.RiwayatRepository
import com.example.sijar.api.model.repository.WaktuRepository
import com.example.sijar.api.utils.ApiClient
import com.example.sijar.viewModel.BarangViewModel
import com.example.sijar.viewModel.DashboardViewModel
import com.example.sijar.viewModel.LoginViewModel
import com.example.sijar.viewModel.PeminjamanViewModel
import com.example.sijar.viewModel.ProfileViewModel
import com.example.sijar.viewModel.WaktuViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf

val appModule = module {
    single { ApiClient.apiService }
    singleOf(::AuthRepository)
    singleOf(::DashboardRepository)
    singleOf(::ItemRepository)
    singleOf(::PeminjamanRepository)
    singleOf(::ProfileRepository)
    singleOf(::RiwayatRepository)
    singleOf(::WaktuRepository)

    viewModelOf(::LoginViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::BarangViewModel)
    viewModelOf(::PeminjamanViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::WaktuViewModel)
}
