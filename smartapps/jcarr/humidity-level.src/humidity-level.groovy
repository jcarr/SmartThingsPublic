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
    name: "Humidity Level",
    namespace: "jcarr",
    author: "Jason Carr",
    description: "yes",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
	section("Devices") {
		input "powerSwitch","capability.switch", title: "Switch", required: true, multiple: false, displayDuringSetup: true
		input "humiditySensors","capability.relativeHumidityMeasurement", title: "Humidity Sensor", required: true, multiple: false, displayDuringSetup: true
	}

  section("Values") {
    input "humidityTriggerOn","number",title:"Humidity Activate",description:"Humidity percent to activate dehumidifier.",range:"0..100",displayDuringSetup: true
    input "humidityTriggerOff","number",title:"Humidity Target",description:"Humidity percent to turn off dehumidifier.  Must be lower than above.",range:"0..100",displayDuringSetup: true
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
  subscribe(humiditySensors, "humidity", handleHumidityEvent)
}

def handleHumidityEvent(evt) {
  def switchVal = powerSwitch.currentSwitch
  def humidityVal = Double.parseDouble(evt.value)
  log.debug "humidity event ${humidityVal}"
  log.debug "current state of switch ${switchVal}"

  if ((humidityVal > humidityTriggerOn) && (switchVal != "on")) {
    log.debug "XXX turning switch on - ${evt.value} > ${humidityTriggerOn}"
    powerSwitch.on()
  }
  
  if ((humidityVal < humidityTriggerOff) && (switchVal != "off")) {
    log.debug "XXX turning switch off - ${evt.value} < ${humidityTriggerOff}"
    powerSwitch.off()
  }
}

