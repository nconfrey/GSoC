###Nick Confrey's Modification to the Core Processing Network Library

When editing the networking library, the main goal was to make it possible for the server to address specific clients, rather than broadcast to all connected clients at once. Previously, the server had to call `available()` which returned the next client in the list, but the server had no control over which client was being given to it. Furthermore, it was impossible to create games or applications that needed to query a specific user for information, because the server could only send messages to everyone. I have attempted to fix these problems through simple tweaks.

##Change log

`Client getClient(int index)` - This new method returns the specific client at a given index. Since it returns a client object, the server can then send directly to this client, or listen specifically from this client. It also allows looping through all the clients as an alternative to calling `available()` over and over again.

`Client[] getClients()` - Returns the full array of all connected clients.
`int numClients()` - Returns the number of connected clients. Important for looping through the client list, as well as generally how many computers are connected, or players in a game, for example.

`protected void checkClients()` - This internal method is used for error checking. It iterates over the clientlist and makes sure there are no dead clients, and if there are, removes them and updates the clientlist. This is important to call before handing out the clientlist to the user, to avoid null errors.

`protected void disconnectEvent(Client client)` - This fires an event in a similar fashion to `serverEvent` and fires when a client disconnects. Previously there was no indication to the server when a client disconnected, except for some error output on the console. Now the user can decide to do something when a client disconnects, like refresh their user list or inform other users who disconnected. I decided to make this a separate event from the existing `serverEvent` because I did not want to break old code and examples, by adding a perameter required to differentiate connection events from disconnection events. 