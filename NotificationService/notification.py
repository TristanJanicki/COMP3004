from pyfcm import FCMNotification
API_KEY="AIzaSyC-HslrXcXMhXfLY64wnW5WstvygE_Tf5Y" #temp key for testing purposes

#A push notification
#Input: a string of a user id
#output: a notification recieved by the user
def pushNotif(user_RegId):
	push_service = FCMNotification(API_KEY)
	
	msgTit= "Quant.r"
	msgBod = "A result on your tradeName has occured"
	result= push_service.notify_single_device(registration_Id= user_RegID, message_title=msgTit, message_body=msgBody)

  #Multiple push notifications
#Input: a string of user id's
#output: a notification recieved by multiple users	
def pushMultNotif(users_RegId):
	push_service = FCMNotification(API_KEY)
	
	msgTit= "Quant.r"
	msgBod = "A result on your tradeName has occured" #this message should eventually output which trade to whatever user
	result= push_service.notify_multiple_device(registration_Id= users_RegID, message_title=msgTit, message_body=msgBody)
		
	
