# 🌦️ **Weathery**
**Your Personal Weather Companion**

Weathery는 사용자가 관심 있는 지역의 날씨를 간편하게 확인하고 관리할 수 있는 안드로이드 앱입니다.  
🌍 **현재 위치** 기반 날씨와 📍 **추가한 도시**의 정보를 깔끔한 UI로 제공합니다.

---

## 📋 **Features**
### ✅ **주요 기능**
- **현재 위치 기반 날씨 조회**: 사용자의 위치를 자동으로 감지해 날씨 정보를 제공합니다.
- **다중 도시 관리**: 검색을 통해 여러 도시를 추가하고, 페이지 간 스와이프를 통해 날씨를 확인할 수 있습니다.
- **로컬 데이터 저장**: Room DB를 활용해 사용자가 추가한 도시 정보를 저장합니다.
- **구글 지도 연동**: 도시 검색 시 Google Maps API를 통해 지도와 연동됩니다.
- **날씨 데이터 API 연동**: Retrofit을 사용해 공공 데이터 API로부터 실시간 날씨 정보를 가져옵니다.

## 🛠️ **Tech Stack**
- **언어**: Kotlin
- **아키텍처**: MVVM
- **라이브러리 및 프레임워크**:
  - UI: ViewPager2, Navigation Component, Material Design, DrawerLayout
  - 지도: Google Maps API, Google Places API
  - 네트워킹: Retrofit, Gson Converter
  - 로컬 데이터: Room
  - 디자인 요소: DotsIndicator (페이지 Indicator)
- **버전 관리**: Git, GitHub

---
