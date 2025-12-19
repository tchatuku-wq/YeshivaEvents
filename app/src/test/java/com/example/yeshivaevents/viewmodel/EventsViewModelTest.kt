package com.example.yeshivaevents.viewmodel

import com.example.yeshivaevents.model.EventsResult
import com.example.yeshivaevents.repository.EventsRepository
import com.example.yeshivaevents.ui.state.UIState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EventsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: EventsRepository
    private lateinit var vm: EventsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk(relaxed = true)
        vm = EventsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshHomeScreen - Success when repository returns list`() = runTest(dispatcher) {
        val e1 = mockk<EventsResult>(relaxed = true)
        val e2 = mockk<EventsResult>(relaxed = true)
        val list = listOf(e1, e2)

        coEvery { repository.getEvents() } returns list

        vm.refreshHomeScreen()
        advanceUntilIdle()

        val state = vm.homeState.value
        assertTrue(state is UIState.Success<*>)
        val data = (state as UIState.Success<List<EventsResult>>).data
        assertEquals(list, data)
    }

    @Test
    fun `refreshHomeScreen - Empty when repository returns empty list`() = runTest(dispatcher) {
        coEvery { repository.getEvents() } returns emptyList()

        vm.refreshHomeScreen()
        advanceUntilIdle()

        assertTrue(vm.homeState.value is UIState.Empty)
    }

    @Test
    fun `refreshHomeScreen - Error when repository throws`() = runTest(dispatcher) {
        val ex = RuntimeException("boom")
        coEvery { repository.getEvents() } throws ex

        vm.refreshHomeScreen()
        advanceUntilIdle()

        val state = vm.homeState.value
        assertTrue(state is UIState.Error)
    }

    @Test
    fun `refreshDetailsScreen - Success when repository returns event`() = runTest(dispatcher) {
        val ev = mockk<EventsResult>(relaxed = true)
        coEvery { repository.getEventByLink("link") } returns ev

        vm.refreshDetailsScreen("link")
        advanceUntilIdle()

        val state = vm.detailState.value
        assertTrue(state is UIState.Success<*>)
        assertEquals(ev, (state as UIState.Success<EventsResult?>).data)
    }

    @Test
    fun `refreshDetailsScreen - Empty when repository returns null`() = runTest(dispatcher) {
        coEvery { repository.getEventByLink("link") } returns null

        vm.refreshDetailsScreen("link")
        advanceUntilIdle()

        assertTrue(vm.detailState.value is UIState.Empty)
    }

    @Test
    fun `refreshDetailsScreen - Error when repository throws`() = runTest(dispatcher) {
        coEvery { repository.getEventByLink("link") } throws RuntimeException("fail")

        vm.refreshDetailsScreen("link")
        advanceUntilIdle()

        assertTrue(vm.detailState.value is UIState.Error)
    }

    @Test
    fun `selectEvent sets detailState to Success`() = runTest(dispatcher) {
        val ev = mockk<EventsResult>(relaxed = true)

        vm.selectEvent(ev)

        val state = vm.detailState.value
        assertTrue(state is UIState.Success<*>)
        assertEquals(ev, (state as UIState.Success<EventsResult?>).data)
    }
}
