package us.ait.android.weatherinfo.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CityDao {
    @Query("SELECT * FROM city")
    List<City> getAllCities();

    @Insert
    long insertCity(City city);

    @Delete
    void deleteCity(City city);
}
