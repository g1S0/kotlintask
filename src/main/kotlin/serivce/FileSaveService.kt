package org.example.serivce

import org.example.dto.News

interface FileSaveService {
    fun saveNews(path: String =  "csv/news.csv", news: Collection<News>)
}