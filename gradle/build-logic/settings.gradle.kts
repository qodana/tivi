// Copyright 2023, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0


dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("../libs.versions.toml"))
        }
    }
}

//buildCache {
//    val remoteBuildCacheUrl = extra["REMOTE_BUILD_CACHE_URL"] ?: return@buildCache
//    val isCi = System.getenv().containsKey("CI")
//
//    local {
//        isEnabled = !isCi
//    }
//
//    remote(HttpBuildCache::class) {
//        url = uri(remoteBuildCacheUrl)
//        isPush = isCi
//
//        credentials {
//            username = extra["REMOTE_BUILD_CACHE_USERNAME"]?.toString()
//            password = extra["REMOTE_BUILD_CACHE_PASSWORD"]?.toString()
//        }
//    }
//}

include(":convention")
