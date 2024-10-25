package org.example

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import org.example.dsl.NewsPrinter
import org.example.dto.News
import org.example.serivce.impl.FileSaveServiceImpl
import org.example.serivce.impl.NewsServiceImpl
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File
import java.io.IOException

val logger: Logger = LogManager.getLogger("NewsServiceLogger")

fun newsPrinter(block: NewsPrinter.() -> Unit): String {
    val printer = NewsPrinter()
    printer.block()
    return printer.build()
}

fun generateNewsPrettyMarkdown(newsList: List<News>): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")
        .withZone(ZoneId.systemDefault())

    val output = newsPrinter {
        header(1) { "Latest News" }

        newsList.forEach { news ->
            header(2) { news.title }
            +"Published at: ${news.place ?: "Unknown location"}"
            +"Description: ${news.description}"
            +"Site: ${link(news.siteUrl) { news.siteUrl }}"
            +"Favorites: ${bold { news.favoritesCount.toString() }}, Comments: ${bold { news.commentsCount.toString() }}"
            +"Publication Date: ${formatter.format(Instant.ofEpochSecond(news.publicationDate))}"
            +"Rating: ${"%.2f".format(news.rating)}"
        }
    }

    return output
}

suspend fun main() {
    saveNewsToFile()
}

@OptIn(ObsoleteCoroutinesApi::class)
fun CoroutineScope.newsSaverActor(path: String = "csv/news.csv"): SendChannel<News> =
    actor(capacity = Channel.UNLIMITED) {
        val file = File(path)
        val directory = file.parentFile

        if (!directory.exists()) {
            println("Directory $directory does not exist. Creating...")
            directory.mkdirs()
        }

        if (file.exists()) {
            println("File already exists at the specified path: $path")
            throw FileAlreadyExistsException(file, reason = "File already exists")
        }

        try {
            file.bufferedWriter().use { writer ->
                writer.write("ID,Title,Place,Description,Site URL,Favorites Count,Comments Count,Publication Date,Rating")
                writer.newLine()

                for (newsItem in channel) {
                    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")
                        .withZone(ZoneId.systemDefault())
                    val date = formatter.format(Instant.ofEpochSecond(newsItem.publicationDate))

                    writer.write(
                        "${newsItem.id}," +
                                "\"${newsItem.title}\"," +
                                "\"${newsItem.place ?: "Unknown"}\"," +
                                "\"${newsItem.description}\"," +
                                "\"${newsItem.siteUrl}\"," +
                                "${newsItem.favoritesCount}," +
                                "${newsItem.commentsCount}," +
                                "\"$date\"," +
                                "%.2f".format(newsItem.rating)
                    )
                    writer.newLine()
                }
            }
            println("News saved successfully to $path")
        } catch (e: IOException) {
            println("An error occurred while saving news: ${e.message}")
            throw e
        }
    }

fun CoroutineScope.worker(
    newsChannel: SendChannel<News>,
    client: NewsServiceImpl,
    page: Int,
    dispatcher: CoroutineDispatcher
) = launch(dispatcher) {
    logger.info("Worker started for page $page")
    try {
        val newsList = client.getNews(3, page)
        logger.info("Fetched ${newsList.size} news items from page $page")
        newsList.forEach { newsItem ->
            newsChannel.send(newsItem)
        }
        logger.info("Worker finished processing page $page")
    } catch (e: Exception) {
        logger.error("Error fetching news for page $page: ${e.message}")
    }
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun saveNewsToFile() {
    val pageCount = 3
    val client = NewsServiceImpl()

    val dispatcher = newFixedThreadPoolContext(3, "news-workers")

    coroutineScope {
        val newsChannel = newsSaverActor()

        logger.info("Starting news fetching process with $pageCount pages")

        val jobs = (1..pageCount).map { page ->
            worker(newsChannel, client, page, dispatcher)
        }

        jobs.joinAll()

        logger.info("All workers completed. Closing the news channel.")
        newsChannel.close()
    }

    logger.info("News fetching and saving process completed.")
}

suspend fun fetchNews(newsService: NewsServiceImpl, count: Int = 2): List<News> {
    logger.info("Fetching $count news...")
    return newsService.getNews(count)
}

fun saveNewsToFile(fileSaveService: FileSaveServiceImpl, news: List<News>) {
    try {
        fileSaveService.saveNews(news = news)
    } catch (e: IOException) {
        logger.error(e.message)
    } catch (e: FileAlreadyExistsException) {
        logger.error("File already exists: ${e.message}")
    } catch (e: IOException) {
        logger.error(e.message)
    }
}

suspend fun processTopRatedNews(newsService: NewsServiceImpl) {
    val period = LocalDate.of(2020, 1, 1)..LocalDate.of(2024, 12, 31)

    logger.info("Fetching top-rated news with sequence...")
    val topNewsWithSequence = newsService.getTopRatedNewsWithSequence(count = 2, period = period)

    logger.info("Fetching top-rated news with loops...")
    val topNewsWithLoops = newsService.getTopRatedNewsWithLoops(count = 2, period = period)

    logger.info("Top-rated news with sequence: $topNewsWithSequence")
    logger.info("Top-rated news with loops: $topNewsWithLoops")
}

fun printFormattedNews(news: List<News>) {
    logger.info("Generating pretty Markdown format...")
    val formattedNews = generateNewsPrettyMarkdown(news)
    logger.info(formattedNews)
}