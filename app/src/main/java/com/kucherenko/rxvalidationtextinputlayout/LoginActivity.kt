package com.kucherenko.rxvalidationtextinputlayout

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.kucherenko.RxValidationTextInputLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit

/**
 * Created by ihor_kucherenko on 11/22/17.
 * https://github.com/KucherenkoIhor
 */
class LoginActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textInputLayouts = arrayOf(
                tilFirstName,
                tilLastName,
                tilEmail,
                tilPhone,
                tilYear,
                tilDob,
                tilPassword,
                tilConfirmPassword)

        RxValidationTextInputLayout.combineOnReady(*textInputLayouts) { observable ->
            disposable = observable
                    .debounce { Observable.timer(if (it) 500L else 0L, TimeUnit.MILLISECONDS) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { btnSignIn.isEnabled = it }
        }

        btnTriggerError.setOnClickListener {
            tilFirstName.triggerError("Error")
        }

    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
