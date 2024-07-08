# WeatherWeb
A Spring Boot-based weather forecasting application that uses the OpenWeatherMap API to provide real-time weather data and the Spotify API to suggest songs based on the current weather conditions.

# Weather Forecasting and Music Recommendation Application

## Overview
This project is a weather forecasting application built with Spring Boot. It integrates the OpenWeatherMap API to fetch real-time weather data and the Spotify API to suggest songs based on the current weather conditions. The application aims to enhance the user experience by providing not only weather information but also a personalized music recommendation to match the weather.

## Features
- **Real-time Weather Forecasting**: 
  - Get up-to-date weather information including temperature, humidity, wind speed, and detailed weather conditions for your location.
  - Supports multiple locations and provides accurate and timely weather updates.
- **Music Recommendation Based on Weather**:
  - Receives weather data and suggests songs from Spotify that match the current weather conditions.
  - Enhances the user's mood and experience by providing contextually appropriate music.
- **Spring Boot Framework**: 
  - Utilizes the powerful and robust Spring Boot framework to ensure a scalable and maintainable application.
  - Follows best practices for Spring Boot application development.
- **API Integration**:
  - Seamlessly integrates with OpenWeatherMap API for real-time weather data.
  - Uses Spotify API to fetch song recommendations based on weather data.
- **User-friendly Interface**:
  - Easy-to-use interface that allows users to quickly get weather updates and music recommendations.
  - Provides a smooth and intuitive user experience.

## Technologies Used
- **Spring Boot**: For building the backend of the application.
- **OpenWeatherMap API**: To fetch real-time weather data.
- **Spotify API**: To suggest songs based on the current weather conditions.
- **Java**: The primary programming language used.
- **Maven**: For project management and dependency management.
- **Thymeleaf**: For server-side rendering of the user interface.
- **RESTful Web Services**: For API communication.

## Installation and Setup
1. **Clone the repository**:
   git clone https://github.com/ITS-PAWAN-SRIVASTAVA/weather-forecasting-music-recommendation.git
2. Navigate to the project directory:
     cd weather-forecasting-music-recommendation

   
Configure API keys:
Obtain an API key from OpenWeatherMap. = openweathermap.api.key=YOUR_OPENWEATHERMAP_API_KEY

Obtain an API key from Spotify. = spotify.client.id=YOUR_SPOTIFY_CLIENT_ID
spotify.client.secret=YOUR_SPOTIFY_CLIENT_SECRET

Update the application.properties file with your API keys:

Build and run the application:
mvn clean install
mvn spring-boot:run


Usage
Access the application:

Open your web browser and navigate to http://localhost:8080.
Get weather information and song recommendations:

Enter your location in the provided input field.
Click on the "Get Weather and Music" button.
View the current weather information and enjoy the recommended songs based on the weather.
Contributing
We welcome contributions to improve the project. If you find any issues or have suggestions for enhancements, feel free to submit an issue or create a pull request.

License
This project is licensed under the MIT License. See the LICENSE file for details.

Acknowledgements
OpenWeatherMap for providing the weather data API.
Spotify for providing the music recommendation API.

