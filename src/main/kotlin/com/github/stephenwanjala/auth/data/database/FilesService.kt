package com.github.stephenwanjala.auth.data.database

import com.github.stephenwanjala.auth.domain.model.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class FilesService(
    database: Database
):DatabaseService {
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
       transaction(database) {
                SchemaUtils.apply {
                    create(Files)
                    create(Subjects)
                    create(PastPapers)
                    create(PracticeQuestions)
                    create(Materials)

                }
            }

    }


    //        All subjects
    val MATHEMATICS = Subject("MATHEMATICS")
    val ENGLISH = Subject("ENGLISH")
    val KISWAHILI = Subject("KISWAHILI")
    val BIOLOGY = Subject("BIOLOGY")
    val CHEMISTRY = Subject("CHEMISTRY")
    val PHYSICS = Subject("PHYSICS")
    val GEOGRAPHY = Subject("GEOGRAPHY")
    val HISTORY = Subject("HISTORY")
    val CRE = Subject("CRE")
    val IRE = Subject("IRE")
    val HRE = Subject("HRE")
    val BUSINESS_STUDIES = Subject("BUSINESS STUDIES")
    val AGRICULTURE = Subject("AGRICULTURE")
    val COMPUTER_STUDIES = Subject("COMPUTER STUDIES")
    val HOME_SCIENCE = Subject("HOME SCIENCE")
    val ART_AND_DESIGN = Subject("ART AND DESIGN")
    val MUSIC = Subject("MUSIC")
    val FRENCH = Subject("FRENCH")
    val GERMAN = Subject("GERMAN")
    val ARABIC = Subject("ARABIC")
    val SIGN_LANGUAGE = Subject("SIGN LANGUAGE")
    val KENYA_SIGN_LANGUAGE = Subject("KENYA SIGN LANGUAGE")
    val CHINESE = Subject("CHINESE")
    val LITERATURE = Subject("LITERATURE")
    val AVIATION_TECHNOLOGY = Subject("AVIATION TECHNOLOGY")
    val BUILDING_CONSTRUCTION = Subject("BUILDING CONSTRUCTION")
    val POWER_MECHANICS = Subject("POWER MECHANICS")
    val ELECTRICAL_AND_ELECTRONICS_TECHNOLOGY = Subject("ELECTRICAL AND ELECTRONICS TECHNOLOGY")
    val DRAWING_AND_DESIGN = Subject("DRAWING AND DESIGN")
    val WOODWORK_TECHNOLOGY = Subject("WOODWORK TECHNOLOGY")
    val METALWORK_TECHNOLOGY = Subject("METALWORK TECHNOLOGY")
    val AVIATION_TECHNOLOGY_2 = Subject("AVIATION TECHNOLOGY 2")
    val BUILDING_CONSTRUCTION_2 = Subject("BUILDING CONSTRUCTION 2")
    val POWER_MECHANICS_2 = Subject("POWER MECHANICS 2")
    val ELECTRICAL_AND_ELECTRONICS_TECHNOLOGY_2 = Subject("ELECTRICAL AND ELECTRONICS TECHNOLOGY 2")
    val DRAWING_AND_DESIGN_2 = Subject("DRAWING AND DESIGN 2")
    val WOODWORK_TECHNOLOGY_2 = Subject("WOODWORK TECHNOLOGY 2")
    val METALWORK_TECHNOLOGY_2 = Subject("METALWORK TECHNOLOGY 2")
    val AVIATION_TECHNOLOGY_3 = Subject("AVIATION TECHNOLOGY 3")
    val BUILDING_CONSTRUCTION_3 = Subject("BUILDING CONSTRUCTION 3")
    val POWER_MECHANICS_3 = Subject("POWER MECHANICS 3")

  suspend fun createFile(file: RevFile): Int = dbQuery {
        Files.insert {
            it[fileName] = file.fileName
            it[fileType] = file.fileType
            it[user] = file.userId
            it[filePath] = file.filePath
        }[Files.id]
    }

    suspend fun createSubject(subject: Subject): Int = dbQuery {
        Subjects.insert {
            it[subjectName] = subject.subjectName
        }[Subjects.id].value
    }

    suspend fun createPastPaper(pastPaper: PastPaper): Int = dbQuery {
        PastPapers.insert {
            it[subject] = pastPaper.subject.id
            it[user] = pastPaper.userId
            it[file] = pastPaper.fileId
        }[PastPapers.id].value
    }

    suspend fun createPracticeQuestion(practiceQuestion: PracticeQuestion): Int = dbQuery {
        PracticeQuestions.insert {
            it[subject] = practiceQuestion.subject.id
            it[user] = practiceQuestion.user.id
            it[file] = practiceQuestion.file.id
        }[PracticeQuestions.id].value
    }

    suspend fun createMaterial(material: Material): Int = dbQuery {
        Materials.insert {
            it[type] = material.type
            it[subject] = material.subject.id
            it[user] = material.userId
            it[file] = material.file
        }[Materials.id].value
    }

    suspend fun readFile(id: Int): RevFile? {
        return dbQuery {
            Files.select { Files.id eq id }
                .map { Files.toFile(it) }
                .singleOrNull()
        }
    }

    suspend fun readSubject(id: Int): Subject? {
        return dbQuery {
            Subjects.select { Subjects.id eq id }
                .map { Subjects.toSubject(it) }
                .singleOrNull()
        }
    }

    suspend fun readPastPaper(id: Int): PastPaper? {
        return dbQuery {
            PastPapers.select { PastPapers.id eq id }
                .map { PastPapers.toPastPaper(it) }
                .singleOrNull()
    }
    }

    suspend fun readPracticeQuestion(id: Int): PracticeQuestion? {
        return dbQuery {
            PracticeQuestions.select { PracticeQuestions.id eq id }
                .map { PracticeQuestions.toPracticeQuestion(it) }
                .singleOrNull()
        }
    }

    suspend fun readMaterial(id: Int): Material? {
        return dbQuery {
            Materials.select { Materials.id eq id }
                .map { Materials.toMaterial(it) }
                .singleOrNull()
        }
    }

    suspend fun readAllFiles(): List<RevFile> {
        return dbQuery {
            Files.selectAll()
                .map { Files.toFile(it) }
        }
    }

    suspend fun readAllSubjects(): List<Subject> {
        return dbQuery {
            Subjects.selectAll()
                .map { Subjects.toSubject(it) }
        }
    }

    suspend fun readAllPastPapers(): List<PastPaper> {
        return dbQuery {
            PastPapers.selectAll()
                .map { PastPapers.toPastPaper(it) }
        }
    }

    suspend fun readAllPracticeQuestions(): List<PracticeQuestion> {
        return dbQuery {
            PracticeQuestions.selectAll()
                .map { PracticeQuestions.toPracticeQuestion(it) }
        }
    }

    suspend fun readAllMaterials(): List<Material> {
        return dbQuery {
            Materials.selectAll()
                .map { Materials.toMaterial(it) }
        }
    }

    suspend fun updateFile(id: Int, file: RevFile) {
        dbQuery {
            Files.update({ Files.id eq id }) {
                it[fileName] = file.fileName
                it[fileType] = file.fileType
                it[user] = file.userId
                it[filePath] = file.filePath
            }
        }
    }

    suspend fun updateSubject(id: Int, subject: Subject) {
        dbQuery {
            Subjects.update({ Subjects.id eq id }) {
                it[subjectName] = subject.subjectName
            }
        }
    }

    suspend fun updatePastPaper(id: Int, pastPaper: PastPaper) {
        dbQuery {
            PastPapers.update({ PastPapers.id eq id }) {
                it[subject] = pastPaper.subject.id
                it[user] = pastPaper.userId
                it[file] = pastPaper.fileId
            }
        }
    }

    suspend fun updatePracticeQuestion(id: Int, practiceQuestion: PracticeQuestion) {
        dbQuery {
            PracticeQuestions.update({ PracticeQuestions.id eq id }) {
                it[subject] = practiceQuestion.subject.id
                it[user] = practiceQuestion.user.id
                it[file] = practiceQuestion.file.id
            }
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

        fun toPastPaper(row: ResultRow): PastPaper {
            return PastPaper(
                id = row[id].value,
                subject = Subjects.toSubject(row),
                userId = UserService.Users.toUser(row).id,
                fileId = Files.toFile(row).id
            )
        }
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



    object Materials : IntIdTable() {
        val type = enumeration("material_type", MaterialType::class)
        val subject = reference("subject_id", Subjects)
        val user = reference("user_id", UserService.Users.id, onDelete = ReferenceOption.CASCADE)
        val file = reference("file_id", Files.id, onDelete = ReferenceOption.CASCADE)

        fun toMaterial(row: ResultRow): Material {
            return Material(
                id = row[id].value,
                type = row[type].name.let { MaterialType.valueOf(it) },
                subject = Subjects.toSubject(row),
                userId = UserService.Users.toUser(row).id,
                file = Files.toFile(row).id
            )
        }
    }


}