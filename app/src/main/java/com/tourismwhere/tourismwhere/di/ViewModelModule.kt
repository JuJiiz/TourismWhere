package com.tourismwhere.tourismwhere.di

import android.arch.lifecycle.ViewModel
import com.tourismwhere.tourismwhere.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindLoginViewModel(mainViewModel: MainViewModel): ViewModel
}