package com.github.tehras.workmode.models.settings

import android.support.annotation.IntegerRes
import com.github.tehras.workmode.models.AudioSettings

data class VolumeSettingGroup(var name: String, var ringSetting: AudioSettings, var mediaSetting: AudioSettings, var volumeLocations: VolumeLocations, @IntegerRes var image: Int)