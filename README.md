# 🛡️ OfflineID: Highway Security & Edge Biometrics

[![Platform](https://img.shields.io/badge/Platform-Native_Android-3DDC84?logo=android&logoColor=white)](#)
[![Language](https://img.shields.io/badge/Language-Java_%7C_C++-007396?logo=java&logoColor=white)](#)
[![Engine](https://img.shields.io/badge/Inference-Tencent_NCNN-FF4500?logo=tux&logoColor=white)](#)
[![Status](https://img.shields.io/badge/Build-Production_Ready-brightgreen)](#)

> **Zero-latency, air-gapped biometric authentication engineered for remote highway infrastructure.**

**OfflineID** is a hyper-optimized, native Android application designed to secure remote NHAI toll plazas, night-shift patrol zones, and restricted highway sectors. By completely eliminating cloud dependency, Supra processes state-of-the-art (SOTA) neural networks entirely on the edge device's CPU.

---

## 👥 The Team (BIT, Bangalore)
* **Lasya N S**
* **Jayadeep Gowda**
* **S Likhith Achari**
* **Judah Samuel**

---

## 🏗️ Technical Architecture
OfflineID bypasses heavy cross-platform frameworks (like Flutter/React Native) to achieve bare-metal performance.

* **Frontend UI:** Pure Native Android (Java & XML) for zero-overhead rendering.
* **Camera Pipeline:** Android CameraX API (Direct YUV byte stream manipulation).
* **The Bridge:** JNI (Java Native Interface) & Android NDK (C/C++) for high-speed communication between the UI and AI engine.
* **Inference Engine:** Tencent NCNN (Optimized for mobile ARM CPUs).

---

## 🧠 The Edge AI Pipeline
Our native C++ engine chains three SOTA models sequentially in real-time:

1. **Zero-DCE (Deep Curve Estimation):** A dynamic, mathematical "night-vision" lens. It intercepts raw camera frames and applies pixel-wise quadratic curve adjustments to illuminate faces in low light without a flash.
2. **TransRPPG (Remote Photoplethysmography):** Our anti-spoofing liveness shield. It analyzes micro-capillary color shifts in skin to calculate a live heartbeat, instantly rejecting high-res photos or deepfakes.
3. **EdgeFace:** A lightweight, optimized residual network that extracts a 512-dimensional facial vector, executing local cosine-similarity matches in milliseconds.

---

## ⚙️ Native Build Instructions
This project utilizes the Android NDK for native compilation. Follow these steps to build from source:

### 1. Prerequisites
* **Android Studio:** Ladybug or newer.
* **Android NDK:** Version `27.0.12077973` or higher.
* **CMake:** Version `3.22.1` or higher.

### 2. Configure AI Assets
Because this app runs real AI locally, you must provide the compiled NCNN model files. Place your `.param` and `.bin` files inside the assets directory:
`app/src/main/assets/`
* `zerodce_opt.param` & `zerodce_opt.bin`
* `edgeface_opt.param` & `edgeface_opt.bin`

### 3. Build Process
1. **Clone the Repo:** `git clone https://github.com/JudahSamuel/offline_id.git`
2. **Sync Gradle:** Open in Android Studio and click **Sync Now**.
3. **Clean Build:** Navigate to `Build > Clean Project`.
4. **Compile:** Connect your Android device and click **Run**. 

Android Studio will automatically trigger CMake to link the `supra_native_engine` shared library and package the AI assets into the final APK.

---

## 📜 Project Structure
```text
SupraHighwayNative/
├── app/src/main/
│   ├── assets/              <-- AI Model binary files (.bin/.param)
│   ├── cpp/
│   │   ├── native_lib.cpp   <-- JNI Logic & NCNN Inference Loop
│   │   ├── CMakeLists.txt   <-- C++ Build Configuration
│   │   └── ncnn/            <-- Pre-compiled NCNN static libraries
│   ├── java/.../            <-- CameraX & UI Controller Logic
│   └── res/layout/          <-- Native XML UI
└── build.gradle.kts         <-- Native Build Configuration
.
