package com.example.newsapp.repository

import com.example.newsapp.api.RetrofitInstance
import com.example.newsapp.dp.ArticleDatabase
import com.example.newsapp.models.Article

class NewsRepository(val db: ArticleDatabase) {
    suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadLines(countryCode,pageNumber)

    suspend fun searchNews(q:String,pageNumber: Int) =
        RetrofitInstance.api.searchNews(q,pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)
    suspend fun delete(article: Article) = db.getArticleDao().deleteArticle(article)
     fun getFavArticles() = db.getArticleDao().getAllArticles()
}