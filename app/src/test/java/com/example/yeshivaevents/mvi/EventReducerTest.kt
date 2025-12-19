package com.example.yeshivaevents.mvi



import com.example.yeshivaevents.model.EventsResult
import com.example.yeshivaevents.ui.state.UIState
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class EventReducerTest {

    @Test
    fun `LoadEvents sets uiState Loading`() {
        val initial = EventState()
        val next = EventReducer(initial, EventAction.LoadEvents)
        assertEquals(UIState.Loading, next.uiState)
    }

    @Test
    fun `FetchListOfEvents clears error and keeps state valid`() {
        val initial = EventState(error = Throwable("error"))
        val list = listOf(mockk<EventsResult>(relaxed = true))

        val next = EventReducer(initial, EventAction.FetchListOfEvents(list))

        assertNull(next.error)
    }


    @Test
    fun `Error sets UIState Error`() {
        val initial = EventState()
        val next = EventReducer(initial, EventAction.Error("bad"))
        assertTrue(next.uiState is UIState.Error)
    }

    @Test
    fun `SelectEvent updates selectedEvent`() {
        val initial = EventState()
        val ev = mockk<EventsResult>(relaxed = true)
        val next = EventReducer(initial, EventAction.SelectEvent(ev))
        assertEquals(ev, next.selectedEvent)
    }
}
