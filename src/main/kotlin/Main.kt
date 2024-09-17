package org.example

import org.example.dsl.NewsPrinter
import org.example.dto.News
import org.example.serivce.impl.FileSaveServiceImpl
import org.example.serivce.impl.NewsServiceImpl
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
    val newsService = NewsServiceImpl()
    val fileSaveService = FileSaveServiceImpl()

    // Get news
    val news = fetchNews(newsService)

    // Save to file
    saveNewsToFile(fileSaveService, news)

    // Highest rating
    processTopRatedNews(newsService)

    // Markdown
    printFormattedNews(news)
}

suspend fun fetchNews(newsService: NewsServiceImpl, count: Int = 2): List<News> {
    println("Fetching $count news...")
    return newsService.getNews(count)
}

fun saveNewsToFile(fileSaveService: FileSaveServiceImpl, news: List<News>) {
    fileSaveService.saveNews(news = news)
}

suspend fun processTopRatedNews(newsService: NewsServiceImpl) {
    val period = LocalDate.of(2020, 1, 1)..LocalDate.of(2024, 12, 31)

    println("Fetching top-rated news with sequence...")
    val topNewsWithSequence = newsService.getTopRatedNewsWithSequence(count = 2, period = period)

    println("Fetching top-rated news with loops...")
    val topNewsWithLoops = newsService.getTopRatedNewsWithLoops(count = 2, period = period)

    println("Top-rated news with sequence: $topNewsWithSequence")
    println("Top-rated news with loops: $topNewsWithLoops")
}

fun printFormattedNews(news: List<News>) {
    println("Generating pretty Markdown format...")
    val formattedNews = generateNewsPrettyMarkdown(news)
    println(formattedNews)
}