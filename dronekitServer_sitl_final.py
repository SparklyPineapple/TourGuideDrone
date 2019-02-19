#Server and DroneKit test on the sitl simulator for final app design
#Made up from sampleCommSocketServer, simTest, and dkt files
#Updated 16 Feb 2019

import socket
from dronekit import connect, VehicleMode
import dronekit_sitl
import time

sitl = dronekit_sitl.start_default(lat=39.749003, lon=83.814051)
connection_string = sitl.connection_string()

def sendMessageReceiveAck(message, connection):
    #SEND AND RECEIVE ACK FROM APP 
    # send message    
    byteString = (message + "\n").encode()
    connection.send(byteString)
    #print("message sent to client") 
    #receive ack 
    #print("ready to receive ack \n")
    try:
        byteInput = c.recv(24)
        piInput = byteInput.decode()
        #make so throws error if true not sent-----------------------------
        if piInput == "true":
            #print("ack received \n")
        else:
            print("ERROR: no ack received \n")
    except socket.error:
        print("no ack received \n")

def readMessageSendAck(connection):
    # #RECEIVE + ACK A MESSAGE FROM APP
    #receive message 
    #print("ready to receive message \n")
    try:
        byteInput = connection.recv(1024)
        piInput = byteInput.decode()
        if piInput != "":
            #print("this is the message read: " + piInput + "\n")
    except socket.error:
        print("no data to receive") 
    # send ack    
    newString = "true" + "\n"
    byteString = newString.encode()
    connection.send(byteString)
    #print("ack sent to client \n")
      
    return piInput

def update():
    dLat = drone.location.global_frame.lat #global latitude of drone
    dLon = drone.location.global_frame.lon #global longitude of drone
    dAlt = drone.location.global_frame.alt # global altitude from sea level of drone
    dVel = drone.groundspeed #groundspeed in m/s
    dHead = drone.heading #heading in degrees

    sendMessageReceiveAck(dLat, c) #drone lat
    sendMessageReceiveAck(dLon, c) #drone lon
    sendMessageReceiveAck(dAlt, c) #drone alt
    sendMessageReceiveAck(dVel, c) #drone velocity
    sendMessageReceiveAck(dHead, c) #drone heading



#waypoint array
#NOTE xx.xxx1 = 1.2 meters
#wp[0] = North (Lat), wp[1] = West (Lon)
#wp[x][y] y = 0 (SSC), 9 (DMC), 11 (BTS), 22 (ENS), 28 (HSC)
wp = [[39.749003, 39.749154, 39.749309, 39.749314, 39.749416, 39.749493, 39.749620, 39.749686, 39.749795, 39.750000, 39.749611, 39.749438, 39.748833, 39.748773, 39.748650, 39.748492, 39.748390, 39.748224, 39.748059, 39.747869, 39.747849, 39.747935, 39.748071, 39.748156, 39.748258, 39.748374, 39.748414, 39.748574, 39.748717], 
[83.814051, 83.813784, 83.813581, 83.813453, 83.813136, 83.812932, 83.812717, 83.812469, 83.812094, 83.811544, 83.811133, 83.811208, 83.811616, 83.811811, 83.811991, 83.812132, 83.812246, 83.812311, 83.812329, 83.812442, 83.812663, 83.813061, 83.813355, 83.813576, 83.813864, 83.814104, 83.814275, 83.814216, 83.814137]]

#connect to drone
print'\n'
print "Beginning connection..."
drone = connect(connection_string, wait_ready=True)

SSC = LocationGlobal(39.749003, 83.814051, 320)
drone.home_location = SSC

print '\n' 
print "Connection complete."

print '\n'
print "Getting parameters:"
print drone.location.global_frame
print ("Ground Speed: %s" % drone.groundspeed)
print drone.attitude
print ("Heading: %s" % drone.heading)
print ("Armable: %s" %drone.is_armable)
print ("System Status: %s" % drone.system_status.state)
print ("Mode: %s" % drone.mode.name) #settable
print ("Armed: %s" % drone.armed) #settable

dLat = drone.location.global_frame.lat #global latitude of drone
dLon = drone.location.global_frame.lon #global longitude of drone
dAlt = drone.location.global_frame.alt # global altitude from sea level of drone
dVel = drone.groundspeed #groundspeed in m/s
dHead = drone.heading #heading in degrees

