package com.mytech.mangatalkreader.data.source.impl

import com.mytech.mangatalkreader.data.source.MangaSource
import com.mytech.mangatalkreader.data.source.model.ChapterInfo
import com.mytech.mangatalkreader.data.source.model.MangaDetailInfo
import com.mytech.mangatalkreader.data.source.model.MangaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MangaDexSource(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
) : MangaSource {

    override val id: String = "mangadex"
    override val name: String = "MangaDex"
    override val baseUrl: String = "https://api.mangadex.org"

    private val coverBaseUrl = "https://uploads.mangadex.org"
    private val mangaLimit = 20

    private fun makeRequest(url: String): String {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "MangaTalkReader/1.0")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("HTTP ${response.code} for $url")
            }
            return response.body?.string() ?: throw RuntimeException("Empty response body")
        }
    }

    private fun makeRequestAsync(url: String): Result<String> {
        return runCatching { makeRequest(url) }
    }

    override suspend fun getPopularManga(page: Int): List<MangaInfo> = withContext(Dispatchers.IO) {
        val offset = (page - 1) * mangaLimit
        val url = "$baseUrl/manga?limit=$mangaLimit&offset=$offset&includes[]=cover_art&order[followedCount]=desc&contentRating[]=safe&contentRating[]=suggestive&contentRating[]=erotica"
        val responseBody = makeRequest(url)
        parseMangaList(JSONObject(responseBody))
    }

    override suspend fun searchManga(query: String, page: Int): List<MangaInfo> = withContext(Dispatchers.IO) {
        val offset = (page - 1) * mangaLimit
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        val url = "$baseUrl/manga?limit=$mangaLimit&offset=$offset&title=$encodedQuery&includes[]=cover_art&contentRating[]=safe&contentRating[]=suggestive&contentRating[]=erotica"
        val responseBody = makeRequest(url)
        parseMangaList(JSONObject(responseBody))
    }

    override suspend fun getMangaDetails(mangaId: String): MangaDetailInfo = withContext(Dispatchers.IO) {
        val url = "$baseUrl/manga/$mangaId?includes[]=cover_art&includes[]=author&includes[]=artist"
        val responseBody = makeRequest(url)
        val json = JSONObject(responseBody)
        val data = json.getJSONObject("data")
        parseMangaDetail(data)
    }

    override suspend fun getChapterList(mangaId: String): List<ChapterInfo> = withContext(Dispatchers.IO) {
        val chapters = mutableListOf<ChapterInfo>()
        var offset = 0
        var hasMore = true

        while (hasMore) {
            val url = "$baseUrl/manga/$mangaId/feed?limit=500&offset=$offset&order[volume]=desc&order[chapter]=desc&translatedLanguage[]=en&translatedLanguage[]=ja&translatedLanguage[]=ko&translatedLanguage[]=zh"
            val responseBody = makeRequest(url)
            val json = JSONObject(responseBody)
            val dataArray = json.optJSONArray("data") ?: JSONArray()

            for (i in 0 until dataArray.length()) {
                val chapterData = dataArray.getJSONObject(i)
                chapters.add(parseChapter(chapterData, mangaId))
            }

            val total = json.optInt("total", 0)
            offset += 500
            hasMore = offset < total
        }

        chapters
    }

    override suspend fun getPageList(chapterId: String): List<String> = withContext(Dispatchers.IO) {
        val serverUrl = "$baseUrl/at-home/server/$chapterId"
        val responseBody = makeRequest(serverUrl)
        val json = JSONObject(responseBody)

        val baseUrl = json.getString("baseUrl")
        val chapterHash = json.getString("hash")
        val data = json.getJSONObject("chapter")

        val images = data.optJSONArray("data") ?: JSONArray()
        val pageUrls = mutableListOf<String>()

        for (i in 0 until images.length()) {
            val fileName = images.getString(i)
            pageUrls.add("$baseUrl/data/$chapterHash/$fileName")
        }

        pageUrls
    }

    private fun parseMangaList(json: JSONObject): List<MangaInfo> {
        val results = mutableListOf<MangaInfo>()
        val dataArray = json.optJSONArray("data") ?: return results

        for (i in 0 until dataArray.length()) {
            val data = dataArray.getJSONObject(i)
            results.add(parseMangaInfo(data))
        }

        return results
    }

    private fun parseMangaInfo(data: JSONObject): MangaInfo {
        val attributes = data.getJSONObject("attributes")
        val mangaId = data.getString("id")
        val title = getBestTitle(attributes)
        val description = parseDescription(attributes)
        val author = parseAuthor(data)
        val status = parseStatus(attributes)
        val genres = parseTags(attributes)
        val year = attributes.optInt("year", 0).takeIf { it > 0 }
        val coverUrl = parseCoverUrl(data)

        return MangaInfo(
            id = mangaId,
            title = title,
            coverUrl = coverUrl,
            description = description,
            author = author,
            status = status,
            genres = genres,
            year = year
        )
    }

    private fun parseMangaDetail(data: JSONObject): MangaDetailInfo {
        val attributes = data.getJSONObject("attributes")
        val mangaId = data.getString("id")
        val title = getBestTitle(attributes)
        val altTitles = parseAltTitles(attributes)
        val description = parseDescription(attributes)
        val author = parseAuthor(data)
        val artist = parseArtist(data)
        val status = parseStatus(attributes)
        val genres = parseTags(attributes)
        val tags = parseAllTags(attributes)
        val year = attributes.optInt("year", 0).takeIf { it > 0 }
        val coverUrl = parseCoverUrl(data)

        return MangaDetailInfo(
            id = mangaId,
            title = title,
            altTitles = altTitles,
            coverUrl = coverUrl,
            description = description,
            author = author,
            artist = artist,
            status = status,
            genres = genres,
            tags = tags,
            year = year,
            chapterCount = 0
        )
    }

    private fun parseChapter(data: JSONObject, mangaId: String): ChapterInfo {
        val attributes = data.getJSONObject("attributes")
        val chapterId = data.getString("id")

        val chapterNum = when {
            attributes.has("chapter") && !attributes.isNull("chapter") ->
                attributes.optString("chapter", "0").toFloatOrNull() ?: 0f
            else -> 0f
        }

        val title = attributes.optString("title", "")
        val scanlator = attributes.optString("scanlator", "")
        val language = attributes.optString("translatedLanguage", "en")
        val uploadDate = if (attributes.has("publishAt")) {
            parseTimestamp(attributes.getString("publishAt"))
        } else {
            0L
        }
        val volume = if (attributes.has("volume") && !attributes.isNull("volume")) {
            attributes.optString("volume", null)
        } else {
            null
        }

        return ChapterInfo(
            id = chapterId,
            mangaId = mangaId,
            chapterNumber = chapterNum,
            title = title,
            scanlator = scanlator,
            language = language,
            uploadDate = uploadDate,
            volume = volume
        )
    }

    private fun getBestTitle(attributes: JSONObject): String {
        return when {
            attributes.has("title") -> {
                val titles = attributes.getJSONObject("title")
                when {
                    titles.has("en") -> titles.optString("en", "")
                    titles.has("ja") -> titles.optString("ja", "")
                    titles.has("ja-ro") -> titles.optString("ja-ro", "")
                    titles.has("ko") -> titles.optString("ko", "")
                    titles.has("ko-ro") -> titles.optString("ko-ro", "")
                    titles.has("zh") -> titles.optString("zh", "")
                    titles.has("zh-ro") -> titles.optString("zh-ro", "")
                    else -> {
                        val keys = titles.keys()
                        if (keys.hasNext()) titles.getString(keys.next()) else "Unknown"
                    }
                }
            }
            else -> "Unknown"
        }.ifBlank { "Unknown" }
    }

    private fun parseAltTitles(attributes: JSONObject): List<String> {
        val result = mutableListOf<String>()
        if (attributes.has("altTitles")) {
            val altArray = attributes.getJSONArray("altTitles")
            for (i in 0 until altArray.length()) {
                val titleObj = altArray.getJSONObject(i)
                val keys = titleObj.keys()
                if (keys.hasNext()) {
                    result.add(titleObj.getString(keys.next()))
                }
            }
        }
        return result
    }

    private fun parseDescription(attributes: JSONObject): String {
        if (!attributes.has("description")) return ""
        val descObj = attributes.getJSONObject("description")
        return when {
            descObj.has("en") -> descObj.optString("en", "")
            descObj.has("ja") -> descObj.optString("ja", "")
            else -> {
                val keys = descObj.keys()
                if (keys.hasNext()) descObj.getString(keys.next()) else ""
            }
        }
    }

    private fun parseAuthor(data: JSONObject): String {
        val relationships = data.optJSONArray("relationships") ?: return ""
        for (i in 0 until relationships.length()) {
            val rel = relationships.getJSONObject(i)
            if (rel.optString("type") == "author") {
                val attrs = rel.optJSONObject("attributes")
                return attrs?.optString("name", "") ?: ""
            }
        }
        return ""
    }

    private fun parseArtist(data: JSONObject): String {
        val relationships = data.optJSONArray("relationships") ?: return ""
        for (i in 0 until relationships.length()) {
            val rel = relationships.getJSONObject(i)
            if (rel.optString("type") == "artist") {
                val attrs = rel.optJSONObject("attributes")
                return attrs?.optString("name", "") ?: ""
            }
        }
        return ""
    }

    private fun parseStatus(attributes: JSONObject): String {
        return when (attributes.optString("status", "")) {
            "ongoing" -> "Ongoing"
            "completed" -> "Completed"
            "hiatus" -> "Hiatus"
            "cancelled" -> "Cancelled"
            else -> "Unknown"
        }
    }

    private fun parseTags(attributes: JSONObject): List<String> {
        val genres = mutableListOf<String>()
        if (!attributes.has("tags")) return genres

        val tagArray = attributes.getJSONArray("tags")
        for (i in 0 until tagArray.length()) {
            val tag = tagArray.getJSONObject(i)
            val attrs = tag.getJSONObject("attributes")
            if (attrs.optString("group") == "genre") {
                val nameObj = attrs.getJSONObject("name")
                val name = when {
                    nameObj.has("en") -> nameObj.optString("en", "")
                    else -> {
                        val keys = nameObj.keys()
                        if (keys.hasNext()) nameObj.getString(keys.next()) else ""
                    }
                }
                if (name.isNotBlank()) genres.add(name)
            }
        }
        return genres
    }

    private fun parseAllTags(attributes: JSONObject): List<String> {
        val tags = mutableListOf<String>()
        if (!attributes.has("tags")) return tags

        val tagArray = attributes.getJSONArray("tags")
        for (i in 0 until tagArray.length()) {
            val tag = tagArray.getJSONObject(i)
            val attrs = tag.getJSONObject("attributes")
            val nameObj = attrs.getJSONObject("name")
            val name = when {
                nameObj.has("en") -> nameObj.optString("en", "")
                else -> {
                    val keys = nameObj.keys()
                    if (keys.hasNext()) nameObj.getString(keys.next()) else ""
                }
            }
            if (name.isNotBlank()) tags.add(name)
        }
        return tags
    }

    private fun parseCoverUrl(data: JSONObject): String {
        val relationships = data.optJSONArray("relationships") ?: return ""
        for (i in 0 until relationships.length()) {
            val rel = relationships.getJSONObject(i)
            if (rel.optString("type") == "cover_art") {
                val fileName = rel.optString("attributes", JSONObject()).optString("fileName", "")
                if (fileName.isNotEmpty()) {
                    val mangaId = data.getString("id")
                    return "$coverBaseUrl/covers/$mangaId/$fileName"
                }
            }
        }
        return ""
    }

    private fun parseTimestamp(isoString: String): Long {
        return try {
            val cleaned = isoString.replace("Z", "+00:00")
            val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            format.timeZone = java.util.TimeZone.getTimeZone("UTC")
            format.parse(cleaned.substringBefore("+"))?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
