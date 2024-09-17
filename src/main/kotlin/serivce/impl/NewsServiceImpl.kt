package org.example.serivce.impl

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.dto.News
import org.example.dto.NewsResponse
import org.example.serivce.NewsService
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class NewsServiceImpl : NewsService {
    private val logger: Logger = LogManager.getLogger(NewsServiceImpl::class.java)

    companion object {
        private const val BASE_URL = "https://kudago.com/public-api/v1.4/news/"
    }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun getNews(count: Int): List<News> {
        return try {
            val response: HttpResponse = client.get(BASE_URL) {
                parameter("location", "krd")
                parameter("text_format", "text")
                parameter("expand", "place")
                parameter(
                    "fields",
                    "id,title,place,description,site_url,favorites_count,comments_count,publication_date"
                )
                parameter("page_size", count)
            }

            val newsResponse: NewsResponse = response.body()
            logger.debug("Successfully fetched ${newsResponse.results.size} news items")
            return newsResponse.results
        } catch (e: Exception) {
            logger.error("Error fetching news: ${e.message}", e)
            return emptyList()
        }
    }

    fun List<News>.getMostRatedNewsWithSequence(count: Int, period: ClosedRange<LocalDate>): List<News> {
        return this
            .filter { news ->
                val publicationDate = Instant.ofEpochSecond(news.publicationDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                publicationDate in period
            }
            .sortedByDescending { it.rating }
            .take(count)
    }

    fun List<News>.getTopRatedNewsWithLoops(count: Int, period: ClosedRange<LocalDate>): List<News> {
        val filteredNews = mutableListOf<News>()

        for (news in this) {
            val publicationDate = Instant.ofEpochSecond(news.publicationDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            if (publicationDate in period) {
                filteredNews.add(news)
            }
        }

        filteredNews.sortByDescending { it.rating }

        val result = mutableListOf<News>()
        for (i in 0 until minOf(count, filteredNews.size)) {
            result.add(filteredNews[i])
        }

        return result
    }

    override suspend fun getTopRatedNewsWithLoops(
        count: Int,
        period: ClosedRange<LocalDate>,
    ): List<News> {
        val newsList = getNews(count = count)

        return newsList.getTopRatedNewsWithLoops(count, period)
    }

    override suspend fun getTopRatedNewsWithSequence(
        count: Int,
        period: ClosedRange<LocalDate>,
    ): List<News> {
        val newsList = getNews(count = count)

        return newsList.getMostRatedNewsWithSequence(count, period)
    }
}