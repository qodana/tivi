// Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package app.tivi.util

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.PowerManager
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import app.tivi.settings.TiviPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import me.tatarka.inject.annotations.Inject

@Inject
class AndroidPowerController(
    private val context: Application,
    private val preferences: TiviPreferences,
) : PowerController {
    private val powerManager: PowerManager = context.getSystemService()!!
    private val connectivityManager: ConnectivityManager = context.getSystemService()!!

    override fun observeShouldSaveData(ignorePreference: Boolean): Flow<SaveData> {
        return merge(
            context.flowBroadcasts(IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)),
            context.flowBroadcasts(IntentFilter(ConnectivityManager.ACTION_RESTRICT_BACKGROUND_CHANGED)),
        ).map {
            shouldSaveData()
        }.onStart {
            emit(shouldSaveData())
        }
    }

    override fun shouldSaveData(): SaveData = when {
        preferences.useLessData -> {
            SaveData.Enabled(SaveDataReason.PREFERENCE)
        }

        powerManager.isPowerSaveMode -> {
            SaveData.Enabled(SaveDataReason.SYSTEM_POWER_SAVER)
        }

        Build.VERSION.SDK_INT >= 24 && isBackgroundDataRestricted() -> {
            SaveData.Enabled(SaveDataReason.SYSTEM_DATA_SAVER)
        }

        else -> SaveData.Disabled
    }

    @RequiresApi(24)
    private fun isBackgroundDataRestricted(): Boolean {
        return connectivityManager.restrictBackgroundStatus ==
            ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED
    }
}

private fun Context.flowBroadcasts(intentFilter: IntentFilter): Flow<Intent> {
    val resultChannel = MutableStateFlow(Intent())

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            resultChannel.value = intent
        }
    }

    return resultChannel.onStart { registerReceiver(receiver, intentFilter) }
        .onCompletion { unregisterReceiver(receiver) }
}
