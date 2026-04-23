<p align="center">
  <img src="docs/img/Logo_Completo.png" width="200"/>
</p>

<h1 align="center">ServiYa 🇨🇷</h1>

<p align="center">
  <b>Plataforma móvil para conectar clientes con trabajadores de servicios profesionales</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.3.0-purple" />
  <img src="https://img.shields.io/badge/Compose_Multiplatform-1.10.0-blue" />
  <img src="https://img.shields.io/badge/KMP-Android%20%7C%20iOS-green" />
  <img src="https://img.shields.io/badge/Firebase-Enabled-orange" />
</p>

---

## 📌 Description

**ServiYa** is a mobile application built with **Kotlin Multiplatform** that connects clients with service professionals.  
It allows users to explore service categories, view worker profiles, request appointments, and manage service workflows from both **client** and **worker** perspectives.

The project follows modern mobile development practices using shared UI, clean architecture principles, and scalable modular design.

---

## 🛠️ Features

### 👤 Client Features
- Browse service categories
- View workers list by category
- View professional profiles
- Request appointments
- Track appointment status
- View appointment details
- Manage requests and bookings
- Save favorite workers
- Client dashboard with activity overview

### 🧑‍🔧 Worker Features
- Worker dashboard overview
- Manage incoming service requests
- Accept / reject requests
- View request details
- Verify payments
- Manage appointments lifecycle:
    - Confirm payment
    - Start appointment (OTP validation)
    - Complete appointment
    - Cancel appointment
- Monthly calendar (agenda view)
- Appointment detail screen

### 🔐 Appointment Flow
- Client creates appointment request
- Worker accepts/rejects
- Client uploads payment (in progress)
- Worker verifies payment
- Worker starts appointment using OTP
- Worker completes service

---

## 🚧 In Progress / Planned Features

- Payment receipt upload (client side)
- Interactive map (workers & services)
- Real-time notifications
- Chat system (client ↔ worker)
- Profile management
- Worker portfolio
- Worker services management
- Worker schedule configuration
- Reports & analytics

---

## 📦 Project Structure

This is a **Kotlin Multiplatform (KMP)** project targeting Android and iOS.

### 🔹 composeApp
- UI built with Compose Multiplatform
- Navigation
- Screens (client & worker)
- Components (BottomBar, Menu, etc.)

### 🔹 shared
- Clean architecture layers:
    - data → local + remote + repository
    - domain → entities & business logic
    - presentation → ViewModels
- Firebase integration (Auth & Firestore)
- Ktor (networking)
- SQLDelight (prepared for persistence)

### 🔹 iosApp
- iOS entry point
- SwiftUI wrapper for Compose UI

---

## ⚙️ Tech Stack

### 🧠 Core
- Kotlin Multiplatform
- Compose Multiplatform
- Kotlin Coroutines
- Kotlinx Serialization

### 🔗 Architecture & DI
- Koin (Dependency Injection)

### ☁️ Backend & Services
- Firebase Authentication
- Firebase Firestore
- Firebase Analytics (ready)

### 🌐 Networking
- Ktor Client

### 💾 Persistence
- SQLDelight (configured, not fully used)

### 🎨 UI & Navigation
- Jetpack Navigation Compose
- Compose Material3
- Compose Animation
- Kamel (image loading)

---

## 🧱 Architecture

The project follows a **Clean Architecture + MVVM** approach:

- Separation of concerns
- Shared business logic across platforms
- Scalable and testable structure

---

## 📱 Platform Status

- ✅ Android → Fully functional (tested on Pixel 8 Pro emulator)
- ⚠️ iOS → Structure ready (not fully tested yet)

---

## ⚙️ Project Setup

### 🔧 Prerequisites
- Android Studio (latest)
- JDK 11+
- Xcode (for iOS)
- Firebase project configured

---

## 📱 Run Android

```bash
# macOS / Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

To install on device/emulator:

```bash
./gradlew :composeApp:installDebug
```
---

## 🍎 Run iOS

- Open the project in Android Studio

- Or open /iosApp in Xcode

- Run on simulator or device

---

## 🔐 Firebase Setup

- Create a Firebase project

- Download google-services.json

- Place it inside:

- /composeApp/google-services.json

- Enable:

- Authentication

- Firestore

---

## 📊 Versioning

- versionCode = 1

- versionName = 1.0

---

## 🧪 Methodology

- This project is developed using Agile (Scrum) methodology:

- Sprint-based development

- Task distribution per team member

- Incremental feature delivery

---

## 📄 License

- MIT License

- Copyright (c) 2026 ServiYa

---

## 🚀 Final Notes

- ServiYa demonstrates:

- Modern Kotlin Multiplatform architecture

- Shared UI across platforms

- Real-world service workflow management

- Scalable and modular design
