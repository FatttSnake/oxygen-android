package top.fatweb.oxygen.toolbox.data.tool

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolBaseDao
import top.fatweb.oxygen.toolbox.data.tool.dao.ToolDao
import top.fatweb.oxygen.toolbox.model.tool.ToolBaseWithDistEntity
import top.fatweb.oxygen.toolbox.model.tool.ToolWithDistEntity
import top.fatweb.oxygen.toolbox.repository.storage.CASRepository

@Database(
    entities = [ToolWithDistEntity::class, ToolBaseWithDistEntity::class],
    version = 2,
    autoMigrations = [],
    exportSchema = true
)
abstract class ToolDatabase : RoomDatabase() {
    abstract fun toolDao(): ToolDao
    abstract fun toolBaseDao(): ToolBaseDao

    companion object {
        @Volatile
        private var INSTANCE: ToolDatabase? = null

        /**
         * Migration 1→2: Clear all data from the database.
         *
         * Previously, tool dist and tool base dist content was stored directly in the `dist` TEXT
         * column of the `tool` and `tool_base` tables. When the content exceeded ~2 MB, Room's
         * CursorWindow could not load the rows, causing SQLiteBlobTooBigException.
         *
         * Starting in version 2, the `dist` column stores only a 64-character SHA-256 file key,
         * while the actual content is saved to the app's internal file storage via [CASRepository].
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DELETE FROM tool")
                db.execSQL("DELETE FROM tool_base")
            }
        }

        fun getInstance(context: Context): ToolDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = ToolDatabase::class.java,
                    name = "tool.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
