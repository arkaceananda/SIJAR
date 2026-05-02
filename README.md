# SIJAR - Sistem Inventaris Peminjaman Barang Jurusan

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
</p>

SIJAR is a modern Android mobile application designed to streamline the management of department inventory and the equipment lending process. The application aims to digitize inventory tracking, making it more transparent, organized, and efficient for both administrators and users.

## Project Overview

This project is built using the latest Android development standards to provide a responsive user experience and a clean interface. SIJAR connects users to a central inventory system via API integration, allowing for real-time monitoring of item availability and lending status.

## Tech Stack

### Core
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

### Networking & Data
![Retrofit](https://img.shields.io/badge/Retrofit-orange?style=for-the-badge)
![OkHttp](https://img.shields.io/badge/OkHttp-black?style=for-the-badge)
![Gson](https://img.shields.io/badge/Gson-blue?style=for-the-badge)

### Design & UI
![Material 3](https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white)
![Coil](https://img.shields.io/badge/Coil-Image%20Loading-lightgrey?style=for-the-badge)

## Project Structure
The project is organized into functional layers for better maintainability:
- `api/model`: Data classes for API requests and responses (Single Source of Truth).
- `api/repository`: Data handling logic, Dispatchers.IO management, and API result wrapping.
- `api/service`: Retrofit interfaces for backend communication.
- `api/utils`: Utility classes including `ApiResult`, `SessionManager` (Singleton), and Network helpers.
- `viewModel`: Business logic and UI State management (MVVM).
- `ui/theme`: Global theme configuration, color palettes, and typography.
- `ui/presentation`: Jetpack Compose screens and reusable UI components.

---
*Note: This project is currently under active development.*
