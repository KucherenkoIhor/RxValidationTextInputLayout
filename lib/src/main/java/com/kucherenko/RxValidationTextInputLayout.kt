package com.kucherenko

import android.content.Context
import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
import android.util.AttributeSet
import android.widget.EditText
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Created by ihor_kucherenko on 11/22/17.
 * https://github.com/KucherenkoIhor
 */
class RxValidationTextInputLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    var helperResId: Int = 0

    var errorResId: Int = 0

    var equalEditTextId: Int = 0

    var errorText: String? = null

    var helperText: String? = null

    var regex: String? = null

    var allowEmpty: Boolean = false

    var acceptableText: String? = null

    var showErrorIfEmpty: Boolean = false

    private var focusDisposable: Disposable? = null

    var onReady: ((Observable<Boolean>) -> Unit)? = null

    var observable: Observable<Boolean>? = null
        private set(value) {
            field = value
        }

    var isReady: Boolean? = null
        private set(value) {
            field = value
        }

    companion object {

        fun combineOnReady(vararg layouts: RxValidationTextInputLayout, onReady: (Observable<Boolean>) -> Unit) {
            val observables = mutableListOf<Observable<Boolean>>()
            layouts.forEach {
                it.onReady = { observable ->
                    observables.add(observable)
                    if (observables.size == layouts.size) {
                        Observable.combineLatest(observables, { it.any { it == false }.not() }).let {
                            onReady.invoke(it)
                        }
                    }
                }
            }
        }

    }

    init {

        attrs?.let {

            val customTypedArray = context.obtainStyledAttributes(it, R.styleable.RxValidationTextInputLayout, 0, 0)

            helperResId = customTypedArray.getResourceId(R.styleable.RxValidationTextInputLayout_helperTextAppearance, 0)
            errorResId = customTypedArray.getResourceId(R.styleable.RxValidationTextInputLayout_errorTextAppearance, 0)

            errorText = customTypedArray.getString(R.styleable.RxValidationTextInputLayout_error)

            helperText = customTypedArray.getString(R.styleable.RxValidationTextInputLayout_help)

            equalEditTextId = customTypedArray.getResourceId(R.styleable.RxValidationTextInputLayout_equalEditText, 0)

            regex = customTypedArray.getString(R.styleable.RxValidationTextInputLayout_regex)
            acceptableText = customTypedArray.getString(R.styleable.RxValidationTextInputLayout_acceptableText)
            showErrorIfEmpty = customTypedArray.getBoolean(R.styleable.RxValidationTextInputLayout_showErrorIfEmpty, false)
            allowEmpty = customTypedArray.getBoolean(R.styleable.RxValidationTextInputLayout_allowEmpty, false)

            customTypedArray.recycle()
        }
    }

    /**
     * @see triggerError
     *
     * @param errorText - id of string resources that will be shown as error text
     */
    fun triggerError(@StringRes errorText: Int) {
        triggerError(resources.getString(errorText))
    }

    /**
     * This method shows error text immediately
     * @param text - string that will be shown as error text
     *
     * It is useful for case when some out logic can decide validation rules for inputted text
     */
    fun triggerError(text: String) {
        editText?.isFocusableInTouchMode = true
        editText?.requestFocus()
        isErrorEnabled = true
        error = text
        this.setErrorTextAppearance(errorResId)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isReady = null
        subscribeValidation()
    }

    private fun subscribeValidation() {

        val equalEditText: EditText? = rootView.findViewById(equalEditTextId)

        editText?.let { et ->

            observable = RxTextView.afterTextChangeEvents(et)
                    .skip(if (allowEmpty) 0 else 1)
                    .map {
                        (acceptableText?.let {
                            it == et.text.toString()
                        } ?: it.editable().isNullOrBlank() && showErrorIfEmpty.not())
                                ||
                                equalEditText?.let {
                                    et.text.toString() == it.text.toString()
                                } ?: et.text.matches(Regex(regex ?: return@map true))
                    }
                    .doOnNext {
                        this.error = when {
                            it -> ""
                            hasFocus() && helperText.isNullOrBlank().not() -> {
                                this.setErrorTextAppearance(helperResId)
                                helperText
                            }
                            hasFocus() && helperText.isNullOrBlank()-> {
                                this.setErrorTextAppearance(errorResId)
                                errorText
                            }
                            else -> {
                                this.setErrorTextAppearance(errorResId)
                                errorText
                            }
                        }
                    }
                    .map {
                        if (allowEmpty && et.text.isNullOrBlank()) {
                            true
                        } else {
                            (acceptableText?.let {
                                et.text.toString() != it
                            } ?: et.text.isNullOrBlank().not()) && it
                        }
                    }
                    .doOnSubscribe {
                        focusDisposable = RxView.focusChanges(et)
                                .skipInitialValue()
                                .subscribe { isFocused ->
                                    if (isFocused) {
                                        this.setErrorTextAppearance(helperResId)
                                        if (this.error.isNullOrBlank().not()
                                                && this.error == errorText) {
                                            this.error = helperText
                                        }
                                    } else {
                                        this.setErrorTextAppearance(errorResId)
                                        if (this.error.isNullOrBlank().not()
                                                && this.error == helperText?.let { it } ?: errorText) {
                                            this.error = errorText
                                        }
                                    }
                                }
                    }
                    .doOnDispose {
                        focusDisposable?.let {
                            if (it.isDisposed) {
                                it.dispose()
                            }
                        }
                    }

            observable?.let { onReady?.invoke(it) }
        }
    }

}