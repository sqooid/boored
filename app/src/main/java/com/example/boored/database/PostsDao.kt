package com.example.boored.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostsDao {
    @Query("SELECT * FROM favourites_table ORDER BY dateFavourited")
    fun getByDate(): LiveData<List<FavouritesDatabaseFormat>>

    @Query("SELECT EXISTS (SELECT 1 FROM favourites_table WHERE fileUrl = :fileUrl)")
    fun isInFavourites(fileUrl: String): LiveData<Boolean>

    @Insert
    fun addToFavourites(favourites: FavouritesDatabaseFormat)

    @Delete
    fun removeFromFavourites(favourites: FavouritesDatabaseFormat)
}