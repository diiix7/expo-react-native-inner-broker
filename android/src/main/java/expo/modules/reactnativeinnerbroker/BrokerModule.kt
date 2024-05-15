package expo.modules.reactnativeinnerbroker

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import mqtt.broker.Broker
import mqtt.broker.interfaces.PacketInterceptor
import mqtt.packets.MQTTPacket
import mqtt.packets.mqtt.MQTTConnect
import mqtt.packets.mqtt.MQTTPublish
import android.os.StrictMode
import java.net.ServerSocket
import java.net.BindException

class BrokerModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("Broker")
    Events("onChange")
    Function("hello") {
      "Hello world! 👋"
    }
    AsyncFunction("setValueAsync") { value: String ->
      // Send an event to JavaScript.
      sendEvent("onChange", mapOf(
        "value" to value
      ))
    }
    Function("startBroker") {
      startBroker()
    }

    fun startBroker () : Boolean {
      val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
      StrictMode.setThreadPolicy(policy)
      try {
        Thread {
          print("broker service starting")
  
          if(isPortAvailable(1883)){
            val broker = Broker(
              port = 1883,
              webSocketPort = 8083,
              packetInterceptor = object : PacketInterceptor {
                override fun packetReceived(clientId: String, username: String?, password: UByteArray?, packet: MQTTPacket) {
                  when (packet) {
                    is MQTTConnect -> println(packet.clientID)
                    is MQTTPublish -> println(packet.topicName)
                  }
                }
              }
            )
            broker.listen()
          }
          true
        }.start()
      } catch (e:Exception) {
        print(e)
        false
      }
  
      return true
    }
    fun isPortAvailable(port: Int): Boolean {
      return try {
          ServerSocket(port).use { }
          true
      } catch (e: BindException) {
          false
      }  
    }
  }
}
