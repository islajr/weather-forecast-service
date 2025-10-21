const currentWeatherButton = document.getElementById("current-weather-button");
const forecastButton = document.getElementById("forecast-button");
const displaySection = document.getElementById("display-section");
const fetchButton = document.getElementById("fetch-results");
const inputField = document.querySelector(".input-field");

let action = "";

/* necessary methods */
async function getData(action, city) {
    let response = ""
    try {
        action === "currentWeather" ?
            response = await fetch(`http://localhost:8080/api/weather/current?city=${city}`) :
            response = await fetch(`http://localhost:8080/api/weather/forecast?city=${city}`);

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();

        console.log("Fetched data: ", data);
    } catch (error) {
        console.error("Failed to fetch data: ", error);
    }
}

currentWeatherButton.addEventListener("click", (e) => {
    e.preventDefault();
    action = "currentWeather";
    console.log(action);
    displaySection.style.display = "flex";
})

forecastButton.addEventListener("click", (e) => {
    e.preventDefault();
    action = "forecast";
    console.log(action);
    displaySection.style.display = "flex";
})

fetchButton.addEventListener("click", (e) => {
    e.preventDefault();
    getData(action, inputField.value);
})

