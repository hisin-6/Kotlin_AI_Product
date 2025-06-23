package com.example.todo.controller

import com.example.todo.model.Todo
import com.example.todo.repository.TodoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todos")
class TodoController(private val todoRepository: TodoRepository) {

    @GetMapping
    fun getAllTodos(): ResponseEntity<List<Todo>> =
        ResponseEntity.ok(todoRepository.findAll())

    @PostMapping
    fun createTodo(@RequestBody todo: Todo): ResponseEntity<Todo> {
        // titleが空文字だったら400返す！ギャル厳しめ！
        if (todo.title.isBlank()) {
            return ResponseEntity.badRequest().build()
        }
        // idは無視して新規作成するよ！
        val newTodo = Todo(
            title = todo.title,
            completed = todo.completed
        )
        val saved = todoRepository.save(newTodo)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }

    @PutMapping("/{id}")
    fun updateTodo(@PathVariable id: Long, @RequestBody updatedTodo: Todo): ResponseEntity<Todo> {
        return todoRepository.findById(id).map {
            val saved = todoRepository.save(updatedTodo.copy(id = id))
            ResponseEntity.ok(saved)
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long): ResponseEntity<Void> {
        return if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}