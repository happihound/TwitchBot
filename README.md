# Twitch Bot

This is a Java-based Twitch Bot that was created as a first major development experience with real-world ramifications. It is designed to interact with hundreds of users in real-time, track messages sent, and save data to an internal database. The development process took more than 50 hours over the span of two weeks.

The bot's primary purpose was to raise chat engagement by allowing users to bet points with no real-world value. Users earned points by sending messages, with one message earning one point. They could use various commands to manage their balance, including checking their point balance, sending points to other users, and betting points based on a value, percentage, or "all" text. The user's point balance was persistent across multiple streams and instances of the chatbot running due to its internal database.

The chatbot also included a hierarchy of users and admins with different permission levels. Admins had the ability to start and stop the bot, as well as changing user points on the fly. Non-admin users had a cooldown enforced on their messages to avoid spamming the chat with betting requests. The default cooldown was 10 seconds, but admin users could change this for each user at will.

The bot logged all actions performed with it, including every time a bet was issued, every time a user was rigged, and the current leaderboard of each user's points. Lastly, the chatbot was able to rig users' bets to ensure a specific outcome. This allowed admin users to control the "luck" value of each user, and the rigged data was saved to the internal database and was persistent across multiple instances and streams.

This project serves as an example of a Java-based Twitch bot that can handle real-time interactions with users and manage a persistent database of user data.
