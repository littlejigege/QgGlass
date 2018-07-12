package com.qgstudio.qgglass.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mobile.utils.value
import com.qgstudio.qgglass.App
import com.qgstudio.qgglass.R
import com.qgstudio.qgglass.data.ResultV2
import com.qgstudio.qgglass.data.StateCode
import com.qgstudio.qgglass.data.User
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        buttonLogin.setOnClickListener {
            val user = User(editTextAccount.value, editTextPassword.value,"","")
            App.usrApi.login(user)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResultV2<String>?> {
                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(t: ResultV2<String>) {
                            println(t.state)
                            if (t.state == StateCode.OK.value) {
                                //登陆
                                startActivity(Intent(this@LoginActivity, ContactActivity::class.java))
                                finish()
                            }
                        }

                        override fun onError(e: Throwable) {
                            println(e.message)
                        }
                    })
        }
    }
}
