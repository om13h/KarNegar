package com.karnegar.app.data

class TaskRepository(private val dao: TaskDao) {

    fun observeAll() = dao.observeAll()

    suspend fun getById(id: Long) = dao.getById(id)

    suspend fun getUpcoming(now: Long) = dao.getUpcoming(now)

    suspend fun upsert(task: Task): Long =
        if (task.id == 0L) {
            dao.insert(task)
        } else {
            dao.update(task)
            task.id
        }

    suspend fun delete(task: Task) = dao.delete(task)
}
