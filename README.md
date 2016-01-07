# Slack Messenger
Eliminates the bloat of having a browser open to send messages in your slack team!


# How to use:
Add an incoming webhook to your team.  
Copy the webhook URL  
Open the program, click "Add URL". Paste the copied webhook URL.  
Fill in the other fields (Only channel needs to be filled in, icon and nick will generate values)  
Type a message, and click send.  
Optionally, you may click "Save" when you have filled out the fields. This will save a config file in your home directory (~/.slackmsg/config.txt)  
If you save to the config, everytime you open the program again, the saved fields will auto fill.  

# Config guide  
Four strings are checked when reading the config:  
"url=" - A webhook URL to be used in the program  
"nick=" - Your nickname that will be displayed on the website. Defaults to "CompBotNNNN" where NNNN is a random number.  
"icon=" - The icon that your post will have. Defaults to :skull_and_crossbones: if none is provided in the program.  
"channel=" - The channel that the message will be sent to.  
These four must start the line to be used.  
Multiple "url=" lines may be added to allow easily sending to multiple teams.  