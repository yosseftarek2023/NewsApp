package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImage: ImageView = itemView.findViewById(R.id.articleImage)
        val articleSource: TextView = itemView.findViewById(R.id.articleSource)
        val articleTitle: TextView = itemView.findViewById(R.id.articleTitle)
        val articleDescription: TextView = itemView.findViewById(R.id.articleDescription)
        val articleDateTime: TextView = itemView.findViewById(R.id.articleDateTime)
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.apply {
            Glide.with(itemView.context)
                .load(article.urlToImage)
                .into(articleImage)
            articleSource.text = article.source?.name
            articleTitle.text = article.title
            articleDateTime.text = article.publishedAt
            articleDescription.text = article.description

            itemView.setOnClickListener {
                onItemClickListener?.invoke(article)
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
