package dev.mikan.altairkit.utils.time

class DateTime {

    val year: Int
    val month: Int
    val day: Int
    val time: Time

    val valid: Boolean

    constructor(datetime: String) {
        // datetime must be formatted as: "dd/MM/yyyy HH:mm:ss"
        if (datetime.isEmpty() || !datetime.matches(Regex.fromLiteral("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"))) {
            this.valid = false
            this.day = -1
            this.month = -1
            this.year = -1
            this.time = Time(-1,-1,-1);
            return
        }

        this.valid = true
        val parts: List<String> = datetime.split(" ")
        val dateParts: List<String> = parts[0].split("/".toRegex()).dropLastWhile { it.isEmpty() }
        val timeParts: List<String> = parts[1].split(":".toRegex()).dropLastWhile { it.isEmpty() }

        this.day = dateParts[0].toInt()
        this.month = dateParts[1].toInt()
        this.year = dateParts[2].toInt()

        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        val second = timeParts[2].toInt()

        this.time = Time(hour,minute, second)
    }


    override fun toString() :String {
        return if (valid)
            String.format("%02d/%02d/%04d %02d:%02d:%02d", day, month, year, this.time.hours, this.time.minutes, this.time.seconds)
            else ""
    }

}