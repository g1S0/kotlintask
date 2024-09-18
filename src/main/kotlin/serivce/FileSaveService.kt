package org.example.serivce

import org.example.dto.News
import java.io.File

interface FileSaveService {
    fun saveNews(file: File = File("csv/news.csv"), news: Collection<News>)
}