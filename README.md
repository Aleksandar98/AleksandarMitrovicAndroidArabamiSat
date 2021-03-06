# Arabami Sat (SellMyCar)


Arabami Sat is an application that allows you to:

### Login

![](https://i.ibb.co/RT457hf/img1.png)
* Login (Email,Google,Facebook)
* Create New account 
* Reset password if you forgot

### Main Activity
![](https://i.ibb.co/HgBbwgb/img2.jpg)
* View all car ads
* Sort them by location (show nearest ads to your current location)
* Show favorite ads

### Add Activity
![](https://i.ibb.co/HxPMgCm/img3.png)
* Add your own car Ad
 **Support for multiple image upload**


### Detail Activity
![](https://i.ibb.co/PQ1GMBL/img4.png)
* Detail view of the car ad
* Call ad owner
* Email ad owner
* Add ad to favorites


### Features:
* Kotlin
* MVVM
* Dependency Injection (HILT)
* Repository pattern
* Unit Testing
* Material Design
* LocationManager
* Courutines
* Firebase
  * Firestore
  * Firebase storeage
  * Firebase auth
  * Firebase Crashlytics

### Description:

All business logic is kept out of activities they only interact with views and observe **LiveData** changes from view models

Every activity has its own **ViewModel**

Every model has its own repository from where it is fetched

Adding external database hosted on cloud service provider would only change repository class and that would mean that we need to implement caching with **Room** and use **Retrofit** to fetch data

**Room** is not used here because of firebase's caching of local paths, it would be obsolete and unnecessary.
When uploading images offline, firebase stores local paths for images and stores upload intent in new document on firestore. **Firebase function** (*since this is paid feature I build my own observer inside an app*) observes document and automatically upload those images intended to be uploaded when online.In that context there are no local paths being stored.

#### Testing:
While testing MVVM architecture I am creating **Fake repositories** since it is not good practice to contact database nor ResfulAPI in tests because they are slow.

Fake repositories are injected in the view model and used for test.

Only DetailViewModel and AddingViewModel are tested since authentication is done using firebase and that requires internet connection




### Possible upgrades:
* Delete your ad
* Edit your ad
* More filters
* Search option
* Comment section for ad
* Video upload
* Google map integration
* Pagination
* Navigation components
* Integretion tests
* UI test









