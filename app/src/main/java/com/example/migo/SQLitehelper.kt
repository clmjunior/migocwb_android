package com.example.migo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
        db?.execSQL(createEventsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS USERS")
        db?.execSQL("DROP TABLE IF EXISTS EVENTS")
        onCreate(db)
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

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "migocwb.db"
    }
}
