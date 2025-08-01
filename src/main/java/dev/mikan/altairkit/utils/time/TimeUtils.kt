package dev.mikan.altairkit.utils.time

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TimeUtils {

    val zone: ZoneId = ZoneId.of("Europe/Rome")
    val formatter:DateTimeFormatter =  DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    fun current(): DateTime{
        return DateTime(formatter.format(ZonedDateTime.now(zone)))
    }

    fun add(baseDateTime: DateTime, year: Int,month:Int , day: Int, hour:Int, minute:Int,second:Int) : DateTime{
        val base = LocalDateTime.parse(baseDateTime.toString(), formatter).atZone(zone);
        val result = base
            .plusYears(year.toLong())
            .plusMonths(month.toLong())
            .plusDays(day.toLong())
            .plusHours(hour.toLong())
            .plusMinutes(minute.toLong())
            .plusSeconds(second.toLong())

        return DateTime(formatter.format(result))
    }

    fun timeLeft(fromDateTime: DateTime,toDateTime: DateTime): Time{
        val from = LocalDateTime.parse(fromDateTime.toString(), formatter).atZone(zone);
        val to = LocalDateTime.parse(toDateTime.toString(), formatter).atZone(zone);

        if (to.isBefore(from)) return Time(0,0,0);

        val duration: Duration = Duration.between(from, to)

        val totalSeconds: Long = duration.seconds
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return Time(hours.toInt(), minutes.toInt(), seconds.toInt())
    }
}