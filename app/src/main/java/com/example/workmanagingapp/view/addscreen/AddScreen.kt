package com.example.workmanagingapp.view.addscreen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.workmanagingapp.R
import com.example.workmanagingapp.model.Work
import com.example.workmanagingapp.viewmodel.MyViewModel
import com.example.workmanagingapp.viewmodel.MyViewModelFactory
import java.time.LocalDateTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class AddScreen : AppCompatActivity() {
    private lateinit var btnBack: Button
    private lateinit var btnAdd: Button

    private lateinit var etName: EditText
    private lateinit var etContent: EditText

    private lateinit var btnChooseDate: Button
    private lateinit var btnChooseTime: Button

    private lateinit var tvDate: TextView
    private lateinit var tvTime: TextView

    //variables to store selected data
    private var title = ""
    private var content = ""
    private var newTime = LocalDateTime.now()

    private val viewModel: MyViewModel by viewModels {
        MyViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_new_work_layout)

        tvDate = findViewById(R.id.tvAddDate)
        tvDate.text = "Date: " + MyViewModel.displayDate(LocalDateTime.now())
        tvTime = findViewById(R.id.tvAddTime)
        tvTime.text = "Time: " + MyViewModel.displayTime(LocalDateTime.now())

        btnBack = findViewById(R.id.btnBack)
        btnBack.setOnClickListener { onBackPressed() }

        etName = findViewById(R.id.etName)
        etName.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    title = s.toString()
                }
            }
        )

        etContent = findViewById(R.id.etContent)
        etContent.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    content = s.toString()
                }
            }
        )

        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        btnChooseDate = findViewById(R.id.btnChooseDate)
        btnChooseDate.setOnClickListener {
            val dateDialog =
                DatePickerDialog(this, { _, pickedYear, pickedMonth, pickedDay ->
                    //month is 0-based

                }, year, month, day)
            dateDialog.show()

            val datePickerDialog =
                DatePickerDialog(this, { _, pickedYear, pickedMonth, pickedDay ->
                    newTime = LocalDateTime.of(
                        pickedYear,
                        pickedMonth + 1,
                        pickedDay,
                        newTime.hour,
                        newTime.minute
                    )
                    tvDate.text = "Date: ${MyViewModel.displayDate(newTime)}"
                }, year, month, day)

            datePickerDialog.show()
        }

        btnChooseTime = findViewById(R.id.btnChooseTime)
        btnChooseTime.setOnClickListener {
            val timeDialog = TimePickerDialog(this, { _, pickedHour, pickedMinute ->
                newTime = LocalDateTime.of(
                    newTime.year,
                    newTime.month,
                    newTime.dayOfMonth,
                    pickedHour,
                    pickedMinute
                )
                tvTime.text = "Time: ${MyViewModel.displayTime(newTime)}"
            }, hour, minute, true)
            timeDialog.show()
        }

        observeChanges()

        btnAdd = findViewById(R.id.btnConfirmAdd)
        btnAdd.setOnClickListener {
            val newWork = Work(title, newTime, content)

            viewModel.addNewToList(newWork)
            finish()
        }
    }

    private fun observeChanges() {
        val tvDateLiveData = viewModel.getAddNewDateTVLiveData()
        val dateObserver = Observer<String> { newValue ->
            tvDate.text = newValue
        }
        tvDateLiveData.observe(this, dateObserver)

        val tvTimeLiveData = viewModel.getAddNewTimeTVLiveData()
        val timeObserver = Observer<String> { newValue ->
            tvTime.text = newValue
        }
        tvTimeLiveData.observe(this, timeObserver)
    }
}