package com.qgstudio.qgglass.ui

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.mobile.utils.*
import com.qgstudio.qgglass.App
import com.qgstudio.qgglass.R
import com.qgstudio.qgglass.SoftKeyBoardListener
import com.qgstudio.qgglass.data.ResultV2
import com.qgstudio.qgglass.data.StateCode
import com.qgstudio.qgglass.data.User
import com.qgstudio.qgglass.fastObserve
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.experimental.delay
import org.jetbrains.anko.contentView

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = resources.getColor(R.color.colorStatusGreen)
        }
        SoftKeyBoardListener.setListener(this, object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
            override fun keyBoardShow(height: Int) {
                println(height)
                with(contentView!!) {
                    layout(left, top - height, right, bottom - height)
                }
            }

            override fun keyBoardHide(height: Int) {
                println(height)
                with(contentView!!) {
                    //layout(left,top+height,right,bottom+height)
                }
            }

        })
        buttonCode.setOnClickListener{
            if (buttonCode.text=="获取验证码") {
                buttonCode.text = "60秒后重试"
                var count = 60
                doAfter(60000){
                    buttonCode.text="获取验证码"
                }
                doAfter(1000,60){
                    buttonCode.text = "${--count}秒后重试"
                }
                App.usrApi.getCode(User(account = "",password = "",phone = editTextAccount.value,name = ""))
                        .fastObserve("getCode"){}
            }

        }
        buttonLogin.setOnClickListener {
            val user = User(phone = editTextAccount.value,authCode = editTextPassword.value)
            App.usrApi.login(user)
                    .fastObserve("login") {
                        println(it.state)
                        if (it.state == StateCode.OK.value) {
                            //登陆
                            startActivity(Intent(this@LoginActivity, ContactActivity::class.java))
                            finish()
                        }
                    }
        }
        buttonLogin.setOnLongClickListener {
            startActivity(Intent(this@LoginActivity, ContactActivity::class.java))
            finish()
            true
        }
        setDrawableBounds(editTextAccount)
        setDrawableBounds(editTextPassword)
    }

    private fun setDrawableBounds(edittext: EditText) {
        val leftDrawable = edittext.compoundDrawables[0]
        if (leftDrawable != null) {
            leftDrawable.setBounds(0, 0, 60, 60)
            edittext.setCompoundDrawables(leftDrawable, edittext.compoundDrawables[1], edittext.compoundDrawables[2], edittext.compoundDrawables[3])
        }
    }
}
