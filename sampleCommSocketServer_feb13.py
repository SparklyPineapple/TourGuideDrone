#Made by Ailin Leong with key components taken from online and Kirby Darst
#Edited 2/13  by Kirby for testing with app and use for template for which drone server will work
#verified. needs encode() and decode() in order to work with app

import socket

def sendMessageReceiveAck(message, connection):
    #SEND AND RECEIVE ACK FROM APP 
    # send message    
    byteString = (message + "\n").encode()
    connection.send(byteString)
    print("message sent to client") 
    #receive ack 
    print("ready to receive ack \n")
    try:
        byteInput = c.recv(24)
        piInput = byteInput.decode()
        #make so throws error if true not sent-----------------------------
        if piInput == "true":
            print("ack received \n")
        else:
            print("ERROR: no ack received \n")
    except socket.error:
        print("no ack received \n")

def readMessageSendAck(connection):
    # #RECEIVE + ACK A MESSAGE FROM APP
    #receive message 
    print("ready to receive message \n")
    try:
        byteInput = connection.recv(1024)
        piInput = byteInput.decode()
        if piInput != "":
            print("this is the message read: " + piInput + "\n")
    except socket.error:
        print("no data to receive") 
    # send ack    
    newString = "true" + "\n"
    byteString = newString.encode()
    connection.send(byteString)
    print("ack sent to client \n")
      
    return piInput




s = socket.socket()
host = '' #ip of desktop
port = 8080
s.bind((host, port))

hostnam = socket.gethostname()   
IPAddr = socket.gethostbyname(hostnam)   
print("Your Computer Name is:" + hostnam)   
print("Your Computer IP Address is:" + IPAddr)
receivedMessage = ""
print('start listening for client');
s.listen(5)
print ('after listen(5)')

#accept client
c, addr = s.accept()
print ('client connected')



#receive or send messages as desired using functions above
#in offcial server would want a loop for send/receive 
    #unless drone arrives or app says to land or emergency land
    
#send all comm values to app
sendMessageReceiveAck("1", c) #drone lat
sendMessageReceiveAck("2", c) #drone lon
sendMessageReceiveAck("3", c) #drone alt
sendMessageReceiveAck("4", c) #drone velocity
sendMessageReceiveAck("5", c) #drone heading
 
#receive all comm values from phone
readMessageSendAck(c) #destination way point
readMessageSendAck(c) #phone lat
readMessageSendAck(c) #phone lon
readMessageSendAck(c) #start button pressed
readMessageSendAck(c) #stop button pressed
readMessageSendAck(c) #emergency stop button pressed

print("close socket")
c.close()

