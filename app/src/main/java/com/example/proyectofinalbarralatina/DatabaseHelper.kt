package com.example.proyectofinalbarralatina

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "MiDB", null, 1) {

    private val CREATE_INGREDIENTES_TABLE = """
        CREATE TABLE ingredientes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            cantidad_inventario INTEGER,
            unidad_medida TEXT
        );
    """.trimIndent()

    private val CREATE_UTENSILIOS_TABLE = """
        CREATE TABLE utensilios (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            cantidad_inventario INTEGER
        );
    """.trimIndent()

    private val CREATE_PRODUCTOS_TABLE = """
        CREATE TABLE productos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            precio REAL
        );
    """.trimIndent()

    private val CREATE_PRODUCTO_INGREDIENTES_TABLE = """
        CREATE TABLE producto_ingredientes (
            id_producto INTEGER,
            id_ingrediente INTEGER,
            cantidad INTEGER,
            FOREIGN KEY(id_producto) REFERENCES productos(id),
            FOREIGN KEY(id_ingrediente) REFERENCES ingredientes(id)
        );
    """.trimIndent()

    private val CREATE_PRODUCTO_UTENSILIOS_TABLE = """
        CREATE TABLE producto_utensilios (
            id_producto INTEGER,
            id_utensilio INTEGER,
            cantidad INTEGER,
            FOREIGN KEY(id_producto) REFERENCES productos(id),
            FOREIGN KEY(id_utensilio) REFERENCES utensilios(id)
        );
    """.trimIndent()

    private val CREATE_PAQUETES_TABLE = """
        CREATE TABLE paquetes (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL,
            descripcion TEXT,
            precio REAL
        );
    """.trimIndent()

    private val CREATE_PAQUETE_PRODUCTOS_TABLE = """
        CREATE TABLE paquete_productos (
            id_paquete INTEGER,
            id_producto INTEGER,
            cantidad INTEGER,
            FOREIGN KEY(id_paquete) REFERENCES paquetes(id),
            FOREIGN KEY(id_producto) REFERENCES productos(id)
        );
    """.trimIndent()

    // Creación de la base de datos
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_INGREDIENTES_TABLE)
        db.execSQL(CREATE_UTENSILIOS_TABLE)
        db.execSQL(CREATE_PRODUCTOS_TABLE)
        db.execSQL(CREATE_PRODUCTO_INGREDIENTES_TABLE)
        db.execSQL(CREATE_PRODUCTO_UTENSILIOS_TABLE)
        db.execSQL(CREATE_PAQUETES_TABLE)
        db.execSQL(CREATE_PAQUETE_PRODUCTOS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ingredientes")
        db.execSQL("DROP TABLE IF EXISTS utensilios")
        db.execSQL("DROP TABLE IF EXISTS productos")
        db.execSQL("DROP TABLE IF EXISTS producto_ingredientes")
        db.execSQL("DROP TABLE IF EXISTS producto_utensilios")
        db.execSQL("DROP TABLE IF EXISTS paquetes")
        db.execSQL("DROP TABLE IF EXISTS paquete_productos")
        onCreate(db)
    }
}