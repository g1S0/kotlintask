package service

import org.example.serivce.impl.FileSaveServiceImpl
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.io.File
import java.io.IOException
import org.junit.jupiter.api.Assertions.assertThrows


class FileSaveServiceImplTest {

    @Test
    fun `should throw IOException when directory does not exist`() {
        val mockFile = mock(File::class.java)
        val mockDirectory = mock(File::class.java)

        `when`(mockFile.parentFile).thenReturn(mockDirectory)
        `when`(mockDirectory.exists()).thenReturn(false)

        val fileSaveService = FileSaveServiceImpl()

        assertThrows(IOException::class.java) {
            fileSaveService.saveNews(mockFile, listOf())
        }

        verify(mockDirectory).exists()
    }

    @Test
    fun `should throw FileAlreadyExistsException when file already exists`() {
        val mockFile = mock(File::class.java)
        val mockDirectory = mock(File::class.java)

        `when`(mockFile.parentFile).thenReturn(mockDirectory)
        `when`(mockDirectory.exists()).thenReturn(true)
        `when`(mockFile.exists()).thenReturn(true)

        val fileSaveService = FileSaveServiceImpl()

        assertThrows(FileAlreadyExistsException::class.java) {
            fileSaveService.saveNews(mockFile, listOf())
        }

        verify(mockFile).exists()
    }

}