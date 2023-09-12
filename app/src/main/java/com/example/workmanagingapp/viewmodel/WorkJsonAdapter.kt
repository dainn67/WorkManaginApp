package com.example.workmanagingapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.workmanagingapp.model.Work
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class WorkJsonAdapter : JsonSerializer<Work>, JsonDeserializer<Work> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override fun serialize(src: Work?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty("title", src?.getTitle())
        jsonObject.addProperty("time", src?.getTime()?.format(formatter))
        jsonObject.addProperty("content", src?.getContent())
        jsonObject.addProperty("isDone", src?.getStatus())
        return jsonObject
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Work {
        val jsonObject = json?.asJsonObject
        val title = jsonObject?.get("title")?.asString
        val timeString = jsonObject?.get("time")?.asString
        val time = LocalDateTime.parse(timeString, formatter)
        val content = jsonObject?.get("content")?.asString
        val isDone = jsonObject?.get("isDone")?.asBoolean ?: false
        return Work(title ?: "", time, content ?: "", isDone)
    }
}
