package com.stadio.textbookstore.data

object BookStoreRepository {

    //STATE

    private val users = mutableListOf<User>()
    private val books = mutableListOf<Book>()
    private val messages = mutableListOf<Message>()

    private var currentUser: User? = null

    // ID counters so every new record gets a unique id
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
        coverResId: Int? = null
    ): Book {
        val id = "book_${++bookIdCounter}"
        val book = Book(id, title, author, isbn, price, condition, description, sellerId, coverResId)
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
        val u1 = seedUser("Wikus van der Merwe", "wikus@up.ac.za", "password123",
            "University of Pretoria", "BCom Accounting", "Current Student")
        val u2 = seedUser("Thabo Sithole", "thabo@wits.ac.za", "password123",
            "University of Witwatersrand", "BSc Computer Science", "Current Student")
        val u3 = seedUser("Aisha Patel", "aisha@sun.ac.za", "password123",
            "Stellenbosch University", "LLB", "Current Student")
        val u4 = seedUser("Lerato Sibanyoni", "lerato@uct.ac.za", "password123",
            "University of Cape Town", "BCom Marketing", "Current Student")
        val u5 = seedUser("Sipho Ndlovu", "sipho@uj.ac.za", "password123",
            "University of Johannesburg", "BEng Mechanical", "Current Student")

        addBook("Introduction to Accounting", "Service & Brown", "978-0-123456-12-1",
            450.00, "Good", "Foundational accounting textbook covering financial statements and bookkeeping principles. Light highlighting on key chapters.", u1.id)
        addBook("Macroeconomics: A South African Perspective", "K. Botha", "978-0-654321-89-3",
            520.00, "Good", "Standard first-year macroeconomics textbook. Slight wear on the cover.", u1.id)
        addBook("Database Systems Concepts", "M. Naidoo", "978-1-234567-45-2",
            580.00, "Excellent", "Like-new condition. Used for one semester only. No markings.", u2.id)
        addBook("Principles of Marketing", "K. Pillay", "978-0-987654-32-1",
            420.00, "Fair", "Some highlighting and notes throughout. Cover has minor wear.", u4.id)
        addBook("Constitutional Law", "J. van Wyk", "978-0-555444-33-2",
            690.00, "Good", "Comprehensive coverage of South African constitutional law. Minimal markings.", u3.id)
        addBook("Engineering Mathematics", "B. Reddy", "978-0-111222-33-4",
            540.00, "Good", "Includes solved examples. Some pencil notes in the margins.", u5.id)
        addBook("Organic Chemistry", "S. Ngcobo", "978-1-666777-88-9",
            720.00, "Fair", "Well-used but functional. Important sections highlighted.", u3.id)
        addBook("Business Statistics", "L. Khumalo", "978-0-333222-11-0",
            380.00, "Excellent", "Practically new. Wrapped in plastic since purchase.", u4.id)
        addBook("Introduction to Programming with Python", "T. Mokoena", "978-1-444555-66-7",
            475.00, "Good", "Includes companion code samples online. Light usage.", u2.id)
        addBook("Financial Management", "A. Pillay", "978-0-777888-99-0",
            615.00, "Good", "Clean copy. Used for FNCE3001.", u1.id)

        addMessage(u2.id, u1.id, "Hi! Is the Macroeconomics textbook still available?")
        addMessage(u1.id, u2.id, "Yes it is. Are you on UP campus? Could meet up tomorrow.")
        addMessage(u3.id, u4.id, "Hi, I'm interested in the Marketing textbook. Will you accept R380?")
    }

    private fun seedUser(
        fullName: String, email: String, password: String,
        institution: String, course: String, studentStatus: String
    ): User {
        registerUser(fullName, email, password, institution, course, studentStatus)
        return getUserByEmail(email)!!
    }
}