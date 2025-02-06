package com.example.weathery.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.MainActivity
import com.example.weathery.R
import com.example.weathery.adapter.FavCityAdapter
import com.example.weathery.adapter.WeatherAdapter
import com.example.weathery.data.local.CityEntity
import com.example.weathery.data.local.DatabaseProvider
import com.example.weathery.viewmodel.HomeViewModelFactory
import com.example.weathery.data.repository.WeatherRepository
import com.example.weathery.model.FavoriteCity
import com.example.weathery.utils.LocationManager
import com.example.weathery.viewmodel.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"

/**
 * 메인 화면의 UI를 초기화하고 ViewModel과 통신
 */
class HomeFragment : Fragment() {

    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: WeatherAdapter

    private lateinit var locationManager: LocationManager

    // cityList recyclerview
    private lateinit var favCityRecyclerView: RecyclerView
    private lateinit var favCityAdapter: FavCityAdapter

    private var favCityList: MutableList<FavoriteCity> = mutableListOf()

    // viewmodel
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            WeatherRepository(
                DatabaseProvider.getDatabase(requireContext()).cityDao()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 위치 관리자 초기화
        locationManager = LocationManager(requireContext())

        // ViewPager & Indicator 설정
        viewPager = view.findViewById(R.id.viewPager)
        dotsIndicator = view.findViewById(R.id.dots_indicator)

        // 빈 데이터로 초기화
        adapter = WeatherAdapter(requireActivity(), mutableListOf(), listOf())
        viewPager.adapter = adapter

        dotsIndicator.attachTo(viewPager)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigation_view)
            if (navigationView != null) {
                val headerView = navigationView.getHeaderView(0)
                favCityRecyclerView = headerView.findViewById(R.id.fav_city_recycler_view)
                favCityRecyclerView.layoutManager = LinearLayoutManager(requireContext())

                favCityAdapter = FavCityAdapter(favCityList)
                favCityRecyclerView.adapter = favCityAdapter
            } else {
                Log.e("HomeFragment", "NavigationView is null!")
            }
        }

        observeViewModel()

        getNowWeather()
    }

    /**
     * ViewModel에서 데이터 변화를 관찰하고 UI 갱신
     */
    private fun observeViewModel() {
        // 도시 리스트 변경 감지
        homeViewModel.cityList.observe(viewLifecycleOwner) { cities ->
            // Room DB에 저장된 도시 리스트를 기반으로 날씨 데이터 불러오기
            homeViewModel.fetchWeatherForCities(cities)

            // 기존 리스트 초기화 후 새로운 리스트 추가
            favCityList.clear()
            favCityList.addAll(cities.map { FavoriteCity(it.cityName) })

            // UI 업데이트
            favCityAdapter.notifyDataSetChanged()

        }

        // 날씨 데이터 변경 감지 → 리스트가 변경될 때 UI 업데이트
        homeViewModel.weatherData.observe(viewLifecycleOwner) { weatherList ->
            val cityNames = homeViewModel.cityList.value?.map { it.cityName } ?: listOf()

            // 바로 WeatherResponse 리스트를 넘김
            adapter.updateData(weatherList, cityNames)
        }
    }

    /**
     * 현재 위치 기반으로 도시명 및 날씨 데이터 가져오기
     */
    private fun getNowWeather() {
        locationManager.getLastKnownLocation(
            onSuccess = { location ->
                location?.let {
                    lifecycleScope.launch {
                        val cityName = locationManager.getCityNameFromCoord(it.latitude, it.longitude)
                        val newCity = CityEntity(cityName = cityName, latitude = it.latitude, longitude = it.longitude)

                        homeViewModel.addCity(newCity)  // Room에 저장
                        homeViewModel.fetchWeather(it.latitude, it.longitude) // 날씨 데이터 요청
                    }
                }
            },
            onFailure = { Log.e(TAG, "위치 가져오기 실패") }
        )
    }
}