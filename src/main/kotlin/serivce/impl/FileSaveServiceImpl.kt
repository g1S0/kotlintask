package org.example.serivce.impl

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.example.dto.News
import org.example.serivce.FileSaveService
import java.io.File
import java.io.IOException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.forEach

class FileSaveServiceImpl : FileSaveService {
    private val logger: Logger = LogManager.getLogger(NewsServiceImpl::class.java)

    override fun saveNews(file: File, news: Collection<News>) {
        val directory = file.parentFile
        val path = file.path

        if (news.isEmpty()) {
            throw IOException("News are empty")
        }

        if (directory == null || !directory.exists()) {
            logger.info("Directory $directory does not exist.")
            throw IOException("Failed to create directory: $directory")
        }

        if (file.exists()) {
            logger.error("File already exists at the specified path: $path")
            throw FileAlreadyExistsException(file, reason = "File already exists")
        }

        try {
            file.bufferedWriter().use { writer ->
                writer.write("ID,Title,Place,Description,Site URL,Favorites Count,Comments Count,Publication Date,Rating")
                writer.newLine()

                news.forEach { newsItem ->
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
            logger.info("News saved successfully to $path")
        } catch (e: IOException) {
            logger.error("An error occurred while saving news: ${e.message}")
            throw e
        }
    }
}