print '\n'
print "Done... Starting Drone-Phone Connection."
print '\n'

#Start server
s = socket.socket()
host = '192.168.4.1' #ip of desktop
port = 8000
s.bind((host, port))

hostnam = socket.gethostname()    
IPAddr = socket.gethostbyname(hostnam)    
print("Your Computer Name is:" + hostnam)    
print("Your Computer IP Address is:" + IPAddr) 
receivedMessage = ""
s.listen(5)

c, addr = s.accept()
print ('Got connection from',addr)

#send all comm values to app
#Done automatically in function 'update()'
sendMessageReceiveAck(dLat, c) #drone lat
sendMessageReceiveAck(dLon, c) #drone lon
sendMessageReceiveAck(dAlt, c) #drone alt
sendMessageReceiveAck(dVel, c) #drone velocity
sendMessageReceiveAck(dHead, c) #drone heading

#receive all comm values from phone
#Done automatically in function 'update()'
destWPNum = readMessageSendAck(c) #destination way point
pLat = readMessageSendAck(c) #phone lat
pLon = readMessageSendAck(c) #phone lon
start = readMessageSendAck(c) #start button pressed, bool
stop = readMessageSendAck(c) #stop button pressed, bool
eStop = readMessageSendAck(c) #emergency stop button pressed, bool
ex = readMessageSendAck(c) #IMPLEMENT EXIT BUTTON

while not exit:
    #What if I fall?
    #But my dear, what if you fly?

    start = readMessageSendAck(c)
    ex = readMessageSendAck(c)
    if ex:
        start = False
        break

    if start:

        destWPNum = readMessageSendAck(c) #destination way point
        pLat = readMessageSendAck(c) #phone lat
        pLon = readMessageSendAck(c) #phone lon

        #Takeoff
        while not drone.is_armable:
        print "Prepping..."
        time.sleep(1)

        print "\n"
        print "Drone is armable. \n"    


        while drone.mode.name != "GUIDED":
            drone.mode = VehicleMode("GUIDED")
            print "Changing mode: %s" % drone.mode.name
            time.sleep(1)
        mode = drone.mode.name
        print ("Mode changed to %s. \n" % mode)

        drone.armed = True

        while not drone.armed:
            print "Arming...\n"
            time.sleep(2)

        print ("Drone armed: %s \n" % drone.armed)

        #Check and see if mission has been aborted or not
        stop = readMessageSendAck(c)
        if stop:
            start = False
            #sendMessageReceiveAck("Mission Aborted", c)
            print "Mission Aborted\n"
            break
        #Else, continue flight
        #Takeoff
        drone.simple_takeoff(12)
        print ("Takeoff Alt: %s" % dAlt)
        while (drone.location.global_frame.alt) < (dAlt + 12):
            print drone.location.global_frame.alt
            sendMessageReceiveAck(dAlt, c)
            time.sleep(2)

        #send phone "adv" to show I made it through the loop - not implemented in python but is implemented on app
		print "\n"
        dest = LocationGlobal(wp[0][destWPNum], wp[1][destWPNum], 333)

        
        print "Beginning Flight... \n"

        waypoint = 0

        while waypoint < destWPNum:
            print "\n"
            print "waypoint: %s" % waypoint
            print "\n"
            dest = LocationGlobal(wp[0][waypoint], wp[1][waypoint], 333)
            drone.simple_goto(dest, None, 3)
            while round(drone.location.global_frame.lat, 4) != round(dest.lat, 4) and round(drone.location.global_frame.lon, 4) != round(dest.lon, 4):
                update()
                print drone.location.global_frame
                print drone.groundspeed
            waypoint = waypoint + 1
        
        #Land
        while drone.mode.name != "LAND":
            drone.mode = VehicleMode("LAND")
            print "Changing mode: %s" % drone.mode.name
            time.sleep(1)
        mode = drone.mode.name
        print ("Mode changed to %s. \n" % mode)

        while drone.location.global_frame.alt > dAlt+1:
            update()
            print drone.location.global_frame
            time.sleep(10)

        #End FLight
        print "Flight Completed"
        sendMessageReceiveAck('done', c)
        start = False


print "Goodbye...\n"
  
c.close() 
drone.close()

