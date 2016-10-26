# EasyMVP 
[![Build Status](https://travis-ci.org/6thsolution/EasyMVP.svg?branch=master)](https://travis-ci.org/6thsolution/EasyMVP)  [ ![Download](https://api.bintray.com/packages/6thsolution/easymvp/easymvp-plugin/images/download.svg) ](https://bintray.com/6thsolution/easymvp/easymvp-plugin/_latestVersion)

A powerful, and very simple MVP library with **annotation processing** and **bytecode weaving**.

EasyMVP eliminates the boilerplate code for dealing with MVP implementation.

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
    - [Presenter](#presenter)
    - [View Annotations](#view-annotations)
    - [Injecting with Dagger](#injecting-with-dagger)
- [Clean Architecture Usage](#clean-architecture-usage)
    - [UseCase](#usecase)
    - [DataMapper](#datamapper)
- [FAQ](#faq)
- [Documentations](#documentations)
- [Author](#author)
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
    classpath 'com.sixthsolution.easymvp:easymvp-plugin:1.0.0-beta4'
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

You can create a presenter class by extending of the [`AbstractPresenter`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/AbstractPresenter.html) or [`RxPresenter`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/RxPresenter.html) (available in reactive API). 
```java
public class MyPresenter extends AbstractPresenter<MyView> {

}
```
To understand when the lifecycle methods of the presenter are called take a look at the following table:

| Presenter          | Activity       | Fragment           | View                    |
| ------------------ |----------------| -------------------| ------------------------|
| ``onViewAttached`` | ``onStart``    | ``onResume``       | ``onAttachedToWindow``  |
| ``onViewDetached`` | ``onStop``     | ``onPause``        | ``onDetachedFromWindow``|
| ``onDestroyed``    | ``onDestroy``  | ``onDestroy``      | ``onDetachedFromWindow``|

### View Annotations

Well, here is the magic part. There is no need for any extra inheritance in your `Activity`, `Fragment` or `View` classes to bind the presenter lifecycle.

Presenter's creation, lifecycle-binding, caching and destruction gets handled automatically by these annotations.

- [`@ActivityView`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/annotation/ActivityView.html) for all activities based on [`AppCompatActivity`](https://developer.android.com/reference/android/support/v7/app/AppCompatActivity.html).
- [`@FragmentView`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/annotation/FragmentView.html) for all fragments based on [`Default Fragment`](https://developer.android.com/reference/android/app/Fragment.html) or [`Support Fragment`](https://developer.android.com/reference/android/support/v4/app/Fragment.html)
- [`@CustomView`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/annotation/CustomView.html) for all views based on [`View`](https://developer.android.com/reference/android/view/View.html).

For injecting presenter into your activity/fragment/view, you can use [`@Presenter`](http://6thsolution.github.io/EasyMVP/api-javadoc/easymvp/annotation/Presenter.html) annotation. Also during configuration changes, previous instance of the presenter will be injected.

EasyMVP uses [Loaders](https://developer.android.com/guide/components/loaders.html) to preserve presenters across configurations changes.

Presenter instance will be set to null, after ``onDestroyed`` method injection.
 
`@ActivityView` example:
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

- You can specify the layout in `@ActivityView#layout` and EasyMVP will automatically inflate it for you.

`@FragmentView` example:
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

### Injecting with Dagger
`@Presenter` annotation will instantiate your presenter class by calling its default constructor, So you can't pass any objects to the constructor.

But if you are using [Dagger](https://google.github.io/dagger/), you can use its constructor injection feature to inject your presenter.

So what you need is make your presenter injectable and add `@Inject` annotation before `@Presenter`. Here is an example:
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

**Don't** inject dependencies after `super.onCreate(savedInstanceState);` in activities, `super.onActivityCreated(bundle);` in fragments and `super.onAttachedToWindow();` in custom views.

## Clean Architecture Usage
You can follow the principles of [Clean Architecture](https://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html) by applying 'easymvp-rx' plugin. Previous part was all about the presentation-layer, Now lets talk about the domain-layer.

**Domain Layer** holds all your business logic, it encapsulates and implements all of the use cases of the system.
This layer is a pure java module without any android SDK dependencies.

### UseCase
**UseCases** are the entry points to the domain layer. These use cases represent all the possible actions a developer can perform from the presentation layer.

Each use case should run off the main thread(UI thread), to avoid reinventing the wheel, EasyMVP uses RxJava to achieve this. 

You can create a use case class by extending of the following classes:
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
And the implementations of [`UseCaseExecutor`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/executer/UseCaseExecutor.html) and [`PostExecutionThread`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/executer/PostExecutionThread.html) are:
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
Each **DataMapper** transforms entities from the format most convenient for the use cases, to the format most convenient for the presentation layer. 

*But, why is it useful?*

Let's see `SuggestPlaces` use case again. Assume that you passed the `Mon` query to this use case and it emitted:
- Montreal
- Monterrey
- Montpellier

But you want to bold the `Mon` part of each suggestion like:
- **Mon**treal
- **Mon**terrey
- **Mon**tpellier 

So, you can use a data mapper to transform the `Place` object to the format most convenient for your presentation layer.

```java
public class PlaceSuggestionMapper extends DataMapper<List<SuggestedPlace>, List<Place>> {
    
    @Override
    public List<SuggestedPlace> call(List<Place> places) {
        //TODO for each Place object, use SpannableStringBuilder to make a partial bold effect
    }
}
```
Note that `Place` entity lives in the domain layer but `SuggestedPlace` entity lives in the presentation layer.

So, How to bind [`DataMapper`](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/easymvp/boundary/DataMapper.html) to `ObservableUseCase`?

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

## FAQ
How does EasyMVP work under the hood?

- For each annotated class with ``@ActivityView``, ``@FragmentView`` or ``@CustomView``, EasyMVP generates ``*_ViewDelegate`` class in the same package. These classes are responsible for binding presenter's lifecycle.
- EasyMVP uses bytecode weaving to call delegate classes inside your view implementation classes. You can find these manipulated classes in `build/weaver` folder.

## Documentations
EasyMVP [API](http://6thsolution.github.io/EasyMVP/api-javadoc/): Javadocs for the current API release

EasyMVP [RX-API](http://6thsolution.github.io/EasyMVP/rx-api-javadoc/): Javadocs for the current RX-API (Clean Architecture API) release

## Author
[Saeed Masoumi](https://github.com/SaeedMasoumi) 

## License
```
Copyright 2016 6thSolution

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
