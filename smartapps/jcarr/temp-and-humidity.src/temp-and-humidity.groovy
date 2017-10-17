/**
 *  Temp and Humidity
 *
 *  Copyright 2015 Jason Carr
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

definition(
    name: "Temp and Humidity",
    namespace: "jcarr",
    author: "Jason Carr",
    description: "Sends temperature and humidity to a remote server",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("Select Devices") {
		input "temperatureSensors","capability.temperatureMeasurement", title: "Temperature Sensors", required: false, multiple: true
		input "humiditySensors","capability.relativeHumidityMeasurement", title: "Humidity Sensors", required: false, multiple: true
	}

	section("URL Endpoint") {
		input "url", "text", title: "Input the URL endpoint (http://someurl.com:31337)"
	}
}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe(temperatureSensors, "temperature", handleTemperatureEvent)
    subscribe(humiditySensors, "humidity", handleHumidityEvent)

/*
	def allsensors = (temperatureSensors << humiditySensors).flatten()
*/
	def sensormap = []

	temperatureSensors.each { sensor ->
		sensormap << [sensor.id, sensor.name, sensor.displayName,"temp"]
	}

	humiditySensors.each { sensor ->
		sensormap << [sensor.id, sensor.name, sensor.displayName,"humi"]
	}

	def params = [
		uri: settings.url,
		path: "/config",
		body: sensormap
	]

	httpPostJson(params)
}

def handleTemperatureEvent(evt) {
	def stuff = URLEncoder.encode(evt.displayName, "UTF-8").replaceAll(/ /,'%20')
	log.debug "temp event $evt - $stuff"
    testThis("/update/${evt.deviceId}/${stuff}/temp/${evt.value}")
}

def handleHumidityEvent(evt) {
	def stuff = URLEncoder.encode(evt.displayName, "UTF-8").replaceAll(/ /,'%20')
	log.debug "humi event $evt - $stuff"
    testThis("/update/${evt.deviceId}/${stuff}/humi/${evt.value}")
}

private testThis(String path) {
	log.warn(path)
	def pollParams = [
		uri: settings.url,
		path: "${path}"
	]
	httpGet(pollParams)
}
