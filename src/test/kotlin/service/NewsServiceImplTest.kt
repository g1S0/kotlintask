package service

import kotlinx.coroutines.runBlocking
import org.example.dto.News
import org.example.serivce.impl.NewsServiceImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId

class NewsServiceImplTest {

    fun getNewsList(): List<News> {
        return listOf(
            News(
                id = 1L,
                title = "News 1",
                place = null,
                description = "Description 1",
                siteUrl = "http://example.com/1",
                favoritesCount = 10,
                commentsCount = 2,
                publicationDate = LocalDate.of(2023, 1, 1)
                    .atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            ),
            News(
                id = 2L,
                title = "News 2",
                place = null,
                description = "Description 2",
                siteUrl = "http://example.com/2",
                favoritesCount = 20,
                commentsCount = 4,
                publicationDate = LocalDate.of(2023, 1, 2)
                    .atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            ),
            News(
                id = 3L,
                title = "News 3",
                place = null,
                description = "Description 3",
                siteUrl = "http://example.com/3",
                favoritesCount = 5,
                commentsCount = 1,
                publicationDate = LocalDate.of(2023, 1, 3)
                    .atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            ),
            News(
                id = 4L,
                title = "News 4",
                place = null,
                description = "Description 4",
                siteUrl = "http://example.com/4",
                favoritesCount = 15,
                commentsCount = 3,
                publicationDate = LocalDate.of(2023, 1, 4)
                    .atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            )
        )
    }

    @Test
    fun testGetTopRatedNewsWithLoops() = runBlocking {
        val sampleNewsList = getNewsList()

        val period = LocalDate.of(2023, 1, 2)..LocalDate.of(2023, 1, 4)

        val newsService = object : NewsServiceImpl() {
            override suspend fun getNews(count: Int): List<News> {
                return sampleNewsList
            }
        }

        val result = newsService.getTopRatedNewsWithLoops(count = 3, period = period)

        val expectedResult = listOf(
            sampleNewsList[1],
            sampleNewsList[3],
            sampleNewsList[2]
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun testGetMostRatedNewsWithSequence() = runBlocking {
        val sampleNewsList = getNewsList()

        val period = LocalDate.of(2023, 1, 2)..LocalDate.of(2023, 1, 4)

        val newsService = object : NewsServiceImpl() {
            override suspend fun getNews(count: Int): List<News> {
                return sampleNewsList
            }
        }

        val result = newsService.getTopRatedNewsWithSequence(count = 3, period = period)

        val expectedResult = listOf(
            sampleNewsList[1],
            sampleNewsList[3],
            sampleNewsList[2]
        )

        assertEquals(expectedResult, result)
    }
}