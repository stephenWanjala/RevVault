package com.github.stephenwanjala.auth.data.database

import com.github.stephenwanjala.auth.domain.model.User
import com.github.stephenwanjala.auth.security.hashing.HashingService
import com.github.stephenwanjala.auth.security.hashing.SHA256HashingService
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*


class UserService(private val database: Database): DatabaseService {
    object Users : Table() {
        val userName: Column<String> = varchar("username", 50).uniqueIndex()
        val password: Column<String> = varchar("password", 255)
        val email: Column<String> = varchar("email", 255).uniqueIndex()
        val fullName: Column<String?> = varchar("full_name", 255).nullable()
        val phoneNumber: Column<String?> = varchar("phone_number", 20).nullable()
        val salt: Column<String> = varchar("salt", 255)
        val id: Column<Int> = integer("id").autoIncrement()

        override val primaryKey = PrimaryKey(id)

        fun toUser(row: ResultRow): User =
            User(
                userName = row[userName],
                email = row[email],
                password = row[password],
                fullName = row[fullName],
                phoneNumber = row[phoneNumber],
                salt = row[salt],
                id = row[id]
            )
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
            SchemaUtils.create(Tokens)
            SchemaUtils.create(BlacklistedTokens)
        }
    }
    suspend fun create(user: User): Int = dbQuery {
        Users.insert {
            it[userName] = user.userName
            it[password] = user.password
            it[email] = user.email
            it[fullName] = user.fullName
            it[phoneNumber] = user.phoneNumber
            it[salt] = user.salt
        }[Users.id]
    }

    suspend fun read(id: Int): User? {
        return dbQuery {
            Users.select { Users.id eq id }
                .map { Users.toUser(it) }
                .singleOrNull()
        }
    }

    suspend fun readByEmail(email: String): User? {
        return dbQuery {
            Users.select { Users.email eq email }
                .map { Users.toUser(it) }
                .singleOrNull()
        }
    }

    suspend fun readByUserName(userName: String): User? {
        return dbQuery {
            Users.select { Users.userName eq userName }
                .map { Users.toUser(it) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
              it[userName] = user.userName
                it[password] = user.password
                it[email] = user.email
                it[fullName] = user.fullName
                it[phoneNumber] = user.phoneNumber
                it[salt] = user.salt
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }


    object Tokens : Table() {
        val id = integer("id").autoIncrement()
        val userId =integer("user_id")
        val token = varchar("token", 300).uniqueIndex()
    }

    object BlacklistedTokens : Table() {
        val id = integer("id").autoIncrement()
        val token = varchar("token", 300)
    }

    suspend fun createToken(userId: Int, token: String): Int = dbQuery {
        Tokens.insert {
            it[Tokens.userId] = userId
            it[Tokens.token] = token
        }[Tokens.id]
    }


    suspend fun readToken(id: Int): String? {
        return dbQuery {
            Tokens.select { Tokens.id eq id }
                .map { it[Tokens.token] }
                .singleOrNull()
        }
    }

    suspend fun deleteToken(token: String) {
        dbQuery {
            Tokens.deleteWhere { Tokens.token.eq(token) }
        }
    }

    suspend fun readTokenByUserId(userId: Int): String? {
        return dbQuery {
            Tokens.select { Tokens.userId eq userId }
                .map { it[Tokens.token] }
                .singleOrNull()
        }
    }



}
