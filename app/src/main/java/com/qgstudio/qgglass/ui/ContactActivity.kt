package com.qgstudio.qgglass.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SimpleAdapter
import com.amap.api.maps.CoordinateConverter
import com.amap.api.maps.model.LatLng
import com.mobile.utils.JsonMaker
import com.mobile.utils.showToast
import com.mobile.utils.value
import com.qgstudio.qgglass.*
import com.qgstudio.qgglass.data.ResultV2
import com.qgstudio.qgglass.data.ServerInfo
import com.qgstudio.qgglass.data.StateCode
import com.qgstudio.qgglass.data.User
import com.qgstudio.qgglass.net.WebSocketManager
import com.qgstudio.qgglass.ui.MainActivity.Companion.lList
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.dialog_delete.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.*
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates

class ContactActivity : AppCompatActivity() {
    private val log = AnkoLogger<ContactActivity>()
    private lateinit var adapter: SimpleAdapter
    private val mList = mutableListOf<Map<String, String>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.colorStatusGreen)
        }
        //设置适配器
        adapter = SimpleAdapter(this, mList, R.layout.tiem_contact, arrayOf("Name"), intArrayOf(R.id.textViewName))
        listViewContact.adapter = adapter

        //第一次获取
        getContactList()
        buttonToMap.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        setupAddEditDialog()
        setupDeleteDialog()
        setupUpdateEditDialog()
        EventBus.getDefault().register(this)
        WebSocketManager.connect("ws://39.108.110.121:8888/ws?gid=${UUID.randomUUID()}")
        checkIfNeedWarning()
    }

    private fun getContactList() {
        App.contactApi.getAllContact()
                .fastObserve("getAllContact") {
                    log.error { "onNext->" + it.state }
                    onContactGet(it.data)
                }
    }

    private fun onContactGet(list: List<User>) {
        mList.clear()
        mList.addAll(userToMapList(list))//重装
        adapter.notifyDataSetChanged()
    }

    private fun userToMapList(list: List<User>): List<Map<String, String>> {
        return list.map {
            mutableMapOf<String, String>().apply {
                put("Name", it.name)
                put("Phone", it.phone)
                put("Id", it.id.toString())
            }
        }
    }

    private fun setupDeleteDialog() {
        listViewContact.setOnItemLongClickListener { _, _, position, _ ->
            //            alert("确认删除联系人吗？", "信息") {
//                yesButton {
//                    App.contactApi.deleteContact(User("", "", "", "", mList[position]["Id"]!!.toInt()))
//                            .fastObserve("deleteContact") {
//                                log.error { "onNext->" + it.state }
//                                getContactList()//刷新
//                            }
//                }
//                noButton { }
//            }.show()
            val view = layoutInflater.inflate(R.layout.dialog_delete,null,false)
            val dialog = AlertDialog.Builder(this)
                    .setView(view)
                    .show()
            view.find<Button>(R.id.buttonSure).setOnClickListener {
                App.contactApi.deleteContact(User("", "", "", "", "",mList[position]["Id"]!!.toInt()))
                        .fastObserve("deleteContact") {
                            log.error { "onNext->" + it.state }
                            getContactList()//刷新
                            dialog.dismiss()
                        }
            }
            view.find<Button>(R.id.buttonCancel).setOnClickListener { dialog.dismiss() }
            true
        }
    }

    private fun setupAddEditDialog() {
        buttonAddContact.setOnClickListener {
//            val dialog = alert {
//                var nameEditText: EditText = EditText(this@ContactActivity)
//                var phoneEditText: EditText = EditText(this@ContactActivity)
//                customView {
//                    verticalLayout {
//                        nameEditText = editText {
//                            hint = "name"
//                        }
//                        phoneEditText = editText {
//                            hint = "phone"
//                        }
//                    }
//
//                }
//                yesButton {
//                    App.contactApi.addContact(User("", "", phoneEditText.value, nameEditText.value))
//                            .fastObserve("addContact") {
//                                log.error { "onNext->" + it.state }
//                                getContactList()//刷新
//                            }
//                }
//                noButton { }
//            }.show()
            val view = layoutInflater.inflate(R.layout.dialog_edit,null,false)
            val dialog = AlertDialog.Builder(this)
                    .setView(view)
                    .show()
            view.find<Button>(R.id.buttonSure).setOnClickListener {
                                    App.contactApi.addContact(User("", "", view.find<EditText>(R.id.editTextPhone).value, view.find<EditText>(R.id.editTextName).value))
                            .fastObserve("addContact") {
                                log.error { "onNext->" + it.state }
                                getContactList()//刷新
                                dialog.dismiss()
                            }
            }
            view.find<Button>(R.id.buttonCancel).setOnClickListener { dialog.dismiss() }
        }
    }

    private fun setupUpdateEditDialog() {
        listViewContact.setOnItemClickListener { _, _, position, _ ->
//            alert {
//                var nameEditText: EditText = EditText(this@ContactActivity)
//                var phoneEditText: EditText = EditText(this@ContactActivity)
//                customView {
//                    verticalLayout {
//                        nameEditText = editText {
//                            setText(mList[position]["Name"])
//                        }
//                        phoneEditText = editText {
//                            setText(mList[position]["Phone"])
//                        }
//                    }
//
//                }
//                yesButton {
//                    App.contactApi.updateContact(User("", "", phoneEditText.value, nameEditText.value, mList[position]["Id"]!!.toInt()))
//                            .fastObserve("updateContact") {
//                                log.error { "onNext->" + it.state }
//                                getContactList()
//                            }
//                }
//                noButton { }
//            }.show()
            val view = layoutInflater.inflate(R.layout.dialog_edit,null,false)
            view.find<EditText>(R.id.editTextPhone).setText(mList[position]["Phone"])
            view.find<EditText>(R.id.editTextName).setText(mList[position]["Name"])

            val dialog = AlertDialog.Builder(this)
                    .setView(view)
                    .show()
            view.find<Button>(R.id.buttonSure).setOnClickListener {
                App.contactApi.updateContact(User("", "", view.find<EditText>(R.id.editTextPhone).value, view.find<EditText>(R.id.editTextName).value, "",mList[position]["Id"]!!.toInt()))
                            .fastObserve("updateContact") {
                                log.error { "onNext->" + it.state }
                                getContactList()
                                dialog.dismiss()
                            }
            }
            view.find<Button>(R.id.buttonCancel).setOnClickListener { dialog.dismiss() }
        }
    }
    @Subscribe
    fun onServerInfoGet(info: ServerInfo) {

        if (info.info.isEmpty()) {
            val converter = CoordinateConverter(this).from(CoordinateConverter.CoordType.GPS)
            lList.add(converter.coord(LatLng(info.latitude,info.longitude)).convert())
            saveLastLatlng(lList[lList.lastIndex])
            Log.e("============","==================================")
        } else {
            //help指令时可能没有附带坐标信息，所以取原先列表中的最后一个点
            if (lList.isEmpty()) {
                toHelpDialog(getLastLatLng())
            } else {
                toHelpDialog(lList[lList.lastIndex])
            }
        }
    }

    fun toHelpDialog(latLng: LatLng?) {
        val dialog = HelpDialog.withLatlng(latLng)
        dialog.show(fragmentManager, "")
        Bee.bee(this)//报警
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    fun checkIfNeedWarning(){
        App.usrApi.getWarning().fastObserve("getWarning"){
            if (it.state == StateCode.NEED_WARNING.value) {
                toHelpDialog(null)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Bee.stopBee()
    }
}
