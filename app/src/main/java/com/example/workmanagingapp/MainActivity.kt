package com.example.workmanagingapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workmanagingapp.model.Constants
import com.example.workmanagingapp.model.Constants.Companion.CHANNEL_ID
import com.example.workmanagingapp.model.Day
import com.example.workmanagingapp.model.Work
import com.example.workmanagingapp.view.addscreen.AddScreen
import com.example.workmanagingapp.view.drawerscreens.DialogViewAllUnfinished
import com.example.workmanagingapp.view.mainscreen.works.DialogDelete
import com.example.workmanagingapp.view.mainscreen.works.DialogViewDetail
import com.example.workmanagingapp.view.mainscreen.days.MyDayAdapter
import com.example.workmanagingapp.viewmodel.OnItemClickListener
import com.example.workmanagingapp.view.mainscreen.works.MyWorkListAdapter
import com.example.workmanagingapp.viewmodel.MyViewModel
import com.example.workmanagingapp.viewmodel.MyViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var btnToggleMenu: FloatingActionButton

    private lateinit var tvCurrentWork: TextView

    private lateinit var btnAdd: FloatingActionButton
    private lateinit var dropdownToday: TextView
    private lateinit var dropdownUpcoming: TextView

    private lateinit var recyclerViewDays: RecyclerView
    private lateinit var recyclerViewCurrent: RecyclerView
    private lateinit var recyclerViewUpcoming: RecyclerView

    private val linearLayoutManager =
        LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    private val viewModel: MyViewModel by viewModels {
        MyViewModelFactory(this)
    }

    override fun onResume() {
        //update the list when back from 2nd screen
        super.onResume()
        viewModel.loadWorkList()
        viewModel.setWorkManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        //drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        btnToggleMenu = findViewById(R.id.btnDrawer)
        btnToggleMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        listenToDrawerItems()

        //Load data from SQLite using content provider
        viewModel.loadWorkList()

        //add new work button
        btnAdd = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddScreen::class.java)
            startActivity(intent)
        }

        //today's work with current date
        tvCurrentWork = findViewById(R.id.tvCurrent)
        tvCurrentWork.text =
            "TODAY - ${LocalDate.now().dayOfMonth}/${LocalDate.now().month.value}"

        //set recyclerViews
        setRecyclerViews()

        //dropdown buttons
        setDropDownButtons()

        observeDayList()
        observeCurrentTitle()
        observeWorkList()

        //set workManager
        viewModel.setWorkManager()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setSound(null, null)
            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun listenToDrawerItems() {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.itemAllWork -> {
                    val dialogViewAll = DialogViewAllUnfinished(
                        this, this, viewModel,
                        Constants.Companion.ViewDetailType.ALL
                    )
                    dialogViewAll.show(supportFragmentManager, "dialog_view_all")
                }

                R.id.itemUnfinished -> {
                    val dialogViewUnfinished = DialogViewAllUnfinished(
                        this, this, viewModel,
                        Constants.Companion.ViewDetailType.UNFINISHED
                    )
                    dialogViewUnfinished.show(supportFragmentManager, "dialog_view_unfinished")
                }
            }
            true
        }
    }

    private fun setRecyclerViews() {
        //days tab
        recyclerViewDays = findViewById(R.id.recViewDays)
        recyclerViewDays.layoutManager = linearLayoutManager
        recyclerViewDays.adapter = MyDayAdapter(this, this, viewModel)


        //current view
        recyclerViewCurrent = findViewById(R.id.recViewCurrent)
        recyclerViewCurrent.layoutManager = LinearLayoutManager(this)
        recyclerViewCurrent.adapter = MyWorkListAdapter(
            this,
            this,
            viewModel,
            Constants.Companion.ViewDetailType.CURRENT
        )

        //upcoming view
        recyclerViewUpcoming = findViewById(R.id.recViewUpcoming)
        recyclerViewUpcoming.layoutManager = LinearLayoutManager(this)
        recyclerViewUpcoming.adapter = MyWorkListAdapter(
            this,
            this,
            viewModel,
            Constants.Companion.ViewDetailType.UPCOMING
        )
    }

    private fun setDropDownButtons() {
        dropdownToday = findViewById(R.id.dropDownToday)
        dropdownToday.setOnClickListener {
            if (recyclerViewCurrent.visibility == View.VISIBLE) {
                recyclerViewCurrent.visibility = View.GONE
                dropdownToday.text = "\u25BC"
            } else {
                recyclerViewCurrent.visibility = View.VISIBLE
                dropdownToday.text = "\u25B2"
            }
        }
        dropdownUpcoming = findViewById(R.id.dropDownUpcoming)
        dropdownUpcoming.setOnClickListener {
            if (recyclerViewUpcoming.visibility == View.VISIBLE) {
                recyclerViewUpcoming.visibility = View.GONE
                dropdownUpcoming.text = "\u25BC"
            } else {
                recyclerViewUpcoming.visibility = View.VISIBLE
                dropdownUpcoming.text = "\u25B2"
            }
        }
    }

    private fun observeDayList() {
        val dayListLiveData = viewModel.getDayListLiveData()

        val observer = Observer<MutableList<Day>> { newList ->
            viewModel.setDayList(newList)

            recyclerViewDays.adapter = MyDayAdapter(this, this, viewModel)
        }

        //observe changes
        dayListLiveData.observe(this, observer)
    }

    private fun observeCurrentTitle() {
        val currentTitleLiveData = viewModel.getCurrentTitleLiveData()

        val observer = Observer<String> { newTitle ->
            tvCurrentWork.text = newTitle
        }

        currentTitleLiveData.observe(this, observer)
    }

    private fun observeWorkList() {
        val currentWorkListLiveData = viewModel.getCurrentWorkListLiveData()
        val upcomingWorkListLiveData = viewModel.getUpcomingWorkListLiveData()

        val observerCurrent = Observer<MutableList<Work>> { newList ->
            viewModel.setCurrentWorkList(newList)
            recyclerViewCurrent.adapter = MyWorkListAdapter(
                this,
                this,
                viewModel,
                Constants.Companion.ViewDetailType.CURRENT
            )
        }
        val observerUpcoming = Observer<MutableList<Work>> { newList ->
            viewModel.setUpcomingWorkList(newList)
            recyclerViewUpcoming.adapter = MyWorkListAdapter(
                this,
                this,
                viewModel,
                Constants.Companion.ViewDetailType.UPCOMING
            )
        }

        //observer will observe the list that is inside the value of its Livedata
        currentWorkListLiveData.observe(this, observerCurrent)
        upcomingWorkListLiveData.observe(this, observerUpcoming)
    }

    override fun onItemDayClick(position: Int) {
        //set the selected day to change the background only
        viewModel.selectDayAndDisplayWork(position)
        linearLayoutManager.smoothScrollToPosition(recyclerViewDays, null, position)
    }

    override fun onItemWorkClick(position: Int, type: Constants.Companion.ViewDetailType) {
        //use type to know which list to pass in
        val dialog =
            if (type == Constants.Companion.ViewDetailType.CURRENT) DialogViewDetail(
                viewModel.getCurrentWorkList()[position],
                viewModel
            )
            else DialogViewDetail(viewModel.getUpcomingWorkList()[position], viewModel)
        dialog.show(supportFragmentManager, "detailToday")
    }

    override fun onItemWorkLongClick(position: Int, type: Constants.Companion.ViewDetailType) {
        val dialog =
            if (type == Constants.Companion.ViewDetailType.CURRENT) DialogDelete(
                viewModel.getCurrentWorkList()[position],
                viewModel
            )
            else DialogDelete(viewModel.getUpcomingWorkList()[position], viewModel)
        dialog.show(supportFragmentManager, "dialog_delete")
    }
}