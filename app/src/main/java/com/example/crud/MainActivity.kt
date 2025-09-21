package com.example.crud

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var bancoDados: SQLiteDatabase
    private lateinit var listViewDados: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajuste de padding para edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listViewDados = findViewById(R.id.listViewDados)

        criarBancoDados()
        inserirDados()
        listarDados()
    }

    private fun criarBancoDados() {
        try {
            bancoDados = openOrCreateDatabase("crud.db", MODE_PRIVATE, null)
            bancoDados.execSQL(
                "CREATE TABLE IF NOT EXISTS pessoas (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nome TEXT, " +
                        "idade INTEGER)"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun inserirDados() {
        try {
            bancoDados = openOrCreateDatabase("crud.db", MODE_PRIVATE, null)
            val sql = "INSERT INTO pessoas (nome, idade) VALUES (?, ?)"
            val stmt: SQLiteStatement = bancoDados.compileStatement(sql)
            stmt.bindString(1, "João")
            stmt.bindLong(2, 30)
            stmt.executeInsert()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bancoDados.close()
        }
    }

    private fun listarDados() {
        try {
            bancoDados = openOrCreateDatabase("crud.db", MODE_PRIVATE, null)
            val cursor = bancoDados.rawQuery("SELECT * FROM pessoas", null)

            val dados = ArrayList<String>()
            if (cursor.moveToFirst()) {
                do {
                    val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
                    val idade = cursor.getInt(cursor.getColumnIndexOrThrow("idade"))
                    dados.add("$nome - $idade anos")
                } while (cursor.moveToNext())
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dados)
            listViewDados.adapter = adapter

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            bancoDados.close()
        }
    }
}
