package com.devanand.weather.view.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devanand.weather.R
import com.devanand.weather.adapter.LocationAdapter
import com.devanand.weather.database.Coordinate
import com.devanand.weather.database.DatabaseBuilder
import com.devanand.weather.database.DatabaseHelperImpl
import com.devanand.weather.utility.Status
import com.devanand.weather.utility.ViewModelFactory
import com.devanand.weather.viewmodel.LocationViewModel
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var viewModel: LocationViewModel
    private lateinit var adapter: LocationAdapter
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        recycler = findViewById(R.id.recycler)
        val actionBar = supportActionBar

        // showing the back button in action bar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        setupUI()
        setupViewModel()
        setupObserver()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupUI() {
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = LocationAdapter(arrayListOf())
        recycler.addItemDecoration(
            DividerItemDecoration(
                recycler.context,
                (recycler.layoutManager as LinearLayoutManager).orientation
            )
        )

        recycler.adapter = adapter
    }

    private fun setupObserver() {
        viewModel.getLocations().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { users -> renderList(users) }
                    recycler.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recycler.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun renderList(users: List<Coordinate>) {
        adapter.addData(users)
        adapter.notifyDataSetChanged()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(LocationViewModel::class.java)
    }
}