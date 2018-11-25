package com.tourismwhere.tourismwhere.di

import com.tourismwhere.tourismwhere.ui.MainActivity
import com.tourismwhere.tourismwhere.ui.fragment.DetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BindActivityModule {
    @ContributesAndroidInjector()
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector()
    abstract fun contributeDetailFragment(): DetailFragment
}
