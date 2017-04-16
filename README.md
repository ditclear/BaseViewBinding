### 写在前面

在以往的android开发中，我们需要使用到findviewById方法来进行初始化view，所以对于页面复杂的情况，经常会看到十几二十行的findviewById方法，而如果需要设置点击事件，则又需要十几二十行的代码，写着很枯燥也很耽搁时间。作为一个程序员而不是搬运工，千万不要做没有效率的事，好在jake大神给我们带来了ButterKnife，在一段时间内为我们自动绑定了view，代替了繁杂的手写步骤。

可是相信使用过ButterKnife的同学都有一个感受，就是如果一个页面view过多的话，也需要一长串的bindView代码，导致一个页面轻轻松松300行+的代码量，看着也有点不舒服。而这些也都是重复性的工作，那么有什么办法解决吗？

***有啊！***

### DataBinding

说实话，DataBinding也出来蛮久了，可是用的人也蛮少的。原因可能是大家在选择的时候多选择了比较流行的MVP，二来是DataBinding还没有像前端那样完善，处于一个观望的状态。但是最近已经出现了越来越多的DataBinding的文章，说明越来越多的开发者开始注重这一块的技术，毕竟是金子总会发光的，不多说。

首先，我们需要开启DataBinding，不需要引入额外的依赖库，因为DataBinding是在support包中的。

```groovy
android {
    …
    dataBinding {
        enabled = true
    }
}
```

然后，我们只需要在改变一下xml的写法：


```java
//activity_main.xml
<?xml version="1.0" encoding="utf-8"?>
<layout>

    <data>

    </data>

    <RelativeLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:id="@+id/activity_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:paddingBottom="@dimen/activity_vertical_margin"
        android:paddingLeft="@dimen/activity_horizontal_margin"
        android:paddingRight="@dimen/activity_horizontal_margin"
        android:paddingTop="@dimen/activity_vertical_margin"
        tools:context="com.ditclear.app.MainActivity">

        <TextView
            android:id="@+id/content_tv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:text="Hello World!"/>
    </RelativeLayout>

</layout>
```

其实和以前差不多，就是外面包了一层<layout>，所以迁移起来很快。

然后就可以使用了，这样写了之后，系统会给你生成一个以xml文件名为前缀的文件。比如activity_main.xml，则会在build文件夹下生成ActivityMainBinding.java的文件，里面有各种绑定View的操作，这样我们就不用在自己的activity中写了。而使用的话直接使用ActivityMainBinding的对象就好了。
例子:

```java
package com.ditclear.app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ditclear.app.databinding.ActivityMainBinding;//生成的bind文件

public class MainActivity extends AppCompatActivity {


    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      	
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_main);

        mBinding.contentTv.setText("this is bindText");//轻轻松松拿到view
    }
}

```

很简单，而且activity瞬间简洁了。

当然还有更简单的写法，生成的binding文件和DataBindingUtil.setContentView()方法其实都是固定的，我们可以使用泛型来简化这些步骤，相信大家都想到了。

首先抽象出一个基类BaseBindActivity：

```java
**
 * 页面描述：activity基类
 * <p>
 * Created by ditclear on 2017/4/16.
 */

public abstract class BaseBindActivity<VB extends ViewDataBinding> extends AppCompatActivity {

    protected VB mBinding;
	...

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        View rootView = getLayoutInflater().inflate(this.getLayoutId(), null, false);
        mBinding = DataBindingUtil.bind(rootView);
      	super.setContentView(rootView);
    
        ...
        initView();
    }
  
 	@LayoutRes
    protected abstract int getLayoutId();
  
  	protected void initView();
}
```

接下来，我们继承它然后传入layoutId就行了。

```java
package com.ditclear.app;

import com.ditclear.app.base.BaseBindActivity;
import com.ditclear.app.databinding.ActivityMainBinding;

public class MainActivity extends BaseBindActivity<ActivityMainBinding> {


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        mBinding.contentTv.setText("this is bindText");
    }


}
```

需要注意的是，如果是以include的方式存在于xml文件中的话，就好比ButterKnife一样，是无法直接找到view的，比如我们include一个统一的toolbar布局。这时候可以通过findViewById来进行操作，毕竟不能忘本啊。

```java
  /**
     * 初始化toolbar
     */
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
          //有些布局可能不需要toolbar,判断是否为空，来进行剩下的操作
          //recyclerView和RefreshLayout同理
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            setShowBackNavigation(true);
        }
    }

```

这样我们就能操作toolbar了，和以前的写法一模一样，限制就是统一了toolbar的id为toolbar，但是我觉得这也能算是团队间的规范吧，免得出现形形色色的名称。

recyclerView和RefreshLayout同理。

#### 效果

![Image.png](http://upload-images.jianshu.io/upload_images/3722695-74c60034cef13de2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/400)

### 最后

这只是最基础的绑定view的例子以及使用泛型和DataBinding来构造我们的基类，只是写了一个BaseActivity，Fragment同样的道理。DataBinding当然不止这些，还能简化RecyclerView的操作，告别反复的自定义Adapter，推荐大帅的[DataBingding入门指南](http://blog.zhaiyifan.cn/2016/06/16/android-new-project-from-0-p7/)和用于绑定的RecyclerView的库[DataBindingAdapter](https://github.com/markzhai/DataBindingAdapter/blob/master/README_CN.md)。

##### ending...
