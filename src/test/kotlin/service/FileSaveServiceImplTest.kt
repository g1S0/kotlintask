package service

import org.example.dto.News
import org.example.serivce.impl.FileSaveServiceImpl
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.io.File
import java.io.IOException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.time.Instant


class FileSaveServiceImplTest {

    private fun getSampleNews(): News {
        return News(
            id = 1,
            title = "Sample News",
            place = "Sample Place",
            description = "Sample Description",
            siteUrl = "http://example.com",
            favoritesCount = 10,
            commentsCount = 5,
            publicationDate = Instant.now().epochSecond,
        )
    }

    @Test
    fun `should throw IOException when directory does not exist`() {
        val fileSaveService = FileSaveServiceImpl()

        val invalidPath = "nonexistent_directory/news.csv"

        assertThrows(IOException::class.java) {
            fileSaveService.saveNews(invalidPath, listOf(getSampleNews()))
        }
    }

    @Test
    fun `should throw FileAlreadyExistsException when file already exists`(@TempDir tempDir: File) {
        val fileSaveService = FileSaveServiceImpl()

        val existingFile = File(tempDir, "news.csv")
        existingFile.createNewFile()

        val path = existingFile.absolutePath

        assertThrows(FileAlreadyExistsException::class.java) {
            fileSaveService.saveNews(path, listOf(getSampleNews()))
        }
    }

}