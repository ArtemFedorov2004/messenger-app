import SockJS from "sockjs-client";
import Stomp from "stompjs";

export default class StompClient {
    constructor(accessToken, username, chatStore) {
        this.accessToken = accessToken;
        this.username = username;
        this.chatStore = chatStore;
        this.client = null;
    }

    connect() {
        const socket = new SockJS(`${process.env.REACT_APP_API_URL}/ws`);
        this.client = Stomp.over(socket);
        this.client.debug = null

        const headers = {
            Authorization: `Bearer ${this.accessToken}`,
        };

        this.client.connect(headers, () => {
            this.client.subscribe(`/user/${this.username}/queue/notifications`, (payload) => {
                const notification = JSON.parse(payload.body);
                this.handleNotification(notification);
            });
        });
    }

    handleNotification(notification) {
        switch (notification.type) {
            case 'NEW_MESSAGE':
                this.chatStore.addMessage(notification.message);
                break;
            case 'EDIT_MESSAGE':
                this.chatStore.editMessage(notification.message);
                break;
            case 'DELETE_MESSAGE':
                this.chatStore.deleteMessage(notification.message);
                break;
            case 'NEW_CHAT':
                this.chatStore.addChat(notification.chat);
                break;
            default:
                console.warn('Unknown event type:', notification.type);
        }
    }

    disconnect() {
        if (this.client && this.client.connected) {
            this.client.disconnect();
        }
    }
}
