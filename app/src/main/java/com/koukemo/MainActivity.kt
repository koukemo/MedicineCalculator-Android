package com.koukemo

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.graphics.Color

class MainActivity : AppCompatActivity() {

    private lateinit var helper: MedicineDataOpenHelper ;

    var medicineValues = arrayListOf<Double>(); //薬ごとに計算し終えた値を保存しておく配列
    var medicineValueTime : Double = 0.0;  //一時的に薬の計算を行うための数
    var lastValue : Double = 0.0;   //合計の値

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        helper = MedicineDataOpenHelper(applicationContext);    //DB作成

        /*上段モード切り替え*/
        val radioGroup_modeChange: RadioGroup = findViewById(R.id.ModeChange)
        radioGroup_modeChange.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == R.id.medList) {
                //薬登録画面への遷移
                val intent_medReg: Intent = Intent(this, MedicineRegisterActivity::class.java)
                startActivity(intent_medReg)

            } else if (checkedId == R.id.accounting) {
                //会計画面への遷移
                val intent_accounting: Intent = Intent(this, AccountingActivity::class.java)
                startActivity(intent_accounting)

            } else if (checkedId == R.id.history) {
                //履歴画面への遷移
                val intent_history: Intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent_history)
            }
        }


        /*スピナーの設定*/
        val spinner: Spinner = findViewById<View>(R.id.spinner) as Spinner;
        setAdapter(spinner, medicineData().map { it.medicineName })

        //リスナを登録
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            //アイテムが選択されたとき
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val spinnerParent = parent as Spinner
                val medicineNameItem = spinnerParent.selectedItem as String
                var i = 0;
                while (medicineNameData()[i] != medicineNameItem) {
                    i++
                }

                medicineValueTime = medicineValueData()[i].toDouble();     //スピナで選択した薬名から値段を取得
            }

            //アイテムが選択されなかったとき
            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }

        val radioButtonSID: RadioButton = findViewById(R.id.SID);
        val radioButtonBID: RadioButton = findViewById(R.id.BID);
        val radioButtonTID: RadioButton = findViewById(R.id.TID);
        val radioButtonQID: RadioButton = findViewById(R.id.QID);
        val radioButtonOTD: RadioButton = findViewById(R.id.OTD);

        /*使用回数に関する設定*/
        radioButtonSID.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 1;
            try {
                //先に日数が入力されたときの処理
                val editText: EditText = findViewById(R.id.daysValue);
                val daysStr: String = editText.text.toString();
                val daysInt: Int = Integer.parseInt(daysStr);
                medicineValueTime = medicineValueTime!! * alternative_decision(-1, daysInt);
            } catch (e: Exception) { }

            //ボタン設定
            radioButtonDisnabled_medcineTimes()
        }

        radioButtonBID.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 2;
            try {
                //先に日数が入力されたときの処理
                val editText: EditText = findViewById(R.id.daysValue);
                val daysStr: String = editText.text.toString();
                val daysInt: Int = Integer.parseInt(daysStr);
                medicineValueTime = medicineValueTime!! * alternative_decision(-1, daysInt);
            } catch (e: Exception) { }

            //ボタン設定
            radioButtonDisnabled_medcineTimes()
        }

        radioButtonTID.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 3;
            try {
                //先に日数が入力されたときの処理
                val editText: EditText = findViewById(R.id.daysValue);
                val daysStr: String = editText.text.toString();
                val daysInt: Int = Integer.parseInt(daysStr);
                medicineValueTime = medicineValueTime!! * alternative_decision(-1, daysInt);
            } catch (e: Exception) { }

            //ボタン設定
            radioButtonDisnabled_medcineTimes()
        }

        radioButtonQID.setOnClickListener {
            try {
                //先に日数が入力されたときの処理
                val editText: EditText = findViewById(R.id.daysValue);
                val daysStr: String = editText.text.toString();
                val daysInt: Int = Integer.parseInt(daysStr);

                val flagQID = if (Integer.parseInt(daysStr) % 2 == 0) {
                    0;
                } else {
                    1;
                }   //奇数の日数は切り上げる

                medicineValueTime = medicineValueTime!! * alternative_decision(flagQID, daysInt);
            } catch (e: Exception) { }

            //ボタン設定
            radioButtonDisnabled_medcineTimes()
        }

        radioButtonOTD.setOnClickListener {
            try {
                //先に日数が入力されたときの処理
                val editText: EditText = findViewById(R.id.daysValue);
                val daysStr: String = editText.text.toString();
                val daysInt: Int = Integer.parseInt(daysStr);

                val flagOTD = when {
                    Integer.parseInt(daysStr) % 3 == 0 -> 2
                    Integer.parseInt(daysStr) % 3 == 1 -> 3
                    else -> 4
                }   //余りは切り上げる

                medicineValueTime = medicineValueTime!! * alternative_decision(flagOTD, daysInt);
            }catch (e: Exception){ }

            //ボタン設定
            radioButtonDisnabled_medcineTimes()
        }

        val oeT: Button = findViewById(R.id.oeT);
        val ofT: Button = findViewById(R.id.ofT);
        val otT: Button = findViewById(R.id.otT);
        val oneT: Button = findViewById(R.id.oneT);
        val twoT: Button = findViewById(R.id.twoT);
        val threeT: Button = findViewById(R.id.threeT);

        val decision: Button = findViewById(R.id.decision);
        val delete: Button = findViewById(R.id.delete);
        val allClear: Button = findViewById(R.id.allclear);
        val add: Button = findViewById(R.id.add);
        val equal: Button = findViewById(R.id.equal);

        /*錠数に関する設定*/
        oeT.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 1/4;  //1/8 convert 1/4

            //ボタン設定
            val button_oeT: Button = findViewById(R.id.oeT);    // 1/8
            buttonDisnabled_tabs(button_oeT)
        }

        ofT.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 1/4;

            //ボタン設定
            val button_ofT: Button = findViewById(R.id.ofT);    // 1/4
            buttonDisnabled_tabs(button_ofT)
        }

        otT.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 1/2;

            //ボタン設定
            val button_otT: Button = findViewById(R.id.otT);    // 1/2
            buttonDisnabled_tabs(button_otT)
        }

        oneT.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 1;

            //ボタン設定
            val button_oneT: Button = findViewById(R.id.oneT);  // 1
            buttonDisnabled_tabs(button_oneT)
        }

        twoT.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 2;

            //ボタン設定
            val button_twoT: Button = findViewById(R.id.twoT);  // 2
            buttonDisnabled_tabs(button_twoT)
        }

        threeT.setOnClickListener {
            medicineValueTime = medicineValueTime!! * 3;

            //ボタン設定
            val button_threeT: Button = findViewById(R.id.threeT);  // 3
            buttonDisnabled_tabs(button_threeT)
        }


        /*日数に関する設定*/
        decision.setOnClickListener {
            try {
                //先に使用日数が入力されたときの処理
                val editText: EditText = findViewById(R.id.daysValue);
                val daysStr: String = editText.text.toString();
                val daysInt: Int = Integer.parseInt(daysStr);

                val flagDecision_QID = if (daysInt % 2 == 0) {
                    0;
                } else {
                    1;
                }   //余りは切り上げ

                val flagDecision_OTD = when {
                    Integer.parseInt(daysStr) % 3 == 0 -> 2
                    Integer.parseInt(daysStr) % 3 == 1 -> 3
                    else -> 4
                }   //余りは切り上げ

                //ボタン設定
                buttonDisnabled_decision()

                //ボタンの使用可否で変数代入を判断
                medicineValueTime = medicineValueTime!! * alternative_decision(-1, daysInt);
                medicineValueTime = medicineValueTime!! * alternative_decision(flagDecision_QID, daysInt);
                medicineValueTime = medicineValueTime!! * alternative_decision(flagDecision_OTD, daysInt);
            } catch (e: Exception) {
                //日数が空の状態[決定]が押されたときの処理
                val builder: AlertDialog.Builder = AlertDialog.Builder(this);
                builder.setMessage("先に日数を入力してください")
                builder.setTitle("警告")
                builder.setPositiveButton("OK",null)
                builder.show()
            }
        }


        /*演算に関する設定*/
        delete.setOnClickListener {
            medicineValueTime = medicineValueData()[spinnerCount()].toDouble();   //薬の値段を初期値にリセットする

            //表示テキストをクリア
            val messageView: TextView = findViewById(R.id.unitvalue);
            messageView.text = "0";

            //ボタン設定
            buttonEnabled_operator()
            clearCheck_mdicineTimes()
            clear_daysValue()
        }

        allClear.setOnClickListener {
            medicineValues.clear()  //List内の要素を全削除

            //表示テキストをクリア
            val messageView: TextView = findViewById(R.id.unitvalue);
            val messageView2: TextView = findViewById(R.id.addvalue);
            messageView.text = "0";
            messageView2.text = "0";

            //ボタン設定
            buttonEnabled_operator()
            clearCheck_mdicineTimes()
            clear_daysValue()
        }

        add.setOnClickListener {
            if (!flag_tabs() && !flag_medcineTimes() && !flag_decision()) {
                //使用日数、錠剤数、日数がすべて入力されているときの処理
                medicineValues.add(medicineValueTime);    //計算後の値を配列に格納
                val messageView: TextView = findViewById(R.id.unitvalue)
                messageView.text = medicineValueTime.toString()     //単価に値段を表示

                medicineValueTime = medicineValueData()[spinnerCount()].toDouble();   //薬の値段を初期値にリセットする

                //ボタン設定
                buttonEnabled_operator()
                clearCheck_mdicineTimes()
                clear_daysValue()
            } else {
                //使用日数、錠剤数、日数のいずれかに入力不備があったときの処理
                val builder: AlertDialog.Builder = AlertDialog.Builder(this);
                builder.setMessage("入力不備があります" + "\n" + "錠剤数、使用回数、日数を入力後に[+]を押してください")
                builder.setTitle("警告")
                builder.setPositiveButton("OK",null)
                builder.show()
            }
        }

        equal.setOnClickListener {
            lastValue = sum();
            val lastValue_ceil = kotlin.math.ceil(lastValue / 100) * 100;
            val messageView2: TextView = findViewById(R.id.addvalue)
            messageView2.text = "$lastValue → $lastValue_ceil";    //最終計算結果と十の位切り上げ結果を表示する

            //ボタン設定
            buttonEnabled_operator()
            clearCheck_mdicineTimes()
            clear_daysValue()
        }

    }

    /*
    以下その他関数
     */

    /* DBからデータをすべて取得し薬のデータを配列にして返す関数*/
    private fun medicineData(): List<MedicineAllData> {
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

        val medicineList = arrayListOf<MedicineAllData>();

        for (i in 1..cursor.count) {
            medicineList.add(MedicineAllData(cursor.getString(0), cursor.getInt(1), cursor.getString(2)))
            cursor.moveToNext()
        }

        cursor.close()

        return medicineList
    }


    fun medicineNameData() : List<String> {
        val medicineNameList = medicineData().map { it.medicineName }
        return medicineNameList
    }

    fun medicineValueData() : List<Int> {
        val medicineValueList = medicineData().map { it.medicineValue }
        return medicineValueList
    }


    /*spinnerにString型のArrayListをセットする関数*/
    private fun setAdapter(spinner: Spinner, list: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter;
    }

    /*錠剤数ボタンを押したときすべてを無効にし選択したボタンを着色する関数*/
    private fun buttonDisnabled_tabs(button: Button) {
        val button_oeT: Button = findViewById(R.id.oeT);    // 1/8
        val button_ofT: Button = findViewById(R.id.ofT);    // 1/4
        val button_otT: Button = findViewById(R.id.otT);    // 1/2
        val button_oneT: Button = findViewById(R.id.oneT);  // 1
        val button_twoT: Button = findViewById(R.id.twoT);  // 2
        val button_threeT: Button = findViewById(R.id.threeT);  // 3

        button_oeT.isEnabled = false;
        button_ofT.isEnabled = false;
        button_otT.isEnabled = false;
        button_oneT.isEnabled = false;
        button_twoT.isEnabled = false;
        button_threeT.isEnabled = false;

        //押されたボタンに色を付ける
        if (button == button_oeT) {
            button_oeT.setBackgroundColor(Color.argb(50,0, 100, 200))
        } else if (button == button_ofT) {
            button_ofT.setBackgroundColor(Color.argb(50,0, 100, 200))
        } else if (button == button_otT) {
            button_otT.setBackgroundColor(Color.argb(50,0, 100, 200))
        } else if (button == button_oneT) {
            button_oneT.setBackgroundColor(Color.argb(50,0, 100, 200))
        } else if (button == button_twoT) {
            button_twoT.setBackgroundColor(Color.argb(50,0, 100, 200))
        } else if (button == button_threeT) {
            button_threeT.setBackgroundColor(Color.argb(50,0, 100, 200))
        }
    }

    /*演算子ボタンを押したときに呼ばれる関数*/
    private fun buttonEnabled_operator() {
        val button_oeT: Button = findViewById(R.id.oeT);    // 1/8
        val button_ofT: Button = findViewById(R.id.ofT);    // 1/4
        val button_otT: Button = findViewById(R.id.otT);    // 1/2
        val button_oneT: Button = findViewById(R.id.oneT);  // 1
        val button_twoT: Button = findViewById(R.id.twoT);  // 2
        val button_threeT: Button = findViewById(R.id.threeT);  // 3

        val button_decision: Button = findViewById(R.id.decision);  //決定

        val radioButton_SID: RadioButton = findViewById(R.id.SID);  //SID
        val radioButton_BID: RadioButton = findViewById(R.id.BID);  //BID
        val radioButton_TID: RadioButton = findViewById(R.id.TID);  //TID
        val radioButton_QID: RadioButton = findViewById(R.id.QID);  //QID
        val radioButton_OTD: RadioButton = findViewById(R.id.OTD);  //Once of Three Day

        //plus or equal or DELETE or ALL CLEARが押されたときすべてを使用可能にする
        button_oeT.setBackgroundResource(android.R.drawable.btn_default);
        button_ofT.setBackgroundResource(android.R.drawable.btn_default);
        button_otT.setBackgroundResource(android.R.drawable.btn_default);
        button_oneT.setBackgroundResource(android.R.drawable.btn_default);
        button_twoT.setBackgroundResource(android.R.drawable.btn_default);
        button_threeT.setBackgroundResource(android.R.drawable.btn_default);

        button_oeT.isEnabled = true;
        button_ofT.isEnabled = true;
        button_otT.isEnabled = true;
        button_oneT.isEnabled = true;
        button_twoT.isEnabled = true;
        button_threeT.isEnabled = true;

        button_decision.isEnabled = true;

        radioButton_SID.isEnabled = true;
        radioButton_BID.isEnabled = true;
        radioButton_TID.isEnabled = true;
        radioButton_QID.isEnabled = true;
        radioButton_OTD.isEnabled = true;
    }

    /*決定ボタンを押したときに使えなくする関数*/
    private fun buttonDisnabled_decision() {
        val button_decision: Button = findViewById(R.id.decision);
        button_decision.isEnabled = false;
    }

    /*ラジオボタンを1度押したときに使えなくする関数*/
    private fun radioButtonDisnabled_medcineTimes() {
        val radioButton_SID: RadioButton = findViewById(R.id.SID);
        val radioButton_BID: RadioButton = findViewById(R.id.BID);
        val radioButton_TID: RadioButton = findViewById(R.id.TID);
        val radioButton_QID: RadioButton = findViewById(R.id.QID);
        val radioButton_OTD: RadioButton = findViewById(R.id.OTD);

        radioButton_SID.isEnabled = false;
        radioButton_BID.isEnabled = false;
        radioButton_TID.isEnabled = false;
        radioButton_QID.isEnabled = false;
        radioButton_OTD.isEnabled = false;
    }

    /*使用回数ラジオボタンのチェックを外す関数*/
    private fun clearCheck_mdicineTimes () {
        val radioGroup: RadioGroup = findViewById(R.id.MedicineTimes);
        radioGroup.clearCheck()
    }

    /*入力された日数を空にする関数*/
    private fun clear_daysValue() {
        val edittext_daysValue: EditText = findViewById(R.id.daysValue);
        edittext_daysValue.editableText.clear()
    }


    /*錠剤数ボタンの選択の有無を返す関数*/
    private fun flag_tabs(): Boolean {
        val button_oeT: Button = findViewById(R.id.oeT);    // 1/8
        val button_ofT: Button = findViewById(R.id.ofT);    // 1/4
        val button_otT: Button = findViewById(R.id.otT);    // 1/2
        val button_oneT: Button = findViewById(R.id.oneT);  // 1
        val button_twoT: Button = findViewById(R.id.twoT);  // 2
        val button_threeT: Button = findViewById(R.id.threeT);  // 3

        return button_oeT.isEnabled && button_ofT.isEnabled && button_otT.isEnabled && button_oneT.isEnabled && button_twoT.isEnabled && button_threeT.isEnabled
    }

    /*ラジオボタンの選択の有無を返す関数*/
    private fun flag_medcineTimes(): Boolean {
        // ラジオグループのオブジェクトを取得
        val rg = findViewById(R.id.MedicineTimes) as RadioGroup
        // チェックされているラジオボタンの ID を取得
        val id = rg.checkedRadioButtonId

        return id == -1
    }

    /*決定ボタンの選択の有無を返す関数*/
    private fun flag_decision(): Boolean {
        val button_decision: Button = findViewById(R.id.decision)

        return button_decision.isEnabled
    }

    /*決定と使用日数の干渉を避けるために呼び出す関数*/
    private fun alternative_decision(flag: Int, d: Int): Double {
        val radioGroup: RadioGroup = findViewById(R.id.MedicineTimes)
        val id = radioGroup.checkedRadioButtonId
        val radioButton = radioGroup.findViewById<RadioButton>(id)
        val radioButton_SID: RadioButton = findViewById(R.id.SID);
        val radioButton_BID: RadioButton = findViewById(R.id.BID);
        val radioButton_TID: RadioButton = findViewById(R.id.TID);
        val radioButton_QID: RadioButton = findViewById(R.id.QID);
        val radioButton_OTD: RadioButton = findViewById(R.id.OTD);

        var day: Int = 0;   //日数

        if (!flag_decision() && (radioButton == radioButton_SID || radioButton == radioButton_BID || radioButton == radioButton_TID)) {
            if (flag == -1) {
                day = d;

                return day.toDouble()
            }
        }

        if (!flag_decision() && radioButton == radioButton_QID) {
            if (flag == 0) {
                day = d;
                return (day/2).toDouble()
            } else if (flag == 1) {
                day = d + 1;
                return (day/2).toDouble()
            }


        }

        if (!flag_decision() && radioButton == radioButton_OTD) {
            if (flag == 2) {
                day = d;
                return (day/3).toDouble()
            } else if (flag == 3) {
                day = d + 2;
                return (day/3).toDouble()
            } else if (flag == 4) {
                day = d + 1;
                return (day/3).toDouble()
            }


        }

        return 1.0
    }

    /*スピナに選択されている項目のIDを返す関数*/
    private fun spinnerCount(): Int {
        val spinner: Spinner = findViewById(R.id.spinner);
        val medicineNameItem: String = spinner.selectedItem as String;
        var i = 0;
        while (medicineNameData()[i] != medicineNameItem) {
            i++
        }

        return i
    }

    private fun sum() : Double{
        var result  = 0.0;
        var i = 0;
        while (i < medicineValues.size) {
            result += medicineValues[i];
            i++
        }

        return result
    }

}


class MedicineAllData(val medicineName: String, val medicineValue: Int, val medicineKind: String)





