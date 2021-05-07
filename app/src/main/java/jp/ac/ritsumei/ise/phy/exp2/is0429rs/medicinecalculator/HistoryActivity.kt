package jp.ac.ritsumei.ise.phy.exp2.is0429rs.medicinecalculator

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        /*上段モード切り替え*/
        val radioGroup_modeChange : RadioGroup = findViewById(R.id.ModeChange)
        radioGroup_modeChange.setOnCheckedChangeListener  { _, checkedId : Int ->
            if (checkedId == R.id.medList) {
                //薬登録画面への遷移
                val intent_medReg : Intent = Intent(this, MedicineRegistActivity::class.java)
                startActivity(intent_medReg)

            } else if (checkedId == R.id.accounting) {
                //会計画面への遷移
                val intent_accounting : Intent = Intent(this, AccountingActivity::class.java)
                startActivity(intent_accounting)

            } else if (checkedId == R.id.medCul) {
                //薬計算画面への遷移
                val intent_medCul: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent_medCul)
            }
        }
    }
}
