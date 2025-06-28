package com.example.todoapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: TodoDatabase
    private lateinit var dao: TodoDao
    private lateinit var repository: TodoRepository

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TodoDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.todoDao()
        repository = TodoRepository(dao, enableCloudSync = false)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insert_and_get_todo() = runTest {
        val todo = TodoItem(title = "Test", description = "Test Desc")
        repository.insertTodo(todo)
        val todos = repository.getAllTodos().first()
        assertEquals(1, todos.size)
        assertEquals("Test", todos[0].title)
        assertEquals("Test Desc", todos[0].description)
    }

    @Test
    fun delete_todo() = runTest {
        val todo = TodoItem(title = "Test", description = "Test Desc")
        repository.insertTodo(todo)
        val inserted = repository.getAllTodos().first().first()
        repository.deleteTodo(inserted)
        val todos = repository.getAllTodos().first()
        assertTrue(todos.isEmpty())
    }

    @Test
    fun update_todo() = runTest {
        val todo = TodoItem(title = "Test", description = "Test Desc")
        repository.insertTodo(todo)
        val inserted = repository.getAllTodos().first().first()
        val updated = inserted.copy(title = "Updated", isDone = true)
        repository.updateTodo(updated)
        val todos = repository.getAllTodos().first()
        assertEquals("Updated", todos[0].title)
        assertTrue(todos[0].isDone)
    }

    @Test
    fun get_all_todos_returns_empty_list_when_no_todos() = runTest {
        val todos = repository.getAllTodos().first()
        assertTrue(todos.isEmpty())
    }

    @Test
    fun multiple_todos_are_ordered_by_id_desc() = runTest {
        val todo1 = TodoItem(title = "First")
        val todo2 = TodoItem(title = "Second")
        val todo3 = TodoItem(title = "Third")
        
        repository.insertTodo(todo1)
        repository.insertTodo(todo2)
        repository.insertTodo(todo3)
        
        val todos = repository.getAllTodos().first()
        assertEquals(3, todos.size)
        // Should be ordered by id DESC, so newest first
        assertEquals("Third", todos[0].title)
        assertEquals("Second", todos[1].title)
        assertEquals("First", todos[2].title)
    }
} 