## Kotlin Coroutines Demo 🚀

This project demonstrates Kotlin Coroutines in Android and Kotlin applications. It serves as a
reference guide for understanding coroutines, their scopes, dispatchers, structured concurrency, and
reactive state management with StateFlow.
Coroutines provide a lightweight concurrency framework that simplifies writing asynchronous and
concurrent code in Kotlin. They allow you to perform long-running tasks such as network calls,
database operations, or heavy computations without blocking the main thread. This ensures a smooth
user experience in Android applications.

### 📌 Contents

- Traditional Thread vs Coroutine
- Coroutine Scopes
- launch vs async
- Dispatchers
- Suspend Functions
- UserViewModel with StateFlow

### 🧵 Traditional Thread vs Coroutine

- Traditional Thread

```
fun fetchData() {
    Thread {
        Thread.sleep(5000) // Blocks the thread
        println("Data fetched!")
    }.start()
}

```

Threads are expensive: Creating a new thread for each background task consumes system resources.
Blocking behavior: Thread.sleep() blocks the thread completely, wasting CPU cycles.

- Coroutine Approach

```
fun main() {
    GlobalScope.launch {
        delay(5000)  // Suspends but does not block
        println("Data fetched!")
    }
    Thread.sleep(6000) // Keeps main thread alive for coroutine execution
}
```

Coroutines are lightweight: Thousands of coroutines can run on a few threads.
Suspending vs blocking: delay() suspends the coroutine without blocking the underlying thread.
Efficiency: Coroutines use cooperative multitasking, resuming only when needed.

🌐 Coroutine Scopes

A CoroutineScope defines the lifecycle of coroutines. Choosing the correct scope ensures tasks are
cancelled automatically when they are no longer needed, preventing memory leaks and crashes.

1. viewModelScope
   Lifecycle bound to ViewModel. Cancels coroutines when the ViewModel is cleared.

```
class UserViewModel : ViewModel() {
    fun fetchUserData() {
        viewModelScope.launch {
            val userData = apiService.getUser(1)
        }
    }
}
```

2. lifecycleScope
   Available in Activities and Fragments. Cancels coroutines when the lifecycle owner is destroyed.

```
lifecycleScope.launch {
    val data = apiService.getData()
}
```

3. GlobalScope
   Lives for the entire application lifecycle. Not recommended, as it cannot be cancelled
   automatically.
```
GlobalScope.launch {
    val result = apiService.getSomeData()
}
```

⚡ launch vs async
Kotlin coroutines provide two primary builders: launch and async.

1. launch → fire-and-forget

- Returns a Job.
- Suitable when you don’t need to return a result.
- Commonly used for UI updates or saving data.

```
viewModelScope.launch {
    apiService.getUser(1)
    println("User fetched successfully")
}
```

2. async → concurrent + result

- Returns a Deferred<T>.
- Suitable when you need a result.
- Supports parallel execution with await().

```
 viewModelScope.launch {
    val userDeferred = async { apiService.getUser(1) }
    val postsDeferred = async { apiService.getPostsByUserId(1) }

    val user = userDeferred.await()
    val posts = postsDeferred.await()

    println("User: ${user.name}, Posts: ${posts.size}")
}
```

⚙️ Dispatchers

- Dispatchers determine which thread or thread pool a coroutine runs on:
- Dispatchers.Main → Optimized for UI updates (Android only).
- Dispatchers.IO → For network, file, or database operations.
- Dispatchers.Default → For CPU-intensive work (e.g., sorting, large calculations).
- Dispatchers.Unconfined → Starts in the current thread, resumes in any thread after suspension.

  Example:

```
launch(Dispatchers.IO) {
    val data = apiService.getData()
}
```

By using the right dispatcher, you ensure smooth UI and efficient resource usage.

⏳ Suspend Functions

- A suspend function is a special function that can be paused and resumed without blocking the
  thread.

```   
suspend fun fetchData() {
    delay(2000)
    println("Data fetch")
}
```

- delay() is non-blocking.
- suspend functions can only be called from another coroutine or suspend function.
- Encourages sequential-looking asynchronous code → improves readability.

Why StateFlow?

- Emits state updates reactively to the UI.
- Ensures the latest value is always available.
- Works seamlessly with Jetpack Compose and LiveData observers.
- Encourages unidirectional data flow (UI listens, ViewModel emits).

📖 Conclusion

- Coroutines enable asynchronous programming with simpler, cleaner, and more readable code.
- Structured concurrency ensures coroutines are scoped and automatically cancelled when not needed.
- Dispatchers optimize execution by assigning the right thread for each task.
- StateFlow provides a modern, reactive way of managing UI state.
