package dev.mikan.altairkit.utils.time

class Time {

    val hours: Int
    val minutes: Int
    val seconds: Int

    val valid: Boolean
    val zero: Boolean

    constructor(hours:Int,minutes:Int,seconds:Int){
        this.valid = hours >= 0 && minutes >= 0 && seconds >= 0
        this.zero = hours != 0 && minutes != 0 && seconds != 0

        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds
    }

    override fun toString() : String{
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }
}