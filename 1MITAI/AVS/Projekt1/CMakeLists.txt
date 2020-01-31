cmake_minimum_required(VERSION 3.8)

project(ANN CXX C)
set(CMAKE_CXX_STANDARD 11)

#
# Cached properties
#
set(STEPS "0" CACHE STRING "Step(s) to compile, \"0\" (default), \"1\", \"0;1;3\"...")
set(WITH_PAPI false CACHE BOOL "Whether to compile in PAPI libraries on Linux targets")

# check for valid configuration
if (NOT STEPS MATCHES "^[0-9](;[0-9])*$")
    message(FATAL_ERROR "Please specify a valid STEPS to compile")
endif ()
if (WIN32 AND WITH_PAPI)
    message(WARNING "Forcing WITH_PAPI to false for Windows builds")
    set(WITH_PAPI false)
endif ()

#
# Compiler options
#

# Optimization level and debugging
#   Use CMAKE_BUILD_TYPE=Release (Debug, RelWithDebInfo) to specify optimization level and debugging symbols inclusion

# Set architecture
if (UNIX AND CMAKE_CXX_COMPILER_ID MATCHES "Intel")
    cmake_host_system_information(RESULT HOST QUERY HOSTNAME)
    if (HOST MATCHES "anselm")              # guaranteed Intel CPU on the supercomputer
        add_compile_options("-xAVX")
    elseif (HOST MATCHES "salomon")         # guaranteed Intel CPU on the supercomputer
        add_compile_options("-xCORE-AVX2")
    else ()                                 # more conservative option
        add_compile_options("-march=native")
    endif ()
elseif (UNIX) # non-Intel compilers
    add_compile_options("-march=native")    # the best instruction set available
elseif (WIN32 AND CMAKE_CXX_COMPILER_ID MATCHES "Intel")
    add_compile_options("/EHsc")
    cmake_host_system_information(RESULT HOST QUERY HOSTNAME)
    if (HOST MATCHES "PCO204")              # CVT computers
        add_compile_options("/QxCORE-AVX2")
    else ()
        message(WARNING "Unknown environment, assuming avx support")
        add_compile_options("/arch:avx")
    endif ()
elseif (WIN32 AND CMAKE_CXX_COMPILER_ID MATCHES "MSVC")
    message(FATAL_ERROR "Microsoft Visual C++ Compiler is inssuficient for this project")
elseif (WIN32)
    add_compile_options("-march=native")    # possibly MinGW or Clang?
else ()
    message(FATAL_ERROR "Platform not supported")
endif ()

# Intel optimization reports
if (CMAKE_CXX_COMPILER_ID MATCHES "Intel")
    if (NOT WITH_PAPI)
        if (WIN32)
            add_compile_options("/Qopt-report-phase:loop,vec")
            add_compile_options("/Qopt-report:5")
        else () # UNIX
            add_compile_options("-qopt-report-phase=loop,vec")
            add_compile_options("-qopt-report=5")
        endif ()
    else ()
        message(WARNING "Optimization reports are supressed when building with PAPI")
    endif ()
endif ()

#
# Project libraries
#

# OpenMP
find_package(OpenMP REQUIRED)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${OpenMP_CXX_FLAGS}")

# HDF5
find_package(HDF5 COMPONENTS "C" NO_MODULE QUIET)   # avoid CMake shipped HDF5 at first
if (HDF5_FOUND)
    if (WIN32)
        add_definitions("/DH5_BUILT_AS_DYNAMIC_LIB")
    endif ()
    include_directories(${HDF5_INCLUDE_DIR})
    link_libraries(${HDF5_C_SHARED_LIBRARY})
else ()
    find_package(HDF5 COMPONENTS "C" REQUIRED)      # allow CMake shipped HDF5
    add_definitions(${HDF5_DEFINITIONS})
    include_directories(${HDF5_INCLUDE_DIRS})
    link_libraries(${HDF5_LIBRARIES})
endif ()

# PAPI
if (WITH_PAPI)
    find_package(PkgConfig REQUIRED)        # use PKGCONFIG to find PAPI on the system
    pkg_check_modules(PAPI REQUIRED "papi")
    add_definitions(${PAPI_CFLAGS_OTHER} "-DWITH_PAPI")
    include_directories(${PAPI_INCLUDE_DIRS} "PapiHeader")
    link_directories(${PAPI_LIBRARY_DIRS})
    link_libraries(${PAPI_LIBRARIES})
    set(LINK_FLAGS ${LINK_FLAGS} ${PAPI_LDFLAGS_OTHER})
endif ()

foreach(STEP IN LISTS STEPS)
    message(STATUS ">>> Configuring Step${STEP} <<<")
    add_subdirectory("Step${STEP}")
endforeach ()
