package com.koukemo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MedicineDataOpenHelper(var mContext: Context?) : SQLiteOpenHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        //データベースのバージョン
        const val DATABASE_VERSION = 1;

        //データベース情報
        const val DATABASE_NAME = "TestDB.db";    //データベース名
        const val TABLE_NAME = "testdb";  //テーブル名
        const val _ID = "_id";    //ID

        //カラムの種類
        const val COLUMN_NAME_MEDICINE = "medicine";
        const val COLUMN_NAME_VALUE = "value";
        const val COLUMN_NAME_KIND = "kind";

        //テーブルの作成を行う変数
        private const val SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " ( " +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME_MEDICINE + " text not null, " +
                COLUMN_NAME_VALUE + " type integer not null, " +
                COLUMN_NAME_KIND + " text not null " + ");";

        //テーブルの消去を行う変数
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME + ";"

    }

    /*テーブルが存在しないときに呼び出す
    * execSQLでクエリSQL文を実行しDB構造を決定する*/
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    /*DBバージョンが上がったときに古いバージョンのテーブルを削除する
    * 新しいテーブルを生成する*/
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion : Int, newVersion : Int) {
        db?.execSQL(SQL_DELETE_ENTRIES)
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    /*DBバージョンが下がったときの処理*/
    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onDowngrade(db, oldVersion, newVersion)
    }

    /*カラムをテーブルに追加する*/
    fun saveData(db: SQLiteDatabase, medicine: String, value: Int, kind: String) {
        val values: ContentValues = ContentValues();
        values.put(COLUMN_NAME_MEDICINE, medicine)
        values.put(COLUMN_NAME_VALUE, value)
        values.put(COLUMN_NAME_KIND, kind)

        db.insertOrThrow(TABLE_NAME, null, values)
    }
}