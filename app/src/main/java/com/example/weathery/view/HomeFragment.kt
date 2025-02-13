package com.example.weathery.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.weathery.R
import com.example.weathery.adapter.FavCityAdapter
import com.example.weathery.adapter.HomeAdapter
import com.example.weathery.data.local.DatabaseProvider
import com.example.weathery.data.repository.WeatherRepository
import com.example.weathery.model.FavoriteCity
import com.example.weathery.model.WeatherUiModel
import com.example.weathery.utils.LocationManager
import com.example.weathery.viewmodel.HomeViewModel
import com.example.weathery.viewmodel.HomeViewModelFactory
import com.example.weathery.viewmodel.WeatherViewModel
import com.google.android.material.navigation.NavigationView
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

private const val TAG = "HomeFragment"

/**
 * 메인 화면의 UI를 담당하는 클래스
 * - ViewPager2르 사용해 현재 위치 날씨와 저장된 도시의 날씨를 표시
 */
class HomeFragment : Fragment() {

    // ViewPager
    private lateinit var dotsIndicator: DotsIndicator
    private lateinit var viewPager: ViewPager2
    private lateinit var homeAdapter: HomeAdapter

    // 현재 위치를 가져옴
    private lateinit var locationManager: LocationManager

    // RecyclerVIew (Drawer)
    private lateinit var favCityRecyclerView: RecyclerView
    private lateinit var favCityAdapter: FavCityAdapter

    // 즐겨찾는 도시 목록을 저장하는 리스트
    private var favCityList: MutableList<FavoriteCity> = mutableListOf()

    // ViewModel
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            WeatherRepository(
                DatabaseProvider.getDatabase(requireContext()).cityDao()
            )
        )
    }

    private val weatherViewModel: WeatherViewModel by activityViewModels()

    private var savedWeatherList: List<WeatherUiModel> = listOf()
    private var savedCityNames: List<String> = listOf()
    private var currentWeather: WeatherUiModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationManager = LocationManager(requireContext())

        viewPager = view.findViewById(R.id.viewPager)
        dotsIndicator = view.findViewById(R.id.dots_indicator)

        homeAdapter = HomeAdapter(
            requireActivity(),
            listOf(),
            listOf(),
            null
        )

        updateHomeAdapter()

        viewPager.adapter = homeAdapter

        dotsIndicator.attachTo(viewPager)

        // 현재 위치의 날씨 정보 가져오기
        fetchCurrentLocationWeather()

        // Drawer 설정
        setupNavigationView()

        // ViewModel LiveData 관찰 설정
        observeViewModel()
    }

    // setup drawer
    private fun setupNavigationView() {
        try {
            val navigationView =
                requireActivity().findViewById<NavigationView>(R.id.navigation_view)
            navigationView?.let { nav ->
                val headerView = nav.getHeaderView(0)
                favCityRecyclerView = headerView.findViewById(R.id.fav_city_recycler_view)
                favCityRecyclerView.layoutManager = LinearLayoutManager(requireContext())

                // 어댑터 초기화 및 설정
                favCityAdapter = FavCityAdapter().apply {
                    setOnItemClickListener(object : FavCityAdapter.OnItemClickListener {
                        override fun onItemClick(city: FavoriteCity) {
                            // 클릭 이벤트 처리
                        }
                    })
                }

                favCityRecyclerView.adapter = favCityAdapter
            } ?: run {
                Log.e(TAG, "NavigationView is null! 기본 어댑터 생성")
                favCityAdapter = FavCityAdapter() // 기본 어댑터 생성
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up NavigationView", e)
            favCityAdapter = FavCityAdapter() // 예외 발생 시 기본 어댑터 생성
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        if (!::favCityAdapter.isInitialized) {
            Log.e(TAG, "favCityAdapter가 초기화되지 않음!")
            return
        }

        homeViewModel.cityList.observe(viewLifecycleOwner) { cities ->
            favCityList.clear()
            favCityList.addAll(cities.map { FavoriteCity(it.cityName) })
            favCityAdapter.updateList(cities.map { FavoriteCity(it.cityName) })
            favCityAdapter.notifyDataSetChanged()
        }

        homeViewModel.weatherList.observe(viewLifecycleOwner) {
            updateHomeAdapter()
        }

        homeViewModel.currentWeather.observe(viewLifecycleOwner) { currentWeather ->
            currentWeather?.let {
                weatherViewModel.updateWeatherData(it)
                updateHomeAdapter()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateHomeAdapter() {
        savedCityNames = homeViewModel.cityList.value?.map { it.cityName } ?: emptyList()
        savedWeatherList = homeViewModel.weatherList.value ?: emptyList()
        currentWeather = homeViewModel.currentWeather.value

        homeAdapter.updateData(
            savedWeatherList.map { it },
            savedCityNames,
            currentWeather
        )
        homeAdapter.notifyDataSetChanged()
    }

    private fun fetchCurrentLocationWeather() {
        locationManager.getLastKnownLocation(
            onSuccess = {
                if (it != null) {
                    Log.d(TAG, "${it.latitude}, ${it.longitude}")
                    val cityName = locationManager.getCityNameFromCoord(it.latitude, it.longitude)

                    // ViewModel을 통해 API에서 날씨 정보 요청
                    homeViewModel.fetchCurrentLocationWeather(it.latitude, it.longitude)
                }
            },
            onFailure = {
                Log.e(TAG, "위치 가져오기 실패")
            }
        )
    }
}
