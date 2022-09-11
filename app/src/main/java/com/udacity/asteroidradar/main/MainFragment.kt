package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        val adapter = AsteroidAdapter(AsteroidAdapter.OnClickListener { asteroid ->
            viewModel.onAsteroidItemClick(asteroid)
        })

        binding.asteroidRecycler.adapter = adapter
        viewModel.asteroidList.observe(viewLifecycleOwner) { asteroid ->
            asteroid.apply {
                adapter.submitList(this)
            }
        }
        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner) { asteroid ->
            if (asteroid != null) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                viewModel.onDetailFragmentNavigated()
            }
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.onChangeFilter(
            when (item.itemId) {
                R.id.show_rent_menu -> {
                    FilterAsteroid.TODAY
                }
                R.id.show_all_menu -> {
                    FilterAsteroid.WEEK
                }
                else -> {
                    FilterAsteroid.ALL
                }
            }
        )
        return true
    }
}
