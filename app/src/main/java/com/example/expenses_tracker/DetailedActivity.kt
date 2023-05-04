package com.example.expenses_tracker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailedActivity : AppCompatActivity() {
    private lateinit var transaction: FoodTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)

        val updateBtn : Button =  findViewById(R.id.updateBtn)
        val labelInput: TextInputEditText = findViewById(R.id.labelInput)
        val amountInput : TextInputEditText = findViewById(R.id.amountInput)
        val labelLayout : TextInputLayout =  findViewById(R.id.labelLayout)
        val amountLayout : TextInputLayout =  findViewById(R.id.amountLayout)
        val closeBtn : ImageButton =  findViewById(R.id.closeBtn)
        val descriptionInput : TextInputEditText = findViewById(R.id.descriptionInput)
        val rootView : ConstraintLayout = findViewById(R.id.rootView)

        transaction = intent.getSerializableExtra("transaction") as FoodTransaction

        labelInput.setText(transaction.label)
        amountInput.setText(transaction.amount.toString())
        descriptionInput.setText(transaction.description)

        rootView.setOnClickListener {
            this.window.decorView.clearFocus()

            val a = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            a.hideSoftInputFromWindow(it.windowToken, 0)
        }


        labelInput.addTextChangedListener {
            updateBtn.visibility =  View.VISIBLE
            if (it!!.count()>0){
                labelLayout.error = null
            }
        }

        amountInput.addTextChangedListener {
            updateBtn.visibility =  View.VISIBLE
            if (it!!.count()>0){
                amountLayout.error = null
            }
        }

        descriptionInput.addTextChangedListener {
            updateBtn.visibility =  View.VISIBLE
        }

        updateBtn.setOnClickListener{
            val label : String = labelInput.text.toString()
            val description : String = descriptionInput.text.toString()
            val amount : Double? = amountInput.text.toString().toDoubleOrNull()

            if(label.isEmpty())
                labelLayout.error = "Please Enter a Valid Label"

            else if(amount==null)
                amountLayout.error = "Please Enter a Valid Amount"

            else{

                val transaction = FoodTransaction(transaction.id,label,amount, description)
                update(transaction)
            }
        }
        closeBtn.setOnClickListener {
            finish()
        }
    }

    private fun update(transaction: FoodTransaction){
        val db = Room.databaseBuilder(this,
            AppDatabase::class.java,
            "foodtransactions").build()

        GlobalScope.launch {
            db.foodTransactionDao().update(transaction)
            finish()
        }

    }
}