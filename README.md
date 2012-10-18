The goal of this project is to support the [JSR 356](http://jcp.org/en/jsr/detail?id=356) Java API for WebSocket using the [Atmosphere Framework](http://github.com/Atmosphere/atmosphere). 
The implementation will extends Atmosphere and will brings you Atmosphere's concepts like transport fallback (atmosphere.js), Broadcaster, Clustering, etc.

As simple as 
```java
@WebSocketEndpoint("/chat")
public class Jsr356WebSocketChat {

    private final ObjectMapper mapper = new ObjectMapper();

    @Broadcast("/chat")
    @WebSocketMessage
    public String onMessage(String message) throws IOException {
        return mapper.writeValueAsString(mapper.readValue(message, Data.class));
    }
}
```
Client side, as simple as:
```js
    // We are now ready to cut the request
    var request = { 
        url: document.location.toString() + 'chat',
        contentType : "application/json",
        // Uncomment to allow sharing websocket amongst  windows/tabs
        //shared : true,
        transport : 'websocket'
    };

    subSocket = socket.subscribe(request);

    request.onOpen = function(response) {
      ...
    };

    request.onMessage = function (response) {
       ...
    };

    request.onError = function(response) {
        ...
    };
```
