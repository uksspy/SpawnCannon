package me.uksspy.SpawnCannon.config;

import org.bukkit.Sound;

public class ConfigSound {

    public Sound sound;
    public float volume;
    public float pitch;

    public ConfigSound(Sound sound, float volume, float pitch){
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }
}
