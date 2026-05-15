package com.stadio.textbookstore.data

object BookStoreRepository {

    //STATE

    private val users = mutableListOf<User>()
    private val books = mutableListOf<Book>()
    private val messages = mutableListOf<Message>()

    private var currentUser: User? = null

    //ID counters so every new record gets a unique id
    private var userIdCounter = 0
    private var bookIdCounter = 0
    private var messageIdCounter = 0

    //INITIALISATION

    init {
        seedSampleData()
    }

    //USERS

    fun getAllUsers(): List<User> = users.toList()

    fun getUserById(id: String): User? = users.find { it.id == id }

    fun getUserByEmail(email: String): User? =
        users.find { it.email.equals(email, ignoreCase = true) }

    //Return true if registered, false email already taken.
    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        institution: String,
        course: String,
        studentStatus: String
    ): Boolean {
        if (getUserByEmail(email) != null) return false
        val id = "user_${++userIdCounter}"
        users.add(User(id, fullName, email, password, institution, course, studentStatus))
        return true
    }

    //Return matching user if credentials valid, null otherwise
    fun loginUser(email: String, password: String): User? {
        val user = getUserByEmail(email) ?: return null
        return if (user.password == password) user else null
    }

    fun updateUser(updatedUser: User) {
        val index = users.indexOfFirst { it.id == updatedUser.id }
        if (index != -1) {
            users[index] = updatedUser
            if (currentUser?.id == updatedUser.id) currentUser = updatedUser
        }
    }

    fun setCurrentUser(user: User?) { currentUser = user }
    fun getCurrentUser(): User? = currentUser

    //BOOKS

    fun getAllBooks(): List<Book> = books.toList()

    fun getBookById(id: String): Book? = books.find { it.id == id }

    fun getBooksBySeller(sellerId: String): List<Book> =
        books.filter { it.sellerId == sellerId }

    //Search by title, author, or ISBN, empty query returns all books
    fun searchBooks(query: String): List<Book> {
        if (query.isBlank()) return getAllBooks()
        val q = query.trim().lowercase()
        return books.filter {
            it.title.lowercase().contains(q) ||
                    it.author.lowercase().contains(q) ||
                    it.isbn.contains(q)
        }
    }

    fun addBook(
        title: String, author: String, isbn: String, price: Double,
        condition: String, description: String, sellerId: String,
        coverUri: String? = null
    ): Book {
        val id = "book_${++bookIdCounter}"
        val book = Book(id, title, author, isbn, price, condition, description, sellerId, coverUri)
        books.add(book)
        return book
    }

    fun removeBook(id: String) {
        books.removeAll { it.id == id }
    }

    //MESSAGES

    fun getMessagesBetween(userA: String, userB: String): List<Message> =
        messages.filter {
            (it.senderId == userA && it.receiverId == userB) ||
                    (it.senderId == userB && it.receiverId == userA)
        }.sortedBy { it.timestamp }

    fun getConversationsFor(userId: String): List<Message> {
        //return most recent message
        val involving = messages.filter {
            it.senderId == userId || it.receiverId == userId
        }
        val grouped = involving.groupBy {
            if (it.senderId == userId) it.receiverId else it.senderId
        }
        return grouped.values.map { msgs -> msgs.maxBy { it.timestamp } }
            .sortedByDescending { it.timestamp }
    }

    fun addMessage(
        senderId: String, receiverId: String, content: String, bookId: String? = null
    ): Message {
        val id = "msg_${++messageIdCounter}"
        val msg = Message(id, senderId, receiverId, content, System.currentTimeMillis(), bookId)
        messages.add(msg)
        return msg
    }

    //SEED DATA

    private fun seedSampleData() {
        val wikus = seedUser(
            "Wikus van de Merwe", "wikus@up.co.za", "password123",
            "University of Pretoria", "BCom Accounting Sciences", "2nd Year"
        )
        val palesa = seedUser(
            "Palesa Mokoena", "palesa@stadio.ac.za", "password123",
            "STADIO Centurion", "Bachelor of Information Technology", "4th Year"
        )
        val durell = seedUser(
            "Durell Jardim", "24301360@stadio.ac.za", "password123",
            "STADIO Pretoria", "Bachelor of Information Technology", "3rd Year"
        )

        addBook(
            "WordPress for Beginners 2021: A Visual Step-by-Step Guide to Mastering WordPress",
            "A. Williams",
            "979-8584887780",
            380.00,
            "Good",
            "Comprehensive WordPress guide with step-by-step visual instructions.",
            palesa.id
        )
        addBook(
            "Introduction to Information Systems",
            "Rainer, R.K. & Prince, B.",
            "9781119761464",
            650.00,
            "Excellent",
            "Like-new condition. Comprehensive coverage of information systems concepts.",
            palesa.id
        )
        addBook(
            "Head First Android Development: A Learner's Guide to Building Android Apps with Kotlin",
            "Griffiths, D. & Griffiths, D.",
            "978-9355420855",
            450.00,
            "Good",
            "Excellent introduction to Android development with Kotlin. Used for MADA372. Some pencil notes in the margins from study sessions.",
            durell.id
        )

        //Sample conversation between Wikus and Palesa to populate the Messages screen on first launch
        addMessage(
            wikus.id, palesa.id,
            "Hi Palesa! I'm interested in your Information Systems textbook. Is it still available?"
        )
        addMessage(
            palesa.id, wikus.id,
            "Yes it is! Are you on UP campus? I'm in Centurion but can meet halfway."
        )
    }

    private fun seedUser(
        fullName: String, email: String, password: String,
        institution: String, course: String, studentStatus: String
    ): User {
        registerUser(fullName, email, password, institution, course, studentStatus)
        return getUserByEmail(email)!!
    }
}