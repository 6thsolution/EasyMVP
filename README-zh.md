# EasyMVP
[![Build Status](https://travis-ci.org/6thsolution/EasyMVP.svg?branch=master)](https://travis-ci.org/6thsolution/EasyMVP)  [ ![Download](https://api.bintray.com/packages/6thsolution/easymvp/easymvp-plugin/images/download.svg) ](https://bintray.com/6thsolution/easymvp/easymvp-plugin/_latestVersion)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-EasyMVP-orange.svg?style=flat)](http://android-arsenal.com/details/1/4579)


一个带有**注解处理**（annotation processing）和**字节码缝合**（bytecode weaving）的强大且简洁的MVP库。

EasyMVP消除了开发MVP时多余的模板代码。

- [功能](#功能)
- [安装](#安装)
- [用途](#用途)
    - [Presenter](#presenter)
    - [View 注解](#view-注解)
    - [Dagger 注入](#dagger-注入)
- [Clean Architecture 用途](#clean-architecture-用途)
    - [用例](#用例)
    - [DataMapper](#datamapper)
- [问答](#问答)
- [文档](#文档)
- [作者](#作者)
- [演示](#演示)
- [翻译](#翻译)
- [许可](#许可)
- [更变日志](https://github.com/6thsolution/EasyMVP/blob/master/CHANGELOG.md)

## 功能
* 简易的整合
* 少量的模板代码
* 组合（Composition） 优于 聚合（inheritance）
* 使用少量注解即可开发MVP
* 使用 [加载器（Loader）](https://developer.android.com/guide/components/loaders.html)在设备配置变化时（configurations change） 保存 presenters
* 支持 [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html)。

## 安装
在项目根目录下的 `build.gradle` 加入 'easymvp' 插件:
```groovy
buildscript {
  repositories {
    jcenter()
   }
  dependencies {
    classpath 'com.sixthsolution.easymvp:easymvp-plugin:1.2.0-beta7'
  }
}
```
然后在模组内的 `build.gradle` 应用 'easymvp' 插件:
```groovy
apply plugin: 'easymvp'

android {
  ...
}
```
当项目应用到android gradle plugin version 2.2.0-alpha1 或更高时， 本插件不需要 [android-apt](https://bitbucket.org/hvisser/android-apt) 插件。 但是如果已经在项目中使用[android-apt](https://bitbucket.org/hvisser/android-apt)插件，请按照以下顺序声明插件（`easymvp` 插件 先于 `android-apt` 插件）.
```groovy
apply plugin: 'com.neenbedankt.android-apt'
apply plugin: 'easymvp'
```

如要使用响应式 API, 只需在模组内的 `build.gradle` 添加 'easymvp-rx' 插件，然后添加 RxJava 的依赖:
```groovy
apply plugin: 'easymvp-rx'

dependencies {
  compile 'io.reactivex:rxjava:x.y.z'
}

```
EasyMVP 也支持 RxJava2:
```groovy
apply plugin: 'easymvp-rx2'

dependencies {
  compile 'io.reactivex.rxjava2:rxjava:x.y.z'
}

```

**注意:** 所有快照版本都能在  [jfrog](https://oss.jfrog.org/oss-snapshot-local/com/sixthsolution/easymvp/) 找到

## 用途

首先， 你需要创建你的view接口。
```java
public interface MyView {
    void showResult(String resultText);

    void showError(String errorText);
}
```
然后你需要在你的`Activity`, `Fragment` 或 `CustomView`内实现`MyView` 。
*但是为什么呢?*

- 增加单元测试性。你可以不需要安卓SDK的依赖去测试你的presenter。
- 对view的实现进行代码解耦。
- 很方便的进行 stubbing. 例如, 你可以用`Fragment`来替换 `Activity`，但不需要对你的presenter作出任何修改。
- 高等级的具体实现（例如 presenter）, 不能依赖于的等级的具体实现（如 view 的实现）。

### Presenter

**Presenter** 扮演着中间人。它负责从数据层（data-layer）汲取数据然后在view展示出来。

你可以通过继承[`AbstractPresenter`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/AbstractPresenter.html) 或 [`RxPresenter`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/RxPresenter.html)(available in reactive API) 来创建presneter。
```java
public class MyPresenter extends AbstractPresenter<MyView> {

}
```
以下的表格提供了presenter的生命周期方法的调用时机：

| Presenter          | Activity       | Fragment           | View                    |
| ------------------ |----------------| -------------------| ------------------------|
| ``onViewAttached`` | ``onStart``    | ``onResume``       | ``onAttachedToWindow``  |
| ``onViewDetached`` | ``onStop``     | ``onPause``        | ``onDetachedFromWindow``|
| ``onDestroyed``    | ``onDestroy``  | ``onDestroy``      | ``onDetachedFromWindow``|

### View 注解

现在到最神奇的部分了。不需要继承任何`Activity`, `Fragment` 或 `View` 类，你就可以绑定presenter的生命周期。

Presenter的创建，生命周日的绑定， 缓存， 以及销毁都是由以下的注释自动处理。

- [`@ActivityView`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/annotation/ActivityView.html) 用于所有继承于 [`AppCompatActivity`](https://developer.android.com/reference/android/support/v7/app/AppCompatActivity.html)
- [`@FragmentView`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/annotation/FragmentView.html) 用于所有继承于 [`Default Fragment`](https://developer.android.com/reference/android/app/Fragment.html) or [`Support Fragment`](https://developer.android.com/reference/android/support/v4/app/Fragment.html)
- [`@CustomView`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/annotation/CustomView.html) 用于所有继承于 [`View`](https://developer.android.com/reference/android/view/View.html)。

要注入presenter到你的activity/fragment/view， 你可以使用[`@Presenter`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/annotation/Presenter.html)注释。
而且，当设备配置变化时（configurations change）， 之前的presenter的实例会被重新注入。

EasyMVP 用 [加载器（Loader）](https://developer.android.com/guide/components/loaders.html) 以在设备配置变化时（configurations change） 保存 presenters。

在``onDestroyed``方法注入以后， Presenter的实例会被设成null。

`@ActivityView` 例子:
```java
@ActivityView(layout = R.layout.my_activity, presenter = MyPresenter.class)
public class MyActivity extends AppCompatActivity implements MyView {

    @Presenter
    MyPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Now presenter is injected.
    }

    @Override
    public void showResult(String resultText) {
        //do stuff
    }

    @Override
    public void showError(String errorText) {
        //do stuff
    }
}
```

- 你可以在`@ActivityView#layout`指明layout， 然后EasyMVP会自动为你将其实例化。

`@FragmentView` 例子:
```java
@FragmentView(presenter = MyPresenter.class)
public class MyFragment extends Fragment implements MyView {

    @Presenter
    MyPresenter presenter;

    @Override
    public void onResume() {
        super.onResume();
        // Now presenter is injected.
    }

    @Override
    public void showResult(String resultText) {
        //do stuff
    }

    @Override
    public void showError(String errorText) {
        //do stuff
    }
}
```

`@CustomView` 例子:
```java
@CustomView(presenter = MyPresenter.class)
public class MyCustomView extends View implements MyView {

    @Presenter
    MyPresenter presenter;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Now presenter is injected.
    }

    @Override
    public void showResult(String resultText) {
        //do stuff
    }

    @Override
    public void showError(String errorText) {
        //do stuff
    }
}
```

### Dagger 注入
`@Presenter`注解会通过默认构造器自动实例化你的Presenter类，所以你不能向构造器传参数。

不过，如果你在使用[Dagger](https://google.github.io/dagger/)， 你可以用他的构造器注入功能去注入你的Presenter。

所以，你只需要把你的Presenter声明为可注入的， 然后在`@Presenter`之前添加`@Inject`。以下是例子：
```java
public class MyPresenter extends AbstractPresenter<MyView> {

    @Inject
    public MyPresenter(UseCase1 useCase1, UseCase2 useCase2){

    }
}

@ActivityView(layout = R.layout.my_activity, presenter = MyPresenter.class)
public class MyActivity extends AppCompatActivity implements MyView {

    @Inject
    @Presenter
    MyPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SomeDaggerComponent.injectTo(this);
        super.onCreate(savedInstanceState);
     }

    //...
}

```

**千万不要** 在activities的`super.onCreate(savedInstanceState);`， fragments的`super.onActivityCreated(bundle);`， 和定制View的`super.onAttachedToWindow();`之后注入依赖。

## Clean Architecture 用途
使用了'easymvp-rx'插件之后，你就可以遵从[Clean Architecture](https://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html)的原则。之前的部分是讲述presentation-layer， 现在我们来讲解domain-layer。

**Domain Layer** 持有你所有的业务逻辑, 他封装，以及实现所有系统用例。
这一层是纯粹的java模组，没有任何安卓SDK的依赖。

### 用例
**用例** 是domain layer的起始点。这些用例代表了所有开发者能在presentation layer进行的操作。

每个用例应该在主线程（视图线程）外运行， 为了不重新造轮子， EasyMVP使用RxJava来实现这个功能。

你可以通过继承以下类来创建用例：
 - [`ObservableUseCase`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/usecase/ObservableUseCase.html)
 - [`CompletableUseCase`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/usecase/CompletableUseCase.html)

```java
public class SuggestPlaces extends ObservableUseCase<List<Place>, String> {

    private final SearchRepository searchRepository;

    public SuggestPlaces(SearchRepository searchRepository,
                         UseCaseExecutor useCaseExecutor,
                         PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        this.searchRepository = searchRepository;
    }

    @Override
    protected Observable<List<Place>> interact(@NonNull String query) {
        return searchRepository.suggestPlacesByName(query);
    }
}
```

```java
public class InstallTheme extends CompletableUseCase<File> {

    private final ThemeManager themeManager;
    private final FileManager fileManager;

    public InstallTheme(ThemeManager themeManager,
                           FileManager fileManager,
                           UseCaseExecutor useCaseExecutor,
                           PostExecutionThread postExecutionThread) {
        super(useCaseExecutor, postExecutionThread);
        this.themeManager = themeManager;
        this.fileManager = fileManager;
    }

    @Override
    protected Completable interact(@NonNull File themePath) {
        return themeManager.install(themePath)
                .andThen(fileManager.remove(themePath))
                .toCompletable();
    }

}

```
然后 [`UseCaseExecutor`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/executer/UseCaseExecutor.html) 和 [`PostExecutionThread`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/executer/PostExecutionThread.html) 的实现为:
```java
public class UIThread implements PostExecutionThread {

    @Override
    public Scheduler getScheduler() {
        return AndroidSchedulers.mainThread();
    }
}

public class BackgroundThread implements UseCaseExecutor {

    @Override
    public Scheduler getScheduler() {
        return Schedulers.io();
    }
}
```

### DataMapper
每个**DataMapper**会把实体从对用例最方便的格式，转换成对presentation layer最方便的格式。

*可是，为什么这会有用呢?*

我们再来看看`SuggestPlaces`的用例。假设你向这个用例传一个`Mon`的查询， 然后他发出：
- Montreal
- Monterrey
- Montpellier

不过你想把`Mon`的部分粗体化：
- **Mon**treal
- **Mon**terrey
- **Mon**tpellier

所以你可以通过DataMapper来把`Place`实体转换成对presentation layer最方便的格式。

```java
public class PlaceSuggestionMapper extends DataMapper<List<SuggestedPlace>, List<Place>> {

    @Override
    public List<SuggestedPlace> call(List<Place> places) {
        //TODO for each Place object, use SpannableStringBuilder to make a partial bold effect
    }
}
```
注意`Place`实体存在于domain layer， 不过`SuggestedPlace`存在于presentation layer。

现在，那我们怎么把[`DataMapper`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/boundary/DataMapper.html)与`ObservableUseCase`绑定呢？

```java
public class MyPresenter extends RxPresenter<MyView> {

    private SuggestPlace suggestPlace;
    private SuggestPlaceMapper suggestPlaceMapper;

    @Inject
    public MyPresenter(SuggestPlace suggestPlace, SuggestPlaceMapper suggestPlaceMapper){
        this.suggestPlace = suggestPlace;
        this.suggestPlaceMapper = suggestPlaceMapper;
    }

    void suggestPlaces(String query){
        addSubscription(
                       suggestPlace.execute(query)
                                     .map(suggetsPlaceMapper)
                                     .subscribe(suggestedPlaces->{
                                           //do-stuff
                                      })
                        );
    }
}
```

## 问答
EasyMVP的原理是什么？

- 每个注释了 ``@ActivityView``, ``@FragmentView`` or ``@CustomView``的类中, EasyMVP 会在同样的包中生成 ``*_ViewDelegate`` 类。这些类是负责绑定presenter的生命周期。
- EasyMVP 用字节码缝合（bytecode weaving） 来调用在你view实现类里的委托类。你可以在`build/weaver`文件夹中找到这些处理过的类。

EasyMVP有任何使用限制吗？
- EasyMVP 使用 android's transform API 来实现字节码缝合（bytecode weaving）。 注意这个 [问题](https://code.google.com/p/android/issues/detail?id=210730)，  [Jack toolchain](https://source.android.com/source/jack.html) 尚未对此支持。

EasyMVP支持kotlin吗？
- 他支持kotlin, 更多细节请参阅 [问题](https://github.com/6thsolution/EasyMVP/issues/22)。

## 文档
EasyMVP [API](http://6thsolution.github.io/EasyMVP/api-javadoc/): 现发布版本的API文档（Javadocs）

EasyMVP [RX-API](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/): 现发布版本的RX-API (Clean Architecture API)文档（Javadocs）

EasyMVP [RX2-API](http://6thsolution.github.io/EasyMVP/rx2-api-javadoc/): 现发布版本的RX2-API (Clean Architecture API)文档（Javadocs）

## 演示
[CleanTvMaze](https://github.com/mohamad-amin/CleanTvMaze)

## 作者
[Saeed Masoumi](https://github.com/SaeedMasoumi)

## 翻译
翻译：[Robert Zhang](https://github.com/zi-yang-zhang)

核对：[Jianhui Zhu](https://github.com/JianhuiZhu)

## 许可
```
Copyright 2016-2017 6thSolution Technologies Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
