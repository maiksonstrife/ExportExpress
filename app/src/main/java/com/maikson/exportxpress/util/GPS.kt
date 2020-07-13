package com.maikson.exportxpress.util

internal object GPS {
    private val sb = StringBuilder(20)
    /**
     * returns ref for latitude which is S or N.
     *
     * @param latitude
     * @return S or N
     */
    fun latitudeRef(latitude: Double): String {
        return if (latitude < 0.0) "S" else "N"
    }

    /**
     * returns ref for latitude which is S or N.
     *
     * @param latitude
     * @return S or N
     */
    fun longitudeRef(longitude: Double): String {
        return if (longitude < 0.0) "W" else "E"
    }

    /**
     * convert latitude into DMS (degree minute second) format. For instance<br></br>
     * -79.948862 becomes<br></br>
     * 79/1,56/1,55903/1000<br></br>
     * It works for latitude and longitude<br></br>
     *
     * @param latitude could be longitude.
     * @return
     */
    fun convert(latitude: Double): String {
        var latitude = latitude
        latitude = Math.abs(latitude)
        val degree = latitude.toInt()
        latitude *= 60.0
        latitude -= degree * 60.0
        val minute = latitude.toInt()
        latitude *= 60.0
        latitude -= minute * 60.0
        val second = (latitude * 1000.0).toInt()

        sb.setLength(0)
        sb.append(degree)
        sb.append("/1,")
        sb.append(minute)
        sb.append("/1,")
        sb.append(second)
        sb.append("/1000,")
        return sb.toString()
    }
}