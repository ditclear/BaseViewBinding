### 写在前面

在平时的开发中，处理表单也是需要注意的槛。
开发者一般都会有自己的一套处理表单的方法，以前都会自己手动添加textChangeListener,到后来会通过Rxjava亦或者更直接的Rxbinding来处理，可以参考链接[RxJava处理复杂表单验证问题](http://www.jianshu.com/p/282574438481)。

当然还有一些表单验证的库。

这些都是大家熟知的事情，不多谈，本文主要探讨另外一种处理表单的方法。

### 使用DataDinding来处理表单验证问题

关于DataBinding前几篇就有介绍，不了解的可以搜索一下，easy。

处理表单验证，关键是需要获取到填充的数据，然后验证格式是否正确，平常都是根据textChangeListener,而dataBinding可以使用双向绑定轻松拿到。

```xml
<AutoCompleteTextView
    android:id="@+id/username"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="@string/prompt_username"
    android:inputType="text"
    android:maxLines="1"
    android:text="@={vm.username}"/>
```

接下来是验证

```java

    public static final String PASSWORD_PATTERN = "^[a-zA-Z0-9_]{6,16}$";
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{6,16}$";
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    private String username = "";
    private String email = "";
    private String password = "";
    private Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);
    private Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
  
 	@Bindable({"username", "password", "email"})
    public boolean isBtnEnabled() {
        if (!usernamePattern.matcher(username).matches()|| 		 	  	!emailPattern.matcher(email).matches()
                || !passwordPattern.matcher(password).matches()) {
            return false;
        }
        return true;
    }
```

使用正则表达式来验证格式是否正确

**isBtnEnabled()**用来判断按钮是否可点击

**@Bindable({"username", "password", "email"})**是databinding的**Dependent Properties**，在"username", "password", "email"这几个值改变的时候通知isBtnEnabled()方法返回更新的值，具体可以参考前面的文章[【译】Android Data Binding: Dependent Properties](http://www.jianshu.com/p/bc55ca46dc1a)。

效果如下：

![SingleMode.gif](https://github.com/ditclear/BaseViewBinding/blob/master/validate.gif?raw=true)



### 错误处理

在点击Sign In 后 ，可能登录失败，提示用户名或者密码错误，需要通过setError()方法来提示用户。不过TextInputLayout不是那么智能，具体见[使用TextInputLayout创建一个登陆界面](http://www.jcodecraeer.com/a/basictutorial/2015/0821/3338.html) ，可能需要大量的无用的代码。

```java
public void onClick(View v) {
    hideKeyboard();
 
    String username = usernameWrapper.getEditText().getText().toString();
    String password = usernameWrapper.getEditText().getText().toString();
    if (!validateEmail(username)) {
        usernameWrapper.setError("Not a valid email address!");
    } else if (!validatePassword(password)) {
        passwordWrapper.setError("Not a valid password!");
    } else {
        usernameWrapper.setErrorEnabled(false);
        passwordWrapper.setErrorEnabled(false);
        doLogin();
    }
}
```

而使用Databinding我们不必需要写这么多格式化的无用代码，直接在xml里设置就好了。

```xml
<android.support.design.widget.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:error="@{vm.usernameError}"
        app:errorEnabled="@{vm.usernameInValid}"
        app:errorTextAppearance="@color/login_error">

    <AutoCompleteTextView
            android:id="@+id/username"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="@string/prompt_username"
            android:inputType="text"
            android:maxLines="1"
            android:text="@={vm.username}"/>

</android.support.design.widget.TextInputLayout>
```

**TextInputLayout**有**setError**和**setErrorEnabled**两个方法，我们只需要改变viewmodel里的值，就可以达到相应的效果。

```java
 	private String usernameError = "";
    private String passwordError = "";
    private String emailError = "";

   	//get set method
    @Bindable("usernameError")
    public boolean isUsernameInValid() {
        return !isEmpty(usernameError);
    }

    @Bindable("emailError")
    public boolean isEmailInValid() {
        return !isEmpty(emailError);
    }

    @Bindable("passwordError")
    public boolean isPasswordInValid() {
        return !isEmpty(passwordError);
    }

    //···
```

看看效果：

![error.gif](https://github.com/ditclear/BaseViewBinding/blob/master/error.gif?raw=true)

### 写在最后

处理表单平常还是用rxjava 用的比较多，前几天发现databinding处理也很简单，相信databinding还会有更多很赞的地方等待大家发掘。

githud地址：https://github.com/ditclear/BaseViewBinding/tree/master/app/src/main/java/com/ditclear/app/validate