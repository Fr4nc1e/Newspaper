package com.mvvm.newspaper.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mvvm.newspaper.database.db.ArticleDataBase
import com.mvvm.newspaper.databinding.ActivityMainBinding
import com.mvvm.newspaper.repository.NewsRepository
import com.mvvm.newspaper.ui.viewmodel.MainViewModel
import com.mvvm.newspaper.ui.viewmodel.factory.MainViewModelFactory

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    val viewModel by lazy {
        val viewModelProviderFactory = MainViewModelFactory(newsRepository)
        ViewModelProvider(this, viewModelProviderFactory)[MainViewModel::class.java]
    }

    private val newsRepository by lazy {
        NewsRepository(ArticleDataBase(this))
    }

    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(binding.newsNavHostFragment.id) as NavHostFragment

        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView
            .setupWithNavController(navController)
    }
}
