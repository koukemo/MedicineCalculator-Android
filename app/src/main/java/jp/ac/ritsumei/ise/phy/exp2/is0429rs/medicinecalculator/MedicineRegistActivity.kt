
package jp.ac.ritsumei.ise.phy.exp2.is0429rs.medicinecalculator

import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_medicine_regist.*
import java.lang.Exception


class MedicineRegistActivity : AppCompatActivity() {

    private lateinit var helper: MedicineDataOpenHelper ;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicine_regist)

        helper = MedicineDataOpenHelper(applicationContext);    //DB作成

        /*上段モード切り替え*/
        val radioGroup_modeChange : RadioGroup = findViewById(R.id.ModeChange)
        radioGroup_modeChange.setOnCheckedChangeListener  { _, checkedId : Int ->
            if (checkedId == R.id.medCul) {
                //薬計算画面への遷移
                val intent_medCul : Intent = Intent(this, MainActivity::class.java);
                startActivity(intent_medCul)

            } else if (checkedId == R.id.accounting) {
                //会計画面への遷移
                val intent_accounting : Intent = Intent(this, AccountingActivity::class.java)
                startActivity(intent_accounting)

            } else if (checkedId == R.id.history) {
                //履歴画面への遷移
                val intent_history: Intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent_history)
            }
        }


        /*薬の既存変更に関する設定*/
        /*スピナーの設定*/
        val spinner2: Spinner = findViewById<View>(R.id.spinner2) as Spinner;
        setAdapter(spinner2, medicineData_forRegist().map { it.medicineName })

        //リセットが押されたときの処理
        clear2.setOnClickListener {
            val editText_simpleValue: EditText = findViewById(R.id.reValue);
            val radioGroup_medKind: RadioGroup = findViewById(R.id.medKindRadioGroup2);

            clear_mdicineInfo(editText_simpleValue)
            clearCheck_medicimeKind(radioGroup_medKind)
        }

        //完全削除が押されたときの処理
        delete2.setOnClickListener {
            try {
                deleteData(it)
            } catch(e: Exception) {

            }
        }

        //登録が押されたときの処理
        regist2.setOnClickListener {

        }


        /*新規登録の設定*/
        //リセットが押されたときの処理
        clear.setOnClickListener {
            val editText_medNameValue: EditText = findViewById(R.id.medNameValue);
            val editText_simpleValue: EditText = findViewById(R.id.simpleValue);
            val radioGroup_medKind: RadioGroup = findViewById(R.id.medKindRadioGroup);

            clear_mdicineInfo(editText_medNameValue)
            clear_mdicineInfo(editText_simpleValue)
            clearCheck_medicimeKind(radioGroup_medKind)
        }

        //登録が押されたときの処理
        regist.setOnClickListener {
            try {
                saveData(it)

                //登録後に入力内容をクリアする
                val editText_medNameValue: EditText = findViewById(R.id.medNameValue);
                val editText_simpleValue: EditText = findViewById(R.id.simpleValue);
                val radioGroup_medKind: RadioGroup = findViewById(R.id.medKindRadioGroup);

                clear_mdicineInfo(editText_medNameValue)
                clear_mdicineInfo(editText_simpleValue)
                clearCheck_medicimeKind(radioGroup_medKind)
            } catch (e: Exception) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this);
                builder.setMessage("入力不備があります" + "\n" + "薬の名前、単価、種類を入力後に[登録]を押してください")
                builder.setTitle("警告")
                builder.setPositiveButton("OK",null)
                builder.show()
            }
        }


    }



    /**
     *データを保存する
     * @param view
     * */
    fun saveData(view: View) {
        val db: SQLiteDatabase = helper.writableDatabase;
        val values: ContentValues = ContentValues();

        //薬の名前と値段を取得
        val medNameValue = findViewById<EditText>(R.id.medNameValue)  as EditText   //薬の名前を受け取る
        val medValue = findViewById<EditText>(R.id.simpleValue) as EditText //薬の値段を受け取る

        //薬の種類を取得・ラジオボタンに関する変数
        val radioGroup_medKind : RadioGroup = findViewById(R.id.medKindRadioGroup)  //薬の種類のラジオボタン
        val radioId = radioGroup_medKind.checkedRadioButtonId;
        val medKindValue: RadioButton = radioGroup_medKind.findViewById(radioId);

        //表示させる形式に変数を変換
        val medicine: String = medNameValue.text.toString();
        val value: Int = medValue.text.toString().toInt();
        val kind: String = medKindValue.text.toString();

        values.put("medicine", medicine)
        values.put("value", value)
        values.put("kind", kind)

        db.insertOrThrow("testdb", null, values)
    }

    /**
    データを削除する
    * @param view
    * */
    fun deleteData(view: View) {
        val db: SQLiteDatabase = helper.writableDatabase;

        val spinnerParent = findViewById<View>(R.id.spinner2) as Spinner;
        val medicineNameItem = spinnerParent.selectedItem as String

        db.delete("testdb", "name=?", arrayOf(medicineNameItem))
    }


    /*薬の種類を返す関数*/
    fun medKindText() : String {
        val radioGroup_medKind : RadioGroup = findViewById(R.id.medKindRadioGroup)
        var medKindName = ""
        radioGroup_medKind.setOnCheckedChangeListener {_, checkedId : Int ->
            if (checkedId == R.id.tablet) {
                medKindName = "tablet"
            } else if (checkedId == R.id.powder) {
                medKindName = "powder"
            } else if (checkedId == R.id.liquid) {
                medKindName = "liquid"
            }
        }
        return medKindName
    }

    /*薬の種類ラジオボタンのチェックを外す関数*/
    fun clearCheck_medicimeKind (radioGroup: RadioGroup) {
        radioGroup.clearCheck()
    }

    /*入力されたテキストを空にする関数*/
    fun clear_mdicineInfo(edittext: EditText) {
        edittext.editableText.clear()
    }

    /* DBからデータをすべて取得し薬のデータを配列にして返す関数*/
    private fun medicineData_forRegist(): List<MedicineAllData_forRegist> {
        val db: SQLiteDatabase = helper.readableDatabase;
        val cursor: Cursor = db.query(
            "testdb",
            arrayOf("medicine", "value", "kind"),
            null,
            null,
            null,
            null,
            null
        )

        cursor.moveToFirst()

        val medicineList = arrayListOf<MedicineAllData_forRegist>();

        for (i in 1..cursor.count) {
            medicineList.add(MedicineAllData_forRegist(cursor.getString(0), cursor.getInt(1), cursor.getString(2)))
            cursor.moveToNext()
        }

        cursor.close()

        return medicineList
    }


    fun medicineNameData_forRegist() : List<String> {
        val medicineNameList = medicineData_forRegist().map { it.medicineName }
        return medicineNameList
    }

    fun medicineValueData_forRegist() : List<Int> {
        val medicineValueList = medicineData_forRegist().map { it.medicineValue }
        return medicineValueList
    }


    /*spinnerにString型のArrayListをセットする関数*/
    private fun setAdapter(spinner: Spinner, list: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter;
    }
}


class MedicineAllData_forRegist(val medicineName: String, val medicineValue: Int, val medicineKind: String)




