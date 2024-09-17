package org.example.serivce.impl

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.example.dto.News
import org.example.serivce.FileSaveService
import java.io.File
import java.io.IOException
import kotlin.collections.forEach

class FileSaveServiceImpl : FileSaveService {
    private val logger: Logger = LogManager.getLogger(NewsServiceImpl::class.java)

    override fun saveNews(path: String, news: Collection<News>) {
        val file = File(path)
        val directory = file.parentFile

        if (!directory.exists()) {
            logger.info("Directory $directory does not exist.")
            return
        }

        if (file.exists()) {
            logger.error("File already exists at the specified path: $path")
            return
        }

        try {
            file.bufferedWriter().use { writer ->
                writer.write("ID,Title,Place,Description,Site URL,Favorites Count,Comments Count,Publication Date,Rating")
                writer.newLine()

                news.forEach { newsItem ->
                    writer.write(
                        "${newsItem.id}," +
                                "\"${newsItem.title}\"," +
                                "\"${newsItem.place ?: "Unknown"}\"," +
                                "\"${newsItem.description}\"," +
                                "\"${newsItem.siteUrl}\"," +
                                "${newsItem.favoritesCount}," +
                                "${newsItem.commentsCount}," +
                                "${newsItem.publicationDate}," +
                                "%.2f".format(newsItem.rating)
                    )
                    writer.newLine()
                }
            }
            logger.info("News saved successfully to $path")
        } catch (e: IOException) {
            logger.error("An error occurred while saving news: ${e.message}")
            return
        }
    }
}