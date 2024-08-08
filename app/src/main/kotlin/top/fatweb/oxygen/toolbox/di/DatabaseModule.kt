package top.fatweb.oxygen.toolbox.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import top.fatweb.oxygen.toolbox.data.tool.ToolDatabase
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideToolDao(@ApplicationContext context: Context): ToolDao =
        ToolDatabase.getInstance(context).toolDao()
}