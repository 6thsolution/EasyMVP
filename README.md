# EasyMVP 
[![Build Status](https://travis-ci.org/6thsolution/EasyMVP.svg?branch=master)](https://travis-ci.org/6thsolution/EasyMVP)  [ ![Download](https://api.bintray.com/packages/6thsolution/easymvp/easymvp-plugin/images/download.svg) ](https://bintray.com/6thsolution/easymvp/easymvp-plugin/_latestVersion)

A full-featured framework that allows building android applications following the principles of Clean Architecture.

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
    - [Presenter](#presenter)
    - [View Annotations](#view-annotations)
- [License](#license)

## Features
* Easy integration
* Less boilerplate
* Composition over inheritance
* Implement MVP with just few annotations
* Use [Loaders](https://developer.android.com/guide/components/loaders.html) to preserve presenters across configurations changes
* Support [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html) approach.

## Installation
Configure your project-level `build.gradle` to include the 'easymvp' plugin:
```groovy
buildscript {
  repositories {
    jcenter()
   }
  dependencies {
    classpath 'com.sixthsolution.easymvp:easymvp-plugin:1.0.0-beta3'
  }
}
```
Then, apply the 'easymvp' plugin in your module-level `build.gradle`:
```groovy
apply plugin: 'easymvp'

android {
  ...
}
```
For reactive API, simply apply the 'easymvp-rx' plugin in your module-level `build.gradle`  and then add the RxJava dependency:
```groovy
apply plugin: 'easymvp-rx'

dependencies {
  compile 'io.reactivex:rxjava:x.y.z'
}

```

## Usage

First thing you will need to do is to create your view interface.
```java
public interface MyView {
    void showResult(String resultText);

    void showError(String errorText);
}
```
Then you should implement `MyView` in your `Activity`, `Fragment` or `CustomView`.
*But why?*

- Improve unit testability. You can test your presenter without any android SDK dependencies.
- Decouple the code from the implementation view.
- Easy stubbing. For example, you can replace your `Activity` with a `Fragment` without any changes in your presenter.
- High level details (such as the presenter), can't depend on low level concrete details like the implementation view.

### Presenter

**Presenter** acts as the middle man. It retrieves data from the data-layer and shows it in the View.

You can create a presenter class by extending of the `AbstractPresenter` or `RxPresenter` (available in reactive API). 
```java
public class MyPresenter extends AbstractPresenter<MyView> {

}
```
To understand when the lifecycle methods of the presenter are called take a look at the following table:

| Presenter          | Activity       | Fragment           | View                    |
| ------------------ |----------------| -------------------| ------------------------|
| ``onViewAttached`` | ``onStart``    | ``onResume``       | ``onAttachedToWindow``  |
| ``onViewDetached`` | ``onStop``     | ``onPause``        | ``onDetachedFromWindow``|

`Presenter#onDestroyed` will be invoked inside [`Loader#onReset`](https://developer.android.com/reference/android/content/Loader.html#onReset()).

### View Annotations

Well, here is the magic part. There is no need for any extra inheritance in your `Activity`, `Fragment` or `View` classes to bind the presenter lifecycle.

Presenter's creation, lifecycle-binding, caching and destruction gets handled automatically by these annotations.

- `@ActivityView` for all activities based on [`AppCompatActivity`](https://developer.android.com/reference/android/support/v7/app/AppCompatActivity.html).
- `@FragmentView` for all fragments based on [`Default Fragment`](https://developer.android.com/reference/android/app/Fragment.html) or [`Support Fragment`](https://developer.android.com/reference/android/support/v4/app/Fragment.html)
- `@CustomView` for all views based on [`View`](https://developer.android.com/reference/android/view/View.html).

For injecting presenter into your activity/fragment/view, you can use `@Presenter` annotation. Also during configuration changes, previous instance of the presenter will be injected.

Presenter instance will be set to null after [`LoaderCallbacks#onLoadFinished`](https://developer.android.com/reference/android/app/LoaderManager.LoaderCallbacks.html#onLoadFinished)

`@ActivityView` example:
```java
@ActivityView(layout = R.layout.my_activity, presenter = MyPresenter.class)
public class MyActivity extends AppCompatActivity implements MyView {

    @Presenter
    MyPresenter presenter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

- You can specify the layout in `@ActivityView#layout` and EasyMVP will automatically inflate it for you.
- You have access to the presenter instance after `super.onCreate(savedInstanceState);` in `onCreate` method.

`@FragmentView` example:
```java
@FragmentView(presenter = MyPresenter.class)
public class MyFragment extends Fragment implements MyView {

    @Presenter
    MyPresenter presenter;
    
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
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
- You have access to the presenter instance after `super.onActivityCreated(bundle);` in `onActivityCreated` method.

`@CustomView` example:
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
- You have access to the presenter instance after `super.onAttachedToWindow();` in `onAttachedToWindow` method.


## License

EasyMVP is under the Apache 2.0 license. See [LICENSE](LICENSE) file for details.