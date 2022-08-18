# TwitchBot
The creation of the Twitch Bot was my first major development experience with real-world ramifications. The program had to work with actual human users in real-time without crashing or stuttering. This chat client interacted with hundreds of users in real-time, tracking messages sent and saving data to an internal database. Development took 50+ hours over the span of two weeks. 



This bot was meant to raise chat engagement by letting users harmlessly bet points with no real-world value. A user earned points by sending messages. One message rewards one point. They could use a myriad of commands to manage their balance. Some of these commands allowed them to check their point balance, send points to each other, and bet their points based on a value, percentage, or text such as 'all'. Their point balance was persistent across multiple streams and instances of the chatbot running due to its internal database. 



Additionally, the chatbot had a hierarchy of users and admins with different permission levels. These levels permitted them to do advanced things such as starting and stopping the bot, as well as changing user points on the fly. 



Non-admin users had a cooldown enforced on their messages to avoid people spamming the chat with betting requests. The default cooldown was 10s however admin users were able to change this cooldown for each user at will. 



The bot also logged all actions performed with it. These include every time a bet was issued, every time a user was rigged, and the current leaderboard of each user's points. 



Last but not least, the chatbot was able to rig users' bets to ensure a specific outcome. This allowed admin users to control the 'luck' value of each user. This rigged data was saved to the internal database and was persistent across multiple instances and streams. 
