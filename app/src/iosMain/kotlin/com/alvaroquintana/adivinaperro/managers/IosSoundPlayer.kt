package com.alvaroquintana.adivinaperro.managers

class IosSoundPlayer : SoundPlayer {
    // Phase 6 wires AVAudioPlayer with the bundled .mp3 resources.
    override fun playSuccess() = Unit
    override fun playFail() = Unit
    override fun playBark() = Unit
}
