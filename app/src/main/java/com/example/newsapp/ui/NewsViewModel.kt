package com.example.newsapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.newsapp.models.Article
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.repository.NewsRepository
import com.example.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app : Application,val newsRepository:NewsRepository) : AndroidViewModel(app) {

    val headlines : MutableLiveData<Resource<NewsResponse>>  = MutableLiveData()
    var headlinesPage = 1
    var headlinesResponse : NewsResponse? = null

    val searchNews : MutableLiveData<Resource<NewsResponse>>  =MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse : NewsResponse? = null
    var newSearchQuery : String? = null
    var oldSearchQuery : String? = null

    init {
        getHeadlines("us")
    }

    fun getHeadlines(countryCode: String) = viewModelScope.launch {
        headlineInternet(countryCode)
    }
    fun getSearchNews(searchQuery: String) = viewModelScope.launch{
        searchInternet(searchQuery)
    }


    private fun handleHeadlineResponse (response : Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                headlinesPage++
                if(headlinesResponse == null){
                    headlinesResponse = resultResponse
                }else{
                    val oldArticles = headlinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headlinesResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse (response : Response<NewsResponse>) : Resource<NewsResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(searchNewsResponse == null || newSearchQuery != oldSearchQuery){
                    searchNewsPage = 1
                    oldSearchQuery = newSearchQuery
                    searchNewsResponse = resultResponse
                }else{
                    searchNewsPage++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun addToFavorite(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }
    fun getFavArticles() = newsRepository.getFavArticles()

    fun deleteArticle (article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    fun internetConnection(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private suspend fun headlineInternet(countryCode : String){
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.getHeadlines(countryCode,headlinesPage)
                headlines.postValue(handleHeadlineResponse(response))
            }else{
                headlines.postValue(Resource.Error("No internet"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> headlines.postValue(Resource.Error("unable to connect"))
                else -> headlines.postValue(Resource.Error("No signal"))
            }
        }
    }
    private suspend fun searchInternet(searchQuery: String){
        newSearchQuery = searchQuery
        headlines.postValue(Resource.Loading())
        try{
            if(internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            }else{
                searchNews.postValue(Resource.Error("No internet"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("unable to connect"))
                else -> searchNews.postValue(Resource.Error("No signal"))
            }
        }
    }

}