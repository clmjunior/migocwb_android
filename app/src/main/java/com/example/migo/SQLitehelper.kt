package com.example.migo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SQLitehelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = ("CREATE TABLE USERS ( "
                + "ID INTEGER PRIMARY KEY, " +
                "NAME TEXT, " +
                "LAST_NAME TEXT, " +
                "EMAIL TEXT, " +
                "PASSWORD TEXT, " +
                "CEP TEXT, " +
                "NUMBER TEXT, " +
                "UF TEXT, " +
                "CITY TEXT, " +
                "NEIGHBORHOOD TEXT, " +
                "STREET TEXT" +
                ")")

        val createEventsTable = ("CREATE TABLE EVENTS ( "
                + "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "USER_ID INTEGER NOT NULL, " +
                "TITULO TEXT NOT NULL, " +
                "IMAGEM TEXT NOT NULL, " +
                "DESCRICAO TEXT NOT NULL, " +
                "FLAG_ATIVO TEXT DEFAULT 'S' NOT NULL, " +
                "FLAG_PROMOCAO TEXT DEFAULT 'N' NOT NULL, " +
                "DESC_PROMOCAO TEXT NOT NULL, " +
                "HORARIO TEXT NOT NULL, " +
                "DATA TEXT NOT NULL, " + // Use appropriate date format later
                "CEP TEXT NOT NULL, " +
                "CIDADE TEXT NOT NULL, " +
                "UF TEXT NOT NULL, " +
                "RUA TEXT NOT NULL, " +
                "NUMERO INTEGER NOT NULL, " +
                "BAIRRO TEXT NOT NULL, " +
                "FOREIGN KEY (USER_ID) REFERENCES USERS(ID)" +
                ")")

        db?.execSQL(createUsersTable)
        Log.d("SQLiteHelper", "Tabela USERS criada com sucesso")

        db?.execSQL(createEventsTable)
        Log.d("SQLiteHelper", "Tabela EVENTS criada com sucesso")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS USERS")
        db?.execSQL("DROP TABLE IF EXISTS EVENTS")
        onCreate(db)
    }

    fun findUser(email: String, password: String): String? {
        val db = this.readableDatabase
        val query = "SELECT ID FROM USERS WHERE email = ? AND password = ?"
        Log.d("FindUser", "Query: $query, Email: $email, Password: $password")
        val cursor = db.rawQuery(query, arrayOf(email, password))

        var userId: String? = null
        if (cursor.moveToFirst()) {
            val columnNames = cursor.columnNames
            Log.d("FindUser", "Column names: ${columnNames.joinToString()}")
            Log.d("FindUser", "Cursor move to first: true")
            val idColumnIndex = cursor.getColumnIndex("ID")
            if (idColumnIndex != -1) {
                userId = cursor.getString(idColumnIndex)
                Log.d("FindUser", "UserId found: $userId")
            } else {
                Log.e("FindUser", "Column index for 'ID' not found")
            }
        } else {
            Log.d("FindUser", "Cursor move to first: false")
        }
        cursor.close()
        return userId
    }


    fun createUser(user: User): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put("ID", user.id)
        values.put("NAME", user.name)
        values.put("LAST_NAME", user.last_name)
        values.put("EMAIL", user.email)
        values.put("PASSWORD", user.password)
        values.put("CEP", user.cep)
        values.put("NUMBER", user.number)
        values.put("UF", user.uf)
        values.put("CITY", user.city)
        values.put("NEIGHBORHOOD", user.neighborhood)
        values.put("STREET", user.street)

        val status = db.insert("USERS", null, values)
        db.close()
        return status
    }

    fun createEvent(event: Event): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put("USER_ID", event.userId)
        values.put("TITULO", event.titulo)
        values.put("IMAGEM", event.imagem)
        values.put("DESCRICAO", event.descricao)
        values.put("FLAG_ATIVO", event.flagAtivo)
        values.put("FLAG_PROMOCAO", event.flagPromocao)
        values.put("DESC_PROMOCAO", event.descPromocao)
        values.put("HORARIO", event.horario)
        values.put("DATA", event.data)
        values.put("CEP", event.cep)
        values.put("CIDADE", event.cidade)
        values.put("UF", event.uf)
        values.put("RUA", event.rua)
        values.put("NUMERO", event.numero)
        values.put("BAIRRO", event.bairro)

        val status = db.insert("EVENTS", null, values)
        db.close()
        return status
    }

    fun updateEvent(event: Event): Int {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put("TITULO", event.titulo)
        values.put("DESCRICAO", event.descricao)
        values.put("HORARIO", event.horario)
        values.put("DATA", event.data)
        values.put("DESC_PROMOCAO", event.descPromocao)
        // Update other fields similarly

        // Define the WHERE clause based on event ID
        val whereClause = "ID = ?"
        val whereArgs = arrayOf(event.id.toString())

        // Perform the update operation
        val rowsAffected = db.update("EVENTS", values, whereClause, whereArgs)

        db.close()
        return rowsAffected
    }

    fun getEventById(eventId: Int): Event? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM EVENTS WHERE ID = ?", arrayOf(eventId.toString()))

        var event: Event? = null

        if (cursor.moveToFirst()) {
            val columnNames = cursor.columnNames
            Log.d("SQLiteHelper", "Column names: ${columnNames.joinToString()}")

            event = Event(
                id = cursor.getIntSafe("ID"),
                userId = cursor.getIntSafe("USER_ID"),
                titulo = cursor.getStringSafe("TITULO"),
                imagem = cursor.getStringSafe("IMAGEM"),
                descricao = cursor.getStringSafe("DESCRICAO"),
                flagAtivo = cursor.getStringSafe("FLAG_ATIVO"),
                flagPromocao = cursor.getStringSafe("FLAG_PROMOCAO"),
                descPromocao = cursor.getStringSafe("DESC_PROMOCAO"),
                horario = cursor.getStringSafe("HORARIO"),
                data = cursor.getStringSafe("DATA"),
                cep = cursor.getStringSafe("CEP"),
                cidade = cursor.getStringSafe("CIDADE"),
                uf = cursor.getStringSafe("UF"),
                rua = cursor.getStringSafe("RUA"),
                numero = cursor.getIntSafe("NUMERO"),
                bairro = cursor.getStringSafe("BAIRRO")
            )
        }

        cursor.close()
        db.close()
        return event
    }

    fun deactivateEvent(eventId: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("FLAG_ATIVO", "N")

        val rowsAffected = db.update("EVENTS", values, "ID = ?", arrayOf(eventId.toString()))
        db.close()
        return rowsAffected
    }

    fun Cursor.getStringSafe(columnName: String): String {
        val columnIndex = this.getColumnIndex(columnName)
        return if (columnIndex >= 0) this.getString(columnIndex) else ""
    }

    fun Cursor.getIntSafe(columnName: String): Int {
        val columnIndex = this.getColumnIndex(columnName)
        return if (columnIndex >= 0) this.getInt(columnIndex) else 0
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "migo.db"
    }
}
