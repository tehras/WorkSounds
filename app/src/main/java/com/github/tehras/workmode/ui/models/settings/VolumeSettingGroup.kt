package com.github.tehras.workmode.ui.models.settings

import com.github.tehras.workmode.ui.models.AudioSettings

data class VolumeSettingGroup(var name: String, var ringSetting: AudioSettings, var mediaSetting: AudioSettings, var volumeLocations: VolumeLocations)