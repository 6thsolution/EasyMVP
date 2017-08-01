Change Log
==========

Version 1.2.0-beta10
-------------

_2017-08-1_

* Fix conductor `Controller#onDestroy` NPE 


Version 1.2.0-beta9
-------------

_2017-07-24_

* Minor bug fix


Version 1.2.0-beta8
-------------

_2017-06-18_

* Force loader to call `Presenter#onDestroyed` inside `Controller#onDestroy` in conductor
* Minor bug fix

Version 1.2.0-beta7
-------------

_2017-06-11_

* Support new android gradle plugin (3.x.x)
* Minor bug fix



Version 1.2.0-beta6
-------------

_2017-05-29_

* Support for different types of UseCases. ([#18](https://github.com/6thsolution/EasyMVP/issues/18)) 

Version 1.2.0-beta5
-------------

_2017-05-19_

* Add `@PresenterId` annotation to support multiple instances of a same view class in a parent view. ([#28](https://github.com/6thsolution/EasyMVP/issues/28)) 

Version 1.2.0-beta2
-------------

_2017-05-10_

* When return `super.onCreateView` doesn't override, weaver will override and return its super method. 

Version 1.2.0-beta1
-------------

_2017-05-03_

* Add `@ConductorController` annotation to support [Conductor](https://github.com/bluelinelabs/Conductor). ([#10](https://github.com/6thsolution/EasyMVP/issues/10))


Version 1.1.1
-------------

_2017-04-19_

*  Force compiler to generate Loaders classes


Version 1.1.0
-------------

_2017-03-12_

*  Support RxJava 2 (special thanks to [lujiajing1126](https://github.com/lujiajing1126)).


Version 1.0.5
-------------

_2017-01-11_

*  Clear the view reference in `AbstractPresenter#onViewDetached`.

Version 1.0.4
-------------

_2016-11-28_

* Use [LoaderManager](https://developer.android.com/reference/android/app/LoaderManager.html) for `android.app.Activity` [#16](https://github.com/6thsolution/EasyMVP/issues/16)

Version 1.0.3 
-------------

_2016-11-24_

* Downgrading the version of `jsr305`.

Version 1.0.2 
-------------

_2016-11-24_

* Use `jsr305` at compilation step.

Version 1.0.1 
-------------

_2016-10-29_

* Fix a bug related to using both `apt` and `annotationProcessor` configurations.

Version 1.0.0 
-------------

_2016-10-28_

* First public release.
