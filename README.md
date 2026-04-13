# MIMO-Android-Gamer_Collection

Gamer Collection is an app for register and store all data of videogames you have played, are playing o want to play to. You can store different information about a videogame, like its name, the platform you are playing it at (PlayStation, Xbox, PC, etc), the date when you bought it or the score you want to give it, among ohters. Additionally, you can create a saga and add videogames to it, too.

The main app view is a list with all the videogames you have stored. You can add a videogame by searching it and and modifying its information, which will later be displayed in the list. This list can be ordered by some fields like name or purchase date, and it can also be filtered by fields like platform, genre, score or state (Pending, In Progress or Finished), among others.

Finally, you can also store information about songs associated to a videogame, like its original soundtrack or some iconic song related to it.

All this information is stored associated to a user profile. To be able to use the app you must register entering a username, which has to be unique, and a password. This information is stored in a database to be able to associated each videogame to a username.

## Stack & Technologies
- **Language**: Kotlin & Coroutines.
- **Architecture**: Clean Architecture & MVVM (Model-View-ViewModel).
- **Dependency Injection**: Hilt.
- **Database**: Room for local persistence.
- **Networking**: Retrofit for remote API integration (RAWG API).
- **UI/UX**: DataBinding, ViewBinding, Jetpack Navigation Component, Picasso (Image loading), Material Design.
- **Firebase**: Analytics, Crashlytics, and Remote Config.
- **Security**: Security Crypto (Encrypted Shared Preferences).
- **Other**: Google Play Services (Maps & Location), In-app updates, Kotlinx Serialization.
