package com.koukemo

import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        /*上段モード切り替え*/
        val radioGroup_modeChange: RadioGroup = findViewById(R.id.ModeChange)
        radioGroup_modeChange.setOnCheckedChangeListener { _, checkedId: Int ->
            if (checkedId == R.id.medList) {
                //薬登録画面への遷移
                val intent_medReg: Intent = Intent(this, MedicineRegisterActivity::class.java)
                startActivity(intent_medReg)

            } else if (checkedId == R.id.medCul) {
                //薬計算への遷移
                val intent_medCul: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent_medCul)

            } else if (checkedId == R.id.accounting) {
                //履歴画面への遷移
                val intent_history: Intent = Intent(this, AccountingActivity::class.java)
                startActivity(intent_history)
            }
        }
    }
}