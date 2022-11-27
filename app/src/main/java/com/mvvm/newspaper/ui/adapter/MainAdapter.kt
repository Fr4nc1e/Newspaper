package com.mvvm.newspaper.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mvvm.newspaper.databinding.ItemArticlePreviewBinding
import com.mvvm.newspaper.model.Article

class MainAdapter : RecyclerView.Adapter<MainAdapter.ArticleViewHolder>() {

    private lateinit var binding: ItemArticlePreviewBinding

    inner class ArticleViewHolder(
        binding: ItemArticlePreviewBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ArticleViewHolder(
        ItemArticlePreviewBinding
            .inflate(
                LayoutInflater
                    .from(parent.context),
                parent,
                false
            )
    )

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(binding.ivArticleImage)
            binding.tvSource.text = article.source.name
            binding.tvTitle.text = article.title
            binding.tvDescription.text = article.description
            binding.tvPublishedAt.text = article.publishedAt
            setOnClickListener {
                onItemOnClickListener?.let { it(article) }
            }
        }
    }

    private var onItemOnClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemOnClickListener = listener
    }

    override fun getItemCount() = differ.currentList.size
}
