package com.example.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TodoDatabase
import com.example.todoapp.data.TodoItem
import com.example.todoapp.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val repository: TodoRepository
    val todos: StateFlow<List<TodoItem>>
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    init {
        val db = TodoDatabase.getDatabase(app)
        repository = TodoRepository(db.todoDao())
        todos = repository.getAllTodos().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        // Fetch from Firestore on ViewModel creation
        viewModelScope.launch {
            _loading.value = true
            repository.fetchFromFirestoreAndUpdateRoom()
            _loading.value = false
        }
    }

    fun addTodo(title: String, description: String = "") {
        viewModelScope.launch {
            repository.insertTodo(TodoItem(title = title, description = description))
        }
    }

    fun updateTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun toggleDone(todo: TodoItem) {
        viewModelScope.launch {
            repository.updateTodo(todo.copy(isDone = !todo.isDone))
        }
    }
}