package com.example.vix_schoters_jerry_berlin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.vix_schoters_jerry_berlin.util.Constant.DEFAULT_CATEGORY
import com.example.vix_schoters_jerry_berlin.util.Constant.DEFAULT_COUNTRY
import com.example.vix_schoters_jerry_berlin.util.Constant.PREFERENCES_BACK_ONLINE
import com.example.vix_schoters_jerry_berlin.util.Constant.PREFERENCES_CATEGORY
import com.example.vix_schoters_jerry_berlin.util.Constant.PREFERENCES_CATEGORY_ID
import com.example.vix_schoters_jerry_berlin.util.Constant.PREFERENCES_COUNTRY
import com.example.vix_schoters_jerry_berlin.util.Constant.PREFERENCES_COUNTRY_ID
import com.example.vix_schoters_jerry_berlin.util.Constant.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(PREFERENCES_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {
    private object PreferenceKeys {
        val selectedCountry = stringPreferencesKey(PREFERENCES_COUNTRY)
        val selectedCountryId = intPreferencesKey(PREFERENCES_COUNTRY_ID)
        val selectedCategory = stringPreferencesKey(PREFERENCES_CATEGORY)
        val selectedCategoryId = intPreferencesKey(PREFERENCES_CATEGORY_ID)
        val backOnline = booleanPreferencesKey(PREFERENCES_BACK_ONLINE)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveCountryAndCategoryType(
        country: String,
        countryId: Int,
        category: String,
        categoryId: Int
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedCountry] = country
            preferences[PreferenceKeys.selectedCountryId] = countryId
            preferences[PreferenceKeys.selectedCategory] = category
            preferences[PreferenceKeys.selectedCategoryId] = categoryId
        }
    }

    suspend fun saveBackOnline(backOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }

    val readCountryAndCategoryType: Flow<CountryAndCategoryType> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val selectedCountry = preferences[PreferenceKeys.selectedCountry] ?: DEFAULT_COUNTRY
            val selectedCountryId = preferences[PreferenceKeys.selectedCountryId] ?: 0
            val selectedCategory = preferences[PreferenceKeys.selectedCategory] ?: DEFAULT_CATEGORY
            val selectedCategoryId = preferences[PreferenceKeys.selectedCategoryId] ?: 0
            CountryAndCategoryType(
                selectedCountry,
                selectedCountryId,
                selectedCategory,
                selectedCategoryId
            )
        }

    val readBackOnline: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {preferences ->
            val backOnline = preferences[PreferenceKeys.backOnline] ?: false
            backOnline
        }
}


data class CountryAndCategoryType(
    val selectedCountry: String,
    val selectedCountryId: Int,
    val selectedCategory: String,
    val selectedCategoryId: Int
)