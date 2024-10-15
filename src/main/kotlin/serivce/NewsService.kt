package org.example.serivce

import org.example.dto.News
import java.time.LocalDate

interface NewsService {
    suspend fun getNews(count: Int = 100, page: Int = 1): List<News>
    suspend fun getTopRatedNewsWithSequence(count: Int = 100, period: ClosedRange<LocalDate>): List<News>
    suspend fun getTopRatedNewsWithLoops(count: Int = 100, period: ClosedRange<LocalDate>): List<News>
}