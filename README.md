
# HelloMich Android App

HelloMich is an Android application that facilitates user interactions and sessions, including features like user registration, login, profile management, and session tracking using Firebase.

## Features

- **User Registration**: New users can register with email, username, and password.
- **User Login**: Existing users can log in with their credentials.
- **Profile Management**: Users can update their username, profile picture, and password.
- **Session Management**: Users can start, view, and manage sessions.
- **Firebase Integration**: Authentication, Firestore for data storage, and Firebase Storage for profile pictures.
  
## Watch Beta Version

[Watch the beta version of helloMich on YouTube](https://www.youtube.com/shorts/wXkUECLkErI)


## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/hellomich.git
    cd hellomich
    ```

2. Open the project in Android Studio.

3. Sync the project with Gradle files.

4. Create a Firebase project and add the `google-services.json` file to the `app` directory.

5. Enable Email/Password authentication in Firebase.

6. Add Firestore and Storage in Firebase.

## Dependencies

Make sure to include the following dependencies in your `build.gradle` (app-level) file:

```gradle
dependencies {
    implementation 'com.google.firebase:firebase-auth:21.0.1'
    implementation 'com.google.firebase:firebase-firestore:24.0.1'
    implementation 'com.google.firebase:firebase-storage:20.0.1'
    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.1'
    implementation 'androidx.recyclerview:recyclerview:1.2.1'
    implementation 'com.google.android.material:material:1.4.0'
}
