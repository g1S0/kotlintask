import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.example.dto.News
import org.example.serivce.impl.NewsServiceImpl
import kotlin.test.assertEquals
import org.example.worker
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.ZoneId

class WorkerTest {
    private fun mockNewsServiceImpl(): NewsServiceImpl {
        return object : NewsServiceImpl() {
            override suspend fun getNews(count: Int, page: Int): List<News> {
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
                )
            }
        }
    }

    @Test
    fun testWorkerReturn() {
        runBlocking {
            val client = mockNewsServiceImpl()
            val newsChannel = Channel<News>(Channel.UNLIMITED)
            val dispatcher = Dispatchers.Default

            val job = launch {
                worker(newsChannel, client, 1, dispatcher)
            }

            job.join()

            val news = newsChannel.receive()

            assertEquals("News 1", news.title)

            newsChannel.close()
        }
    }
}