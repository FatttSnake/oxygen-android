package top.fatweb.oxygen.toolbox.data.tool

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolEntity

@Database(
    entities = [ToolEntity::class],
    version = 1,
    autoMigrations = [],
    exportSchema = true
)
abstract class ToolDatabase : RoomDatabase() {
    abstract fun toolDao(): ToolDao

    companion object {
        @Volatile
        private var INSTANCE: ToolDatabase? = null

        fun getInstance(context: Context): ToolDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, ToolDatabase::class.java, "tools.db")
                    .build()
                    .also { INSTANCE = it }
            }
    }
}