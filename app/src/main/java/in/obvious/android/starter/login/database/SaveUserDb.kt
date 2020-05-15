package `in`.obvious.android.starter.login.database

import android.content.Context
import androidx.room.*

@Database(entities = [SavingUser::class], version = 1, exportSchema = false)
abstract class SaveUserDb : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: SaveUserDb? = null

        fun getDatabase(context: Context): SaveUserDb {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaveUserDb::class.java,
                    "user_db"
                )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }

}

@Entity(tableName = "note_table")
data class SavingUser(
    @PrimaryKey(autoGenerate = true) var id: Int,
    val username: String,
    val authToken:String
)


@Dao
interface UserDao {
    @Insert
    fun insertUser(user: SavingUser)
}
