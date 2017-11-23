# RxValidationTextInputLayout

  <p align="center">
        <img src="https://preview.ibb.co/emzma6/logo.png"/>
      </p>

The easiest way to bring validation to EditText with TextInputLayout.

## Getting Started

Add dependency to your project using [JitPack](https://jitpack.io)

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

```
dependencies {
		implementation 'com.github.KucherenkoIhor:RxValidationTextInputLayout:0.1'
}
```

Using in XML:

```
     <com.kucherenko.RxValidationTextInputLayout
                android:id="@+id/tilFirstName"
                style="@style/TextInputLayout.Counter"
                android:layout_marginTop="@dimen/margin_l"
                app:layout_constraintLeft_toLeftOf="parent"
                app:layout_constraintRight_toRightOf="parent"
                app:layout_constraintTop_toBottomOf="parent"
                app:help="at least 3 letters"
                app:error="@string/invalid_first_name"
                app:errorTextAppearance="@style/TextAppearance.AppCompat.Small.Error"
                app:helperTextAppearance="@style/TextAppearance.AppCompat.Small.Helper"
                app:regex="@string/regex_name">
    
                <android.support.design.widget.TextInputEditText
                    android:id="@+id/etFirstName"
                    style="@style/EditText.SingleLine"
                    android:hint="@string/hint_first_name"
                    android:imeOptions="actionNext"
                    android:inputType="textPersonName" />
    
            </com.kucherenko.RxValidationTextInputLayout>
```            
And get simple boolean result using RxJava2 in Java code:
```
 tilFirstName.onReady = { observable ->  
            disposable = observable.subscribe { isValid ->
                btnSignIn.isEnabled = isValid
            }
        }
```

## Features

 * Reactive approach using RxJava2:
 ```
 tilFirstName.onReady = { observable ->
             disposable = observable.subscribe { isValid ->
                 btnSignIn.isEnabled = isValid
             }
         }
  ```
 * Or if you want to observe several fields:        
 ```
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
             disposable = observable.subscribe { btnSignIn.isEnabled = it }
         }

```
 * Simple regex support: ``app:regex="@string/regex_name"``
 * You can change configuration as you need: 
    * You can allow empty state of input field: ``  app:allowEmpty="true"``
    * Or disable error showing when filed is empty ``  app:showErrorIfEmpty="true"``
 * Compare input with another one `` app:equalEditText="@id/etPassword"``.
 It's useful for password and confirm password fields:
 
 <p align="center">
   <img src="https://image.ibb.co/dS6ra6/confirm.png" width="350"/>
 </p>
 
 * Helper text according to [Material Design Guidelines](https://material.io/guidelines/components/text-fields.html#text-fields-layout)
 
    ``app:help="Must be the same with password"``
    
    <p align="center">
       <img src="https://image.ibb.co/jL39TR/helper.png" width="350"/>
     </p>
     
 * Error text appears if input text lose focus: ``app:error="@string/invalid_password"``
 
  <p align="center">
        <img src="https://image.ibb.co/fkzSF6/error.png" width="350"/>
      </p>
      
 * Custom text styles for help and error states:
       ```
       app:errorTextAppearance="@style/TextAppearance.AppCompat.Small.Error"
       app:helperTextAppearance="@style/TextAppearance.AppCompat.Small.Helper"
       ```
       
<p align="center">
               <img src="https://preview.ibb.co/ftW4TR/Unknown.png"/>
             </p>
                    
## Licenses

[MIT](http://opensource.org/licenses/MIT)           
           
  