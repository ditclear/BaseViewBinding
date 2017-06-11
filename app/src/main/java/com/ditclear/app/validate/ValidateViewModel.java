package com.ditclear.app.validate;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.ditclear.app.BR;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

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


    private String usernameError = "";
    private String passwordError = "";
    private String emailError = "";

    private CallBack mCallBack;

    @Bindable({"username", "password", "email"})
    public boolean isBtnEnabled() {
        if (!usernamePattern.matcher(username).matches() || !emailPattern.matcher(email).matches()
                || !passwordPattern.matcher(password).matches()) {
            return false;
        }
        return true;
    }

    @Bindable
    public String getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(String usernameError) {
        this.usernameError = usernameError;
        notifyPropertyChanged(BR.usernameError);
    }

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

    @Bindable
    public String getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
        notifyPropertyChanged(BR.passwordError);
    }

    @Bindable
    public String getEmailError() {
        return emailError;
    }

    public void setEmailError(String emailError) {
        this.emailError = emailError;
        notifyPropertyChanged(BR.emailError);
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


    public void attemptToSignIn() {
        clearError();
        mockData().delay(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<String[]>() {
                    @Override
                    public void accept(@NonNull String[] responses) throws Exception {
                        if ("username".equals(responses[0])) {
                            setUsernameError(responses[1]);
                            mCallBack.signInFailure();
                        } else if ("password".equals(responses[0])) {
                            setPasswordError(responses[1]);
                            mCallBack.signInFailure();
                        } else {
                            mCallBack.signInSuccess();
                        }
                    }
                });

    }

    private void clearError() {
        setEmailError("");
        setUsernameError("");
        setPasswordError("");
    }

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public boolean isEmpty(String error) {
        return error == null || "".equals(error) || "".equals(error.trim());
    }

    private Observable<String[]> mockData() {
        return Observable.just(new Random().nextInt(3))
                .flatMap(new Function<Integer,ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(@NonNull Integer random)
                            throws Exception {
                        Log.d("random", "apply() called with: random = [" + random + "]");
                        String response = "";
                        String username = "username,用户名不存在";
                        String password = "password,密码不正确";
                        if (random == 1) {
                            response = username;
                        } else if (random == 2) {
                            response = password;
                        }
                        return Observable.just(response);
                    }
                }).map(new Function<String,String[]>() {
                    @Override
                    public String[] apply(@NonNull String response) throws Exception {
                        return response.split(",");
                    }
                });
    }

    public interface CallBack {
        public void signInSuccess();

        public void signInFailure();
    }
}
