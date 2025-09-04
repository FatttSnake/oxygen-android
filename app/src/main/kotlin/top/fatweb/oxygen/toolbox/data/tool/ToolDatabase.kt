package top.fatweb.oxygen.toolbox.data.tool

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolBaseDao
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity

@Database(
    entities = [ToolWithDistEntity::class, ToolBaseWithDistEntity::class],
    version = 1,
    autoMigrations = [],
    exportSchema = true
)
abstract class ToolDatabase : RoomDatabase() {
    abstract fun toolDao(): ToolDao
    abstract fun toolBaseDao(): ToolBaseDao

    companion object {
        @Volatile
        private var INSTANCE: ToolDatabase? = null

        fun getInstance(context: Context): ToolDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = ToolDatabase::class.java,
                    name = "tool.db"
                )
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
