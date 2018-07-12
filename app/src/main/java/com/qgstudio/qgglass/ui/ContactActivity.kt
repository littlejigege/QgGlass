package com.qgstudio.qgglass.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.SimpleAdapter
import com.mobile.utils.JsonMaker
import com.mobile.utils.value
import com.qgstudio.qgglass.App
import com.qgstudio.qgglass.R
import com.qgstudio.qgglass.data.ResultV2
import com.qgstudio.qgglass.data.User
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contact.*
import org.jetbrains.anko.*
import kotlin.properties.Delegates

class ContactActivity : AppCompatActivity() {
    private val log = AnkoLogger<ContactActivity>()
    private lateinit var adapter: SimpleAdapter
    private val mList = mutableListOf<Map<String, String>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        //设置适配器
        adapter = SimpleAdapter(this, mList, android.R.layout.simple_list_item_2, arrayOf("Name", "Phone"), intArrayOf(android.R.id.text1,
                android.R.id.text2))
        listViewContact.adapter = adapter
        listViewContact.setOnItemClickListener { _, _, position, _ ->
            alert {
                var nameEditText: EditText = EditText(this@ContactActivity)
                var phoneEditText: EditText = EditText(this@ContactActivity)
                customView {
                    verticalLayout {
                        nameEditText = editText {
                            setText(mList[position]["Name"])
                        }
                        phoneEditText = editText {
                            setText(mList[position]["Phone"])
                        }
                    }

                }
                yesButton {
                    App.contactApi.updateContact(User("", "", phoneEditText.value, nameEditText.value,mList[position]["Id"]!!.toInt()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<ResultV2<String>?> {
                                override fun onComplete() {

                                }

                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onNext(t: ResultV2<String>) {
                                    log.error { "onNext->" + t.state }
                                    getContactList()
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                }
                            })
                }
                noButton { }
            }.show()
        }
        listViewContact.setOnItemLongClickListener { _, _, position, _ ->
            alert("确认删除联系人吗？","信息") {
                yesButton {
                    App.contactApi.deleteContact(User("","","","",mList[position]["Id"]!!.toInt()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<ResultV2<String>?> {
                                override fun onComplete() {

                                }

                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onNext(t: ResultV2<String>) {
                                    log.error { "onNext->" + t.state }
                                    getContactList()//刷新
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                }
                            })
                }
                noButton { }
            }.show()
            true
        }
        //第一次获取
        getContactList()
        buttonToMap.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        buttonAddContact.setOnClickListener {
            alert {
                var nameEditText: EditText = EditText(this@ContactActivity)
                var phoneEditText: EditText = EditText(this@ContactActivity)
                customView {
                    verticalLayout {
                        nameEditText = editText {
                            hint = "name"
                        }
                        phoneEditText = editText {
                            hint = "phone"
                        }
                    }

                }
                yesButton {
                    App.contactApi.addContact(User("", "", phoneEditText.value,nameEditText.value))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : Observer<ResultV2<String>?> {
                                override fun onComplete() {

                                }

                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onNext(t: ResultV2<String>) {
                                    log.error { "onNext->" + t.state }
                                    getContactList()
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                }
                            })
                }
                noButton { }
            }.show()
        }
    }

    private fun getContactList() {
        App.contactApi.getAllContact()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ResultV2<List<User>>?> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: ResultV2<List<User>>) {
                        log.error { "onNext->" + t.state }
                        onContactGet(t.data)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
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
}
