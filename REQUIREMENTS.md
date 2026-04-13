# REQUIREMENTS.md — Gamer Collection

**Version:** 1.0  
**Date:** 2026-04-14  
**Standard:** IEEE 830 / User Story + Gherkin (BDD)  
**Platform:** Android  

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Scope](#2-scope)
3. [Actors](#3-actors)
4. [Feature Areas](#4-feature-areas)
   - [US-01 — User Registration](#us-01--user-registration)
   - [US-02 — User Login](#us-02--user-login)
   - [US-03 — User Logout](#us-03--user-logout)
   - [US-04 — View Game Collection](#us-04--view-game-collection)
   - [US-05 — Search Games in Collection](#us-05--search-games-in-collection)
   - [US-06 — Filter Game Collection](#us-06--filter-game-collection)
   - [US-07 — Sort Game Collection](#us-07--sort-game-collection)
   - [US-08 — Filter by Game State](#us-08--filter-by-game-state)
   - [US-09 — Search and Add Games from External Catalog](#us-09--search-and-add-games-from-external-catalog)
   - [US-10 — View Game Detail](#us-10--view-game-detail)
   - [US-11 — Add New Game Manually](#us-11--add-new-game-manually)
   - [US-12 — Edit Game](#us-12--edit-game)
   - [US-13 — Delete Game](#us-13--delete-game)
   - [US-14 — Mark Purchase Location on Map](#us-14--mark-purchase-location-on-map)
   - [US-15 — View Game Trailer](#us-15--view-game-trailer)
   - [US-16 — Manage Game Songs](#us-16--manage-game-songs)
   - [US-17 — View Saga Collection](#us-17--view-saga-collection)
   - [US-18 — Create Saga](#us-18--create-saga)
   - [US-19 — Edit Saga](#us-19--edit-saga)
   - [US-20 — Delete Saga](#us-20--delete-saga)
   - [US-21 — Change Password](#us-21--change-password)
   - [US-22 — Change App Language](#us-22--change-app-language)
   - [US-23 — Configure Default Sort](#us-23--configure-default-sort)
   - [US-24 — Configure Swipe-to-Refresh](#us-24--configure-swipe-to-refresh)
   - [US-25 — Change App Theme](#us-25--change-app-theme)
   - [US-26 — Export Collection Data](#us-26--export-collection-data)
   - [US-27 — Import Collection Data](#us-27--import-collection-data)
   - [US-28 — Delete Account](#us-28--delete-account)
   - [US-29 — In-App Update](#us-29--in-app-update)
5. [Non-Functional Requirements](#5-non-functional-requirements)

---

## 1. Introduction

This document specifies the software requirements for **Gamer Collection**, an Android mobile application that allows registered users to manage a personal catalogue of video games. Users can register, log in, and maintain a collection of games with detailed metadata, organize them into sagas, associate songs, and configure personal preferences.

---

## 2. Scope

The application covers the following functional areas:

- User authentication (registration, login, logout, password management)
- Game collection management (add, view, edit, delete, search, sort, filter)
- Saga management (create, view, edit, delete)
- Song management per game
- App settings and personalization
- Data import/export
- In-app updates via Google Play

---

## 3. Actors

| Actor | Description |
|-------|-------------|
| **Guest** | An unauthenticated user who can only access the login and registration screens. |
| **Registered User** | An authenticated user who has full access to all app features. |
| **System** | The application itself, responsible for automatic operations such as update checks or notifications. |

---

## 4. Feature Areas

---

### US-01 — User Registration

**As a** guest,  
**I want to** create a new account with a unique username and a password,  
**so that** I can start managing my personal game collection.

```gherkin
Feature: User Registration

  Background:
    Given the user is on the registration screen

  Scenario: Successful registration with valid credentials
    When the user enters a valid unique username
    And the user enters a password with more than 5 characters
    And the user confirms the password correctly
    And the user taps the "Register" button
    Then the account is created successfully
    And the user is automatically logged in
    And the user is redirected to the main game collection screen

  Scenario: Registration fails with a blank username
    When the user leaves the username field empty
    And the user enters a valid password
    And the user taps the "Register" button
    Then an error message is displayed indicating the username is invalid
    And the user remains on the registration screen

  Scenario: Registration fails with a password shorter than 6 characters
    When the user enters a valid username
    And the user enters a password of 5 characters or fewer
    And the user taps the "Register" button
    Then an error message is displayed indicating the password is too short
    And the user remains on the registration screen

  Scenario: Registration fails when passwords do not match
    When the user enters a valid username
    And the user enters a valid password
    And the user enters a different password in the confirmation field
    And the user taps the "Register" button
    Then an error message is displayed indicating the passwords do not match
    And the user remains on the registration screen

  Scenario: Registration fails because the username already exists
    When the user enters a username that is already registered
    And the user enters a valid password
    And the user confirms the password correctly
    And the user taps the "Register" button
    Then an error message is displayed indicating the username is already taken
    And the user remains on the registration screen
```

---

### US-02 — User Login

**As a** guest,  
**I want to** log in with my username and password,  
**so that** I can access my personal game collection.

```gherkin
Feature: User Login

  Background:
    Given the user is on the login screen

  Scenario: Successful login with valid credentials
    When the user enters a registered username
    And the user enters the correct password
    And the user taps the "Login" button
    Then the user is authenticated successfully
    And the user is redirected to the main game collection screen

  Scenario: Login fails with incorrect password
    When the user enters a registered username
    And the user enters a wrong password
    And the user taps the "Login" button
    Then an error message is displayed indicating the credentials are incorrect
    And the user remains on the login screen

  Scenario: Login fails with a blank username
    When the user leaves the username field empty
    And the user enters a password
    And the user taps the "Login" button
    Then an error message is displayed indicating the username is invalid
    And the "Login" button is disabled until validation passes

  Scenario: App remembers the last used username
    Given the user has previously logged in successfully
    When the user opens the app again
    Then the username field is pre-filled with the last used username
```

---

### US-03 — User Logout

**As a** registered user,  
**I want to** log out of the app,  
**so that** my account data is protected on shared devices.

```gherkin
Feature: User Logout

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Successful logout
    When the user taps the "Logout" button
    Then the user session is terminated
    And the user is redirected to the login screen
    And accessing any collection screen is no longer possible without logging in again

  Scenario: Logout clears local session data
    When the user taps the "Logout" button
    Then the authentication token is removed from local storage
    And the user's session preferences are cleared
```

---

### US-04 — View Game Collection

**As a** registered user,  
**I want to** see a list of all my video games,  
**so that** I can get an overview of my collection.

```gherkin
Feature: View Game Collection

  Background:
    Given the user is authenticated and on the games screen

  Scenario: Collection is displayed with at least one game
    Given the user has at least one game in their collection
    Then the game list is displayed with each game showing its name, platform, cover image, and score

  Scenario: Empty collection state
    Given the user has no games in their collection
    Then an empty state message is displayed
    And a call-to-action is shown to add the first game

  Scenario: Pull-to-refresh when swipe refresh is enabled
    Given the swipe-to-refresh setting is enabled
    When the user pulls down on the game list
    Then the collection is refreshed and updated

  Scenario: Total game count is visible
    Given the user has multiple games in their collection
    Then the total count of games matching the current view is displayed
```

---

### US-05 — Search Games in Collection

**As a** registered user,  
**I want to** search my collection by game name,  
**so that** I can quickly find a specific game.

```gherkin
Feature: Search Games in Collection

  Background:
    Given the user is authenticated and on the games screen

  Scenario: Search returns matching results
    When the user types a game name in the search bar
    Then only games whose name contains the search query are displayed

  Scenario: Search returns no results
    When the user types a term that matches no game name
    Then an empty result state is shown

  Scenario: Clearing the search restores the full list
    Given the user has performed a search
    When the user clears the search bar
    Then the full game collection is displayed again
```

---

### US-06 — Filter Game Collection

**As a** registered user,  
**I want to** apply filters to my game collection,  
**so that** I can view only the games that match specific criteria.

```gherkin
Feature: Filter Game Collection

  Background:
    Given the user is authenticated and on the games screen

  Scenario: Filter by one or more platforms
    When the user opens the filter panel
    And the user selects one or more platforms (e.g., "PlayStation", "PC")
    And the user applies the filter
    Then only games belonging to the selected platforms are displayed

  Scenario: Filter by genre
    When the user opens the filter panel
    And the user selects one or more genres
    And the user applies the filter
    Then only games of the selected genres are displayed

  Scenario: Filter by format
    When the user opens the filter panel
    And the user selects one or more formats (e.g., "Digital", "Physical")
    And the user applies the filter
    Then only games with the selected formats are displayed

  Scenario: Filter by score range
    When the user opens the filter panel
    And the user sets a minimum and maximum score
    And the user applies the filter
    Then only games with a score within that range are displayed

  Scenario: Filter by release date range
    When the user opens the filter panel
    And the user sets a minimum and a maximum release date
    And the user applies the filter
    Then only games released within that date range are displayed

  Scenario: Filter by purchase date range
    When the user opens the filter panel
    And the user sets a minimum and a maximum purchase date
    And the user applies the filter
    Then only games purchased within that date range are displayed

  Scenario: Filter by price range
    When the user opens the filter panel
    And the user sets a minimum and maximum price
    And the user applies the filter
    Then only games with a price within that range are displayed

  Scenario: Filter by GOTY flag
    When the user opens the filter panel
    And the user toggles the "Game of the Year" filter
    And the user applies the filter
    Then only games marked as GOTY are displayed

  Scenario: Filter by loaned status
    When the user opens the filter panel
    And the user selects the "Loaned" filter
    And the user applies the filter
    Then only games that have been lent to someone are displayed

  Scenario: Filter by saga association
    When the user opens the filter panel
    And the user selects "Has Saga"
    And the user applies the filter
    Then only games that belong to a saga are displayed

  Scenario: Filter by songs association
    When the user opens the filter panel
    And the user selects "Has Songs"
    And the user applies the filter
    Then only games that have at least one associated song are displayed

  Scenario: Clear all active filters
    Given the user has applied one or more filters
    When the user removes all filters and applies
    Then the full unfiltered collection is displayed
```

---

### US-07 — Sort Game Collection

**As a** registered user,  
**I want to** sort my game collection by different fields,  
**so that** I can view games in the order that is most useful to me.

```gherkin
Feature: Sort Game Collection

  Background:
    Given the user is authenticated and on the games screen

  Scenario: Sort by name ascending
    When the user opens the sort dialog
    And the user selects "Name" as the sort field
    And the user selects "Ascending" order
    And the user confirms the selection
    Then the game list is sorted alphabetically from A to Z

  Scenario: Sort by name descending
    When the user opens the sort dialog
    And the user selects "Name" as the sort field
    And the user selects "Descending" order
    And the user confirms the selection
    Then the game list is sorted alphabetically from Z to A

  Scenario: Sort by platform
    When the user opens the sort dialog
    And the user selects "Platform" as the sort field
    And the user confirms the selection
    Then the game list is sorted by platform name

  Scenario: Sort by release date
    When the user opens the sort dialog
    And the user selects "Release Date" as the sort field
    And the user confirms the selection
    Then the game list is sorted by release date

  Scenario: Sort by purchase date
    When the user opens the sort dialog
    And the user selects "Purchase Date" as the sort field
    And the user confirms the selection
    Then the game list is sorted by purchase date

  Scenario: Sort by price
    When the user opens the sort dialog
    And the user selects "Price" as the sort field
    And the user confirms the selection
    Then the game list is sorted by price

  Scenario: Sort by score
    When the user opens the sort dialog
    And the user selects "Score" as the sort field
    And the user confirms the selection
    Then the game list is sorted by score

  Scenario: Cancel the sort dialog
    When the user opens the sort dialog
    And the user taps "Cancel"
    Then no sorting changes are applied
    And the list remains in its previous order
```

---

### US-08 — Filter by Game State

**As a** registered user,  
**I want to** quickly filter my collection by the game's current progress state,  
**so that** I can see what I am playing, what is pending, or what I have finished.

```gherkin
Feature: Filter by Game State

  Background:
    Given the user is authenticated and on the games screen

  Scenario: View only pending games
    When the user selects the "Pending" state tab
    Then only games with the state "Pending" are displayed

  Scenario: View only games in progress
    When the user selects the "In Progress" state tab
    Then only games with the state "In Progress" are displayed

  Scenario: View only finished games
    When the user selects the "Finished" state tab
    Then only games with the state "Finished" are displayed

  Scenario: View all games regardless of state
    When the user selects the "All" tab or clears the state filter
    Then all games are displayed regardless of their state
```

---

### US-09 — Search and Add Games from External Catalog

**As a** registered user,  
**I want to** search for games in an external games database,  
**so that** I can quickly find and add games to my collection with pre-filled metadata.

```gherkin
Feature: Search Games from External Catalog (RAWG API)

  Background:
    Given the user is authenticated and on the game search screen

  Scenario: Browse the external catalog without a search query
    When the user opens the search screen
    Then a paginated list of games from the external catalog is displayed

  Scenario: Search for a specific game by name
    When the user types a game name in the search bar
    Then the external catalog is queried and results matching the name are displayed
    And the total number of matching results is shown

  Scenario: Load more results with pagination
    Given results are displayed on the search screen
    When the user scrolls to the bottom of the list
    Then the next page of results is loaded and appended to the list

  Scenario: Add a game from the external catalog to the collection
    When the user taps on a game from the external catalog
    Then the game detail screen is shown with pre-filled metadata from the external source
    And the user can review and save the game to their local collection

  Scenario: Search returns no results
    When the user types a term that does not match any game in the external catalog
    Then an empty state message is displayed

  Scenario: API call fails due to network error
    Given there is no network connection
    When the user performs a search
    Then an error message is displayed advising the user to check their connection
```

---

### US-10 — View Game Detail

**As a** registered user,  
**I want to** view the full details of a game in my collection,  
**so that** I can review all the information I have stored about it.

```gherkin
Feature: View Game Detail

  Background:
    Given the user is authenticated
    And the user has at least one game in their collection

  Scenario: Open game detail from the collection list
    When the user taps on a game in the collection list
    Then the game detail screen is displayed
    And the following information is shown when available:
      | Field             |
      | Name              |
      | Platform          |
      | Score             |
      | PEGI rating       |
      | Distributor       |
      | Developer         |
      | Number of players |
      | Release date      |
      | GOTY flag         |
      | Format            |
      | Genre             |
      | State             |
      | Purchase date     |
      | Purchase location |
      | Price             |
      | Cover image       |
      | Video/trailer URL |
      | Loaned to         |
      | Observations      |
      | Saga              |
      | Songs             |
```

---

### US-11 — Add New Game Manually

**As a** registered user,  
**I want to** manually add a new game to my collection,  
**so that** I can register games that may not appear in the external catalog.

```gherkin
Feature: Add New Game Manually

  Background:
    Given the user is authenticated and on the games screen

  Scenario: Successfully add a new game with minimal information
    When the user taps the "Add Game" button
    And the user enters at least the game name
    And the user taps "Save"
    Then the game is added to the collection
    And a success message is shown
    And the new game appears in the collection list

  Scenario: Add a game with full details
    When the user taps the "Add Game" button
    And the user fills in all available fields including platform, score, genre, format, state, purchase info, and observations
    And the user taps "Save"
    Then the game is saved with all provided details
    And the game appears in the collection list

  Scenario: Navigate back without saving
    When the user taps the "Add Game" button
    And the user starts entering data but then navigates back
    Then no game is added to the collection
```

---

### US-12 — Edit Game

**As a** registered user,  
**I want to** edit the details of an existing game in my collection,  
**so that** I can keep the information up to date.

```gherkin
Feature: Edit Game

  Background:
    Given the user is authenticated
    And the user is on the detail screen of a game in their collection

  Scenario: Successfully edit a game's details
    When the user modifies any available field
    And the user taps "Save"
    Then the changes are persisted
    And the updated information is reflected on the game detail screen

  Scenario: Edit the game's progress state
    When the user changes the state field from "Pending" to "In Progress"
    And taps "Save"
    Then the game state is updated to "In Progress"
    And the game appears in the correct state tab on the collection screen

  Scenario: Edit the score of a game
    When the user changes the score value
    And taps "Save"
    Then the new score is saved and displayed
```

---

### US-13 — Delete Game

**As a** registered user,  
**I want to** delete a game from my collection,  
**so that** I can remove games I no longer want to track.

```gherkin
Feature: Delete Game

  Background:
    Given the user is authenticated
    And the user has at least one game in their collection

  Scenario: Delete a game from the collection list via swipe gesture
    When the user swipes a game item in the collection list
    Then a delete action is triggered
    And the game is removed from the local database
    And the game disappears from the collection list

  Scenario: Delete a game from the game detail screen
    Given the user is on the detail screen of a game
    When the user taps the "Delete" option
    Then a confirmation is requested
    And upon confirmation the game is deleted
    And the user is navigated back to the collection list
    And the game no longer appears in the collection

  Scenario: Deletion of a game also removes its saga association
    Given the game belongs to a saga
    When the game is deleted
    Then the saga no longer contains that game
```

---

### US-14 — Mark Purchase Location on Map

**As a** registered user,  
**I want to** mark the physical location where I bought a game on a map,  
**so that** I can remember where I purchased it.

```gherkin
Feature: Mark Purchase Location on Map

  Background:
    Given the user is on the game detail/edit screen

  Scenario: Select a purchase location on the map
    When the user taps the "Purchase Location" map field
    Then a map view is displayed
    When the user taps a location on the map
    Then the selected location coordinates are saved as the purchase location for the game

  Scenario: View an already stored purchase location
    Given the game has a saved purchase location
    When the user opens the game detail screen
    Then the map shows a marker at the stored purchase location
```

---

### US-15 — View Game Trailer

**As a** registered user,  
**I want to** watch a video or trailer associated with a game,  
**so that** I can quickly recall what the game looks like.

```gherkin
Feature: View Game Trailer

  Background:
    Given the user is on the game detail screen
    And the game has an associated video URL

  Scenario: Play a game trailer
    When the user taps the video/trailer section
    Then the video is played inside the app using the stored URL

  Scenario: No video available
    Given the game does not have an associated video URL
    Then the video/trailer section is hidden or shows a placeholder
```

---

### US-16 — Manage Game Songs

**As a** registered user,  
**I want to** add, view, and delete songs associated with a game,  
**so that** I can store information about its soundtrack or iconic music.

```gherkin
Feature: Manage Game Songs

  Background:
    Given the user is authenticated
    And the user is on the songs tab of a game detail screen

  Scenario: View list of songs for a game
    Given the game has at least one associated song
    Then the list of songs is displayed with name, singer, and URL for each

  Scenario: Add a new song to a game
    When the user taps the "Add Song" button
    And the user fills in the song name
    And optionally fills in the singer and URL fields
    And the user taps "Save"
    Then the song is added to the game's song list
    And it appears in the songs tab

  Scenario: Delete a song from a game
    Given the game has at least one song
    When the user performs a delete action on a song
    Then the song is removed from the game's song list
    And it no longer appears in the songs tab

  Scenario: Empty song list
    Given the game has no associated songs
    Then an empty state message is displayed on the songs tab
```

---

### US-17 — View Saga Collection

**As a** registered user,  
**I want to** see a list of all my sagas,  
**so that** I can easily navigate and manage my game series.

```gherkin
Feature: View Saga Collection

  Background:
    Given the user is authenticated and on the sagas screen

  Scenario: Sagas list is displayed
    Given the user has at least one saga
    Then the saga list is displayed with each saga showing its name and associated games

  Scenario: Empty sagas state
    Given the user has no sagas
    Then an empty state message is displayed
    And a call-to-action is shown to create the first saga
```

---

### US-18 — Create Saga

**As a** registered user,  
**I want to** create a new saga and associate games to it,  
**so that** I can group related games from the same series.

```gherkin
Feature: Create Saga

  Background:
    Given the user is authenticated and on the sagas screen

  Scenario: Successfully create a saga with at least one game
    When the user taps the "Add Saga" button
    And the user enters a name for the saga
    And the user selects one or more games from their collection
    And the user taps "Save"
    Then the saga is created
    And the associated games are updated with a reference to the new saga
    And the saga appears in the sagas list
    And a success message is shown

  Scenario: Attempt to create a saga without a name
    When the user taps the "Add Saga" button
    And the user leaves the name field empty
    And the user taps "Save"
    Then an error message indicates the name is required
    And the saga is not created
```

---

### US-19 — Edit Saga

**As a** registered user,  
**I want to** edit an existing saga's name or game list,  
**so that** I can keep its information accurate.

```gherkin
Feature: Edit Saga

  Background:
    Given the user is authenticated
    And the user is on the detail screen of an existing saga

  Scenario: Successfully rename a saga
    When the user changes the saga name
    And taps "Save"
    Then the saga name is updated in the list and in all associated games

  Scenario: Add a new game to an existing saga
    When the user selects an additional game to associate with the saga
    And taps "Save"
    Then the saga is updated with the new game
    And the game's saga field is updated accordingly

  Scenario: Remove a game from a saga
    When the user deselects a game that was previously associated with the saga
    And taps "Save"
    Then the saga no longer includes that game
    And the game's saga reference is cleared
```

---

### US-20 — Delete Saga

**As a** registered user,  
**I want to** delete a saga,  
**so that** I can remove series groupings I no longer need.

```gherkin
Feature: Delete Saga

  Background:
    Given the user is authenticated
    And the user is on the detail screen of an existing saga

  Scenario: Successfully delete a saga
    When the user taps the "Delete" option
    Then a confirmation is requested
    And upon confirmation the saga is deleted
    And the saga no longer appears in the sagas list
    And all previously associated games have their saga field cleared

  Scenario: Cancel saga deletion
    When the user taps the "Delete" option
    And then cancels the confirmation dialog
    Then the saga is not deleted
    And the user remains on the saga detail screen
```

---

### US-21 — Change Password

**As a** registered user,  
**I want to** change my account password,  
**so that** I can keep my account secure.

```gherkin
Feature: Change Password

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Successfully change the password
    When the user enters a new valid password (more than 5 characters)
    And the user taps "Save"
    Then the password is updated in the backend
    And the new credentials are stored locally
    And the user is notified of the successful change

  Scenario: Attempt to save an invalid new password
    When the user enters a new password of 5 characters or fewer
    Then an error message is displayed indicating the password is too short
    And the "Save" button remains disabled or the save is prevented
```

---

### US-22 — Change App Language

**As a** registered user,  
**I want to** change the application language between English and Spanish,  
**so that** I can use the app in my preferred language.

```gherkin
Feature: Change App Language

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Change language to English
    When the user selects "English" from the language options
    And taps "Save"
    Then the app restarts the session with the English language applied throughout the interface

  Scenario: Change language to Spanish
    When the user selects "Spanish" from the language options
    And taps "Save"
    Then the app restarts the session with the Spanish language applied throughout the interface
```

---

### US-23 — Configure Default Sort

**As a** registered user,  
**I want to** configure the default sort field and order for my collection,  
**so that** the game list is always presented in the way I prefer.

```gherkin
Feature: Configure Default Sort

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Set default sort to "Name" ascending
    When the user selects "Name" as the default sort parameter
    And selects "Ascending" as the sort order
    And taps "Save"
    Then every time the user navigates to the game list, games are sorted by name A–Z

  Scenario: Set default sort to "Score" descending
    When the user selects "Score" as the default sort parameter
    And selects "Descending" as the sort order
    And taps "Save"
    Then every time the user navigates to the game list, games are sorted by score from highest to lowest
```

---

### US-24 — Configure Swipe-to-Refresh

**As a** registered user,  
**I want to** enable or disable the pull-to-refresh gesture on the game list,  
**so that** I can control whether I want to refresh the list manually.

```gherkin
Feature: Configure Swipe-to-Refresh

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Enable swipe-to-refresh
    When the user enables the "Swipe to Refresh" toggle
    And taps "Save"
    Then a pull-down gesture on the game list triggers a refresh

  Scenario: Disable swipe-to-refresh
    When the user disables the "Swipe to Refresh" toggle
    And taps "Save"
    Then a pull-down gesture on the game list performs no refresh action
```

---

### US-25 — Change App Theme

**As a** registered user,  
**I want to** switch between light, dark, and system-default themes,  
**so that** I can use the app comfortably in different lighting environments.

```gherkin
Feature: Change App Theme

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Switch to dark mode
    When the user selects "Dark" theme
    And taps "Save"
    Then the app applies a dark colour palette immediately

  Scenario: Switch to light mode
    When the user selects "Light" theme
    And taps "Save"
    Then the app applies a light colour palette immediately

  Scenario: Follow system theme
    When the user selects "System Default" theme
    And taps "Save"
    Then the app follows the operating system's current theme setting
```

---

### US-26 — Export Collection Data

**As a** registered user,  
**I want to** export my entire game collection and sagas as a JSON file,  
**so that** I can create a backup or transfer my data to another device.

```gherkin
Feature: Export Collection Data

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Successfully export data
    When the user taps the "Export Data" option
    Then the app generates a JSON file containing all games and sagas
    And the file is shared via the Android share sheet so the user can save it

  Scenario: Export with an empty collection
    Given the user has no games or sagas
    When the user taps the "Export Data" option
    Then an empty but valid JSON structure is shared
```

---

### US-27 — Import Collection Data

**As a** registered user,  
**I want to** import a previously exported JSON file,  
**so that** I can restore my collection or migrate data from another device.

```gherkin
Feature: Import Collection Data

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Successfully import a valid JSON file
    When the user taps the "Import Data" option
    And the user selects a valid exported JSON file
    Then all games and sagas from the file are inserted into the local database
    And the collection is updated with the imported items

  Scenario: Import fails with an invalid or malformed JSON file
    When the user taps the "Import Data" option
    And the user selects a file that is not a valid export
    Then an error message is displayed
    And no data is modified in the local database
```

---

### US-28 — Delete Account

**As a** registered user,  
**I want to** permanently delete my account and all local data,  
**so that** I can completely remove my information from the app.

```gherkin
Feature: Delete Account

  Background:
    Given the user is authenticated and on the settings screen

  Scenario: Successfully delete the account
    When the user taps the "Delete Account" option
    Then a confirmation is requested
    And upon confirmation all user credentials are removed
    And all games and sagas are deleted from the local database
    And the user is redirected to the login screen

  Scenario: Cancel account deletion
    When the user taps the "Delete Account" option
    And then cancels the confirmation
    Then no data is deleted
    And the user remains on the settings screen
```

---

### US-29 — In-App Update

**As a** registered user,  
**I want to** be informed when a new version of the app is available,  
**so that** I can update the app without leaving it.

```gherkin
Feature: In-App Update

  Background:
    Given the user is authenticated and on any screen of the app

  Scenario: A flexible (optional) update is available
    Given a new non-mandatory update is available on the Play Store
    When the app starts or resumes
    Then the app notifies the user that an update is available
    And the user can choose to download and install it in the background
    And when the download is complete the user is prompted to apply the update

  Scenario: A mandatory (immediate) update is available
    Given a mandatory critical update is available on the Play Store
    When the app starts or resumes
    Then the app blocks the user interface
    And forces the update flow to start immediately
    And the update is installed before the user can continue

  Scenario: No update is available
    Given the app is already at the latest version
    When the app starts
    Then no update notification is shown
    And the user continues to the main screen normally

  Scenario: Update check fails
    Given there is no network connection when the app starts
    When the app performs the update check
    Then the update check is silently skipped
    And the user proceeds to the main screen normally
```

---

## 5. Non-Functional Requirements

| ID | Category | Requirement |
|----|----------|-------------|
| NFR-01 | **Security** | User passwords must be stored encrypted in local preferences. Authentication tokens must be stored in encrypted shared preferences. |
| NFR-02 | **Security** | Passwords must be at least 6 characters long. |
| NFR-03 | **Performance** | The game list must support paginated loading to avoid performance degradation with large collections. |
| NFR-04 | **Usability** | The application must support both English and Spanish languages. |
| NFR-05 | **Usability** | The application must support Light, Dark, and System-default themes. |
| NFR-06 | **Reliability** | All collection data must be stored in a local Room database so the app works fully offline after initial setup. |
| NFR-07 | **Maintainability** | The app must follow the MVVM architecture pattern with clear separation between presentation, domain, and data layers. |
| NFR-08 | **Portability** | The application must be compatible with Android devices running the minimum SDK version defined in the project's build configuration. |
| NFR-09 | **Data Integrity** | Deleting a game must cascade-update any related saga by removing the game reference from that saga. |
| NFR-10 | **Connectivity** | External game search (RAWG API) requires an internet connection. All other features must remain functional offline. |
| NFR-11 | **Updates** | The app must support both flexible and immediate (mandatory) in-app update flows via the Google Play In-App Update API. |
