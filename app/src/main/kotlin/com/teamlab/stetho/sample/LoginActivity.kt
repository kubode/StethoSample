package com.teamlab.stetho.sample

import android.app.ProgressDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.rxbinding.view.clicks
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import rx.subscriptions.Subscriptions

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up service.
        val client = OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()
        val service = Retrofit.Builder()
                .baseUrl("https://google.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(MyService::class.java)

        val pref = getSharedPreferences("LoginActivity", MODE_PRIVATE)!!

        // Find views.
        val email = findViewById(R.id.email) as TextView
        val password = findViewById(R.id.password) as EditText
        val signIn = findViewById(R.id.email_sign_in_button) as Button
        val progress = ProgressDialog(this)
                .apply { setCancelable(false) }

        // Initialize views.
        email.text = pref.getString("email", "")
        password.setText(pref.getString("password", ""))

        // Subscribe events.
        subscription = CompositeSubscription(
                signIn.clicks().subscribe {
                    service.login(email.text.toString(), password.text.toString())
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe { progress.show() }
                            .doAfterTerminate { progress.dismiss() }
                            .subscribe(
                                    { Snackbar.make(window.decorView, it, Snackbar.LENGTH_LONG).show() },
                                    { Snackbar.make(window.decorView, it.toString(), Snackbar.LENGTH_LONG).show() })
                },
                Subscriptions.create {
                    progress.dismiss()
                },
                // Save texts.
                Subscriptions.create {
                    pref.edit()
                            .putString("email", email.text.toString())
                            .putString("password", password.text.toString())
                            .apply()
                })
    }

    override fun onDestroy() {
        subscription.unsubscribe()
        super.onDestroy()
    }
}
