package com.example.todoapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TodoRepository(private val dao: TodoDao) {
    fun getAllTodos(): Flow<List<TodoItem>> = dao.getAllTodos()
    suspend fun insertTodo(todo: TodoItem) {
        dao.insertTodo(todo)
        syncToFirestore()
    }
    suspend fun updateTodo(todo: TodoItem) {
        dao.updateTodo(todo)
        syncToFirestore()
    }
    suspend fun deleteTodo(todo: TodoItem) {
        dao.deleteTodo(todo)
        syncToFirestore()
    }

    private suspend fun syncToFirestore() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val todos = dao.getAllTodos().firstOrNull() ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user.uid).set(mapOf("todos" to todos)).await()
    }

    suspend fun fetchFromFirestoreAndUpdateRoom() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val doc = db.collection("users").document(user.uid).get().await()
        val todos = doc.get("todos") as? List<Map<String, Any>> ?: return
        withContext(Dispatchers.IO) {
            todos.forEach { map ->
                val id = (map["id"] as? Long)?.toInt() ?: 0
                val title = map["title"] as? String ?: ""
                val description = map["description"] as? String ?: ""
                val isDone = map["isDone"] as? Boolean ?: false
                dao.insertTodo(TodoItem(id, title, description, isDone))
            }
        }
    }
} 