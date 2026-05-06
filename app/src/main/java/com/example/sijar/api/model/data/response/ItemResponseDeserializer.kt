package com.example.sijar.api.model.data.response

import com.example.sijar.api.model.data.Item
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ItemResponseDeserializer : JsonDeserializer<ItemResponse> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ItemResponse {
        val jsonObject = json.asJsonObject
        val status = jsonObject.get("status")?.asBoolean ?: false
        val message = jsonObject.get("message")?.asString ?: ""
        val total = jsonObject.get("Totalbarangjurusan")?.asInt ?: 0

        val dataElement = jsonObject.get("data")
        val paginator = when {
            dataElement != null && dataElement.isJsonArray -> {
                val listType = object : TypeToken<List<Item>>() {}.type
                val items: List<Item> = context.deserialize(dataElement, listType)
                PagingData(
                    currentPage = 1,
                    barangList = items,
                    lastPage = 1,
                    total = items.size
                )
            }
            dataElement != null && dataElement.isJsonObject -> {
                context.deserialize(dataElement, PagingData::class.java)
            }
            else -> {
                PagingData(
                    currentPage = 1,
                    barangList = emptyList(),
                    lastPage = 1,
                    total = 0
                )
            }
        }

        return ItemResponse(
            success = status,
            message = message,
            paginator = paginator,
            total = total
        )
    }
}

