package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var newsViewModel: NewsViewModel
    private val args: ArticleFragmentArgs by navArgs()
    private lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticleBinding.bind(view)

        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.article

        // Debug log to verify article object
        Log.d("ArticleFragment", "Article received: $article")

        // Configure WebView
        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    Log.d("ArticleFragment", "Page loaded: $url")
                }

                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError
                ) {
                    Log.e("ArticleFragment", "WebView error: ${error.description}")
                    showError("Failed to load the page.")
                }
            }

            article.url?.let {
                if (it.isNotEmpty()) {
                    loadUrl(it)
                } else {
                    Log.e("ArticleFragment", "URL is empty")
                    showError("The URL is empty.")
                }
            } ?: run {
                Log.e("ArticleFragment", "URL is null")
                showError("The URL is not available.")
            }
        }

        // Set up FloatingActionButton click listener
        binding.fab.setOnClickListener {
            if (article != null) {
                newsViewModel.addToFavorite(article)
                Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(view, "Error: Article not found", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showError(message: String) {
        // Display an error message or take appropriate action
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
