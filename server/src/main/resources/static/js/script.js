const currentWeatherButton = document.getElementById("current-weather-button");
const forecastButton = document.getElementById("forecast-button");
const displaySection = document.getElementById("display-section");
const fetchButton = document.getElementById("fetch-results");
const inputField = document.querySelector(".input-field");

let action = "";

/* necessary methods */
function getData(action, city) {

    try {
        action === "currentWeather" ?
            window.location.href = `/weather?city=${city}` :
            window.location.href = `/forecast?city=${city}`

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }


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

