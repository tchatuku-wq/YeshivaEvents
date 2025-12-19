package com.example.yeshivaevents.repository


import com.example.yeshivaevents.data.EventDao
import com.example.yeshivaevents.model.EventsResult
import com.example.yeshivaevents.network.EventApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventsRepositoryTest {

    @Test
    fun `getEvents inserts into dao when api returns non-empty`() = runTest {
        // test body...
    }




    @Test
    fun `getEventByLink delegates to dao`() = kotlinx.coroutines.runBlocking {
        val api = mockk<EventApiService>(relaxed = true)
        val dao = mockk<EventDao>()
        val repo = EventsRepository(api, dao)

        val ev = mockk<EventsResult>(relaxed = true)
        coEvery { dao.getEventByLink("link") } returns ev

        val result = repo.getEventByLink("link")
        assertEquals(ev, result)
        coVerify(exactly = 1) { dao.getEventByLink("link") }
    }
}
