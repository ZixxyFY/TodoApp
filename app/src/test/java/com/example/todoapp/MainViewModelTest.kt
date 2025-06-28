package com.example.todoapp

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.example.todoapp.data.TodoItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = MainViewModel(app)
    }

    @Test
    fun todos_flow_initializes_with_empty_list() {
        val todos = viewModel.todos.value
        assertNotNull(todos)
        assertTrue(todos.isEmpty())
    }

    @Test
    fun loading_flow_initializes_as_false() {
        val loading = viewModel.loading.value
        assertNotNull(loading)
        assertFalse(loading)
    }

    @Test
    fun addTodo_does_not_throw_exception() = runTest {
        // This test verifies that the method can be called without throwing exceptions
        viewModel.addTodo("Test Todo", "Test Description")
        // Since we're using real repository, we can't easily verify the exact behavior
        // but we can ensure the method executes without errors
    }

    @Test
    fun updateTodo_does_not_throw_exception() = runTest {
        val todo = TodoItem(title = "Test", description = "Test Desc")
        viewModel.updateTodo(todo)
        // Verify method executes without errors
    }

    @Test
    fun deleteTodo_does_not_throw_exception() = runTest {
        val todo = TodoItem(title = "Test", description = "Test Desc")
        viewModel.deleteTodo(todo)
        // Verify method executes without errors
    }

    @Test
    fun toggleDone_does_not_throw_exception() = runTest {
        val todo = TodoItem(title = "Test", description = "Test Desc", isDone = false)
        viewModel.toggleDone(todo)
        // Verify method executes without errors
    }
} 