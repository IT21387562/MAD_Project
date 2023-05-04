package com.example.expenses_tracker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddFoodTransactionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food_transaction)

        val addFoodTransactionBtn : Button =  findViewById(R.id.addFoodTransactionBtn)
        val labelInput: TextInputEditText = findViewById(R.id.labelInput)
        val amountInput : TextInputEditText = findViewById(R.id.amountInput)
        val labelLayout : TextInputLayout =  findViewById(R.id.labelLayout)
        val amountLayout : TextInputLayout =  findViewById(R.id.amountLayout)
        val closeBtn : ImageButton =  findViewById(R.id.closeBtn)
        val descriptionInput : TextInputEditText = findViewById(R.id.descriptionInput)

        labelInput.addTextChangedListener {
            if (it!!.count()>0){
                labelLayout.error = null
            }
        }

        amountInput.addTextChangedListener {
            if (it!!.count()>0){
                amountLayout.error = null
            }
        }

        addFoodTransactionBtn.setOnClickListener{
            val label : String = labelInput.text.toString()
            val description : String = descriptionInput.text.toString()
            val amount : Double? = amountInput.text.toString().toDoubleOrNull()

            if(label.isEmpty())
                labelLayout.error = "Please Enter a Valid Label"

            else if(amount==null)
                amountLayout.error = "Please Enter a Valid Amount"

            else{
                val transaction = FoodTransaction(0,label,amount, description)
                insert(transaction)
            }
        }
        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun insert(transaction: FoodTransaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "foodtransactions").build()

        GlobalScope.launch {
            db.foodTransactionDao().insetAll(transaction)
            finish()
        }
    }
}