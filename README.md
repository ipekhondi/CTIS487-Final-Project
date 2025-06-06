# FinalProject - Android Notes Application - MediDoor

This is an Android application developed as a final project for the CTIS487 course. The app is structured using Kotlin and follows modern Android development practices. It likely features components such as activities, background workers, and animations.

## Features

- Splash screen and main activity
- Note-taking functionality
- Background UI change with `WorkManager`
- Organized project structure with Gradle Kotlin DSL
- Firebase integration (`google-services.json` present)
- UI animations (`fade_in`, `slide_in`)
- Model classes for `User`, `Note`, etc.

## Technologies Used

- Kotlin
- Android Jetpack Libraries
- Firebase (authentication or storage assumed)
- WorkManager
- Gradle Kotlin DSL
- Material Design

## Getting Started

***Important: In app/src/google-services.json, on line 18, for security reasons, please replace the API key with your own Firebase API key before running the project.***

1. **Clone the repository**  
   ```bash
   git clone <your-repo-url>
   cd FinalProject_XML

2. **Open the Project in Android Studio**
Open Android Studio.
Select "Open an existing project".
Choose the folder you cloned.

3. **Configure Firebase**
Ensure the google-services.json file located in the app/ directory belongs to your own Firebase project.
In the Firebase Console, enable the necessary services (e.g., Firestore, Authentication).

4. **Build the Project**
Wait for Gradle synchronization to complete.
If needed, use File > Sync Project with Gradle Files.

5. **Run the App**
Select an emulator or a physical device.
Use Run > Run 'app' to start the application.


