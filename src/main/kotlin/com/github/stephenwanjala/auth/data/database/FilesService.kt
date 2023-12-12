package com.github.stephenwanjala.auth.data.database

import com.github.stephenwanjala.auth.domain.model.PracticeQuestion
import com.github.stephenwanjala.auth.domain.model.RevFile
import com.github.stephenwanjala.auth.domain.model.Subject
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

class FilesService(
    private val database: Database
) {
    object Files : Table() {
        val fileName: Column<String> = varchar("file_name", 50).uniqueIndex()
        val fileType: Column<String> = varchar("file_type", 255)
        val user: Column<Int> = reference("user_id", UserService.Users.id)
        val filePath: Column<String> = varchar("file_path", 255)
        val id: Column<Int> = integer("id").autoIncrement()

        override val primaryKey = PrimaryKey(id)

        fun toFile(row: ResultRow): RevFile = RevFile(
            fileName = row[fileName],
            fileType = row[fileType],
            userId = row[user],
            filePath = row[filePath],
            id = row[id]
        )
    }

    init {
        SchemaUtils.apply {
            create(Files)
            create(Subjects)
            create(PastPapers)
        }

    }

    object Subjects : IntIdTable() {
        val subjectName = varchar("subject_name", 100)

        fun toSubject(row: ResultRow): Subject = Subject(
            subjectName = row[subjectName], id = row[id].value
        )
    }

    object PastPapers : IntIdTable() {
        val subject = reference("subject_id", Subjects)
        val user = reference("user_id", UserService.Users.id)
        val file = reference("file_id", Files.id)
    }

    object PracticeQuestions : IntIdTable() {
        val subject = reference("subject_id", Subjects)
        val user = reference("user_id", UserService.Users.id)
        val file = reference("file_id", Files.id)
    }

    fun PracticeQuestions.toPracticeQuestion(row: ResultRow): PracticeQuestion {
        return PracticeQuestion(
            id = row[id].value,
            subject = Subjects.toSubject(row),
            user = UserService.Users.toUser(row),
            file = Files.toFile(row)
        )
    }

}