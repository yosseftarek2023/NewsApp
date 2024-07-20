package com.example.newsapp.dp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.models.Article

@Database(
    entities = [Article::class],
    version = 2
)

@TypeConverters(Converters::class)
abstract class ArticleDatabase : RoomDatabase() {

    abstract fun getArticleDao(): Dao

    companion object {
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context : Context) = instance ?: synchronized(LOCK){
            instance?: createDatabase(context).also {
                instance = it
            }
        }

        private fun createDatabase(context: Context): ArticleDatabase = Room.databaseBuilder(
            context.applicationContext,ArticleDatabase::class.java,"Article_Db.db"
        ).build()
    }
}