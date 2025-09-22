import {LitElement} from 'lit';

export class DemoChat extends LitElement {

    connectedCallback() {
        const chatBot = document.getElementsByTagName("chat-bot")[0];
        let socket;
        let reconnectAttempts = 0;
        const maxReconnectAttempts = 5;
        const reconnectDelay = 2000; // 2 seconds

        const connectWebSocket = () => {
            socket = new WebSocket("ws://" + window.location.host + "/chatbot");

            socket.onopen = function(event) {
                console.log("[WebSocket] Connected to chatbot server");
                reconnectAttempts = 0; // Reset reconnect attempts on successful connection
            };

            socket.onmessage = function (event) {
                console.log("[WebSocket] Received message:", event.data);
                chatBot.hideAllLoading();
                chatBot.sendMessage(event.data, {
                    right: false,
                    sender: {name: 'Bob', id: '007'}
                });
            };

            socket.onerror = function(error) {
                console.error("[WebSocket] Error:", error);
            };

            socket.onclose = function(event) {
                console.log("[WebSocket] Connection closed:", event.code, event.reason);

                // Attempt to reconnect if not a normal closure
                if (event.code !== 1000 && reconnectAttempts < maxReconnectAttempts) {
                    reconnectAttempts++;
                    console.log(`[WebSocket] Attempting to reconnect... (${reconnectAttempts}/${maxReconnectAttempts})`);

                    chatBot.sendMessage("Connection lost. Reconnecting...", {
                        right: false,
                        sender: {name: 'System', id: 'system'},
                        color: '#ff6b6b'
                    });

                    setTimeout(connectWebSocket, reconnectDelay);
                } else if (event.code !== 1000) {
                    console.error("[WebSocket] Max reconnection attempts reached");
                    chatBot.sendMessage("Connection lost. Please refresh the page.", {
                        right: false,
                        sender: {name: 'System', id: 'system'},
                        color: '#ff6b6b'
                    });
                }
            };
        };

        // Initial connection
        connectWebSocket();

        chatBot.addEventListener("sent", function (e) {
            if (e.detail.message.right === true) {
                // User message
                console.log("[WebSocket] Sending message:", e.detail.message.message);

                if (socket.readyState === WebSocket.OPEN) {
                    socket.send(e.detail.message.message);
                    chatBot.sendMessage("", {
                        right: false,
                        sender: {name: 'Bob', id: '007'},
                        loading: true
                    });
                } else {
                    console.warn("[WebSocket] Cannot send message - connection not open");
                    chatBot.sendMessage("Connection not available. Please wait...", {
                        right: false,
                        sender: {name: 'System', id: 'system'},
                        color: '#ff6b6b'
                    });
                }
            }
        });
    }
}

customElements.define('demo-chat', DemoChat);