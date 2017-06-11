package com.ditclear.app.validate;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.ditclear.app.BR;

import java.util.regex.Pattern;

/**
 * 页面描述：
 *
 * Created by ditclear on 2017/6/11.
 */

public class ValidateViewModel extends BaseObservable {

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


    private boolean usernameInValid = false;
    private boolean passwordInValid = false;
    private boolean emailInValid = false;

    private CallBack mCallBack;

    @Bindable({"username", "password", "email"})
    public boolean isBtnEnabled() {
        if (!usernamePattern.matcher(username).matches() || !emailPattern.matcher(email).matches()
                || !passwordPattern.matcher(password).matches()) {
            return false;
        }
        return true;
    }

    @Bindable("emailInValid")
    public boolean isEmailInValid() {
        return emailInValid;
    }

    public void setEmailInValid(boolean emailInValid) {
        this.emailInValid = emailInValid;
        notifyPropertyChanged(BR.emailInValid);
    }

    @Bindable("passwordInValid")
    public boolean isPasswordInValid() {
        return passwordInValid;
    }

    public void setPasswordInValid(boolean passwordInValid) {
        this.passwordInValid = passwordInValid;
        notifyPropertyChanged(BR.passwordInValid);
    }

    @Bindable("usernameInValid")
    public boolean isUsernameInValid() {
        return usernameInValid;
    }

    public void setUsernameInValid(boolean usernameInValid) {
        this.usernameInValid = usernameInValid;
        notifyPropertyChanged(BR.usernameInValid);
    }

    @Bindable("passwordInValid")
    public String getPasswordError() {
        if (isPasswordInValid()) {
            return "密码长度在6-16位间";
        }
        return null;
    }

    @Bindable("emailInValid")
    public String getEmailError() {
        if (isEmailInValid()) {
            return "邮箱格式";
        }
        return null;
    }

    @Bindable("usernameInValid")
    public String getUsernameError() {
        if (isUsernameInValid()) {
            return "请输入正确的用户名";
        }
        return null;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    private boolean validateSignIn() {

        boolean usernameValid = usernamePattern.matcher(username).matches();
        boolean emailValid = emailPattern.matcher(email).matches();
        boolean passwordValid = passwordPattern.matcher(password).matches();
        //验证表单，并且显示错误信息
        setUsernameInValid(!usernameValid);
        setEmailInValid(!emailValid);
        setPasswordInValid(!passwordValid);
        if (usernameValid && passwordValid && emailValid) {
            return true;
        }
        return false;
    }

    public void attemptToSignIn() {
        if (validateSignIn()) {
            // can load remote data
            mCallBack.signInSuccess();
        } else {
            mCallBack.signInFailure();
        }

    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {
        public void signInSuccess();

        public void signInFailure();
    }
}
