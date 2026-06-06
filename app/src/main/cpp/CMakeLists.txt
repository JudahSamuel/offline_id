cmake_minimum_required(VERSION 3.10.2)
project(supra_native_engine)

# Tell CMake exactly where your pre-compiled NCNN static library files live
set(NCNN_DIR ${CMAKE_CURRENT_SOURCE_DIR}/ncnn/${ANDROID_ABI})
set(ncnn_DIR ${NCNN_DIR}/lib/cmake/ncnn)

find_package(ncnn REQUIRED)

add_library(supra_native_engine SHARED native_lib.cpp)

# Enable required optimization compiler flags for deep learning math execution
target_compile_options(supra_native_engine PRIVATE -fopenmp -frtti -fexceptions)

find_library(log-lib log)
find_library(android-lib android)

# Link the dynamic hardware libraries so NCNN can process matrices across CPU cores
target_link_libraries(supra_native_engine ncnn -static-openmp ${log-lib} ${android-lib})