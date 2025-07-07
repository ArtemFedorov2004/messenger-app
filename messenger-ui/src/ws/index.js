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
            this.client.subscribe(`/user/${this.username}/queue/private-chat-notifications`, (payload) => {
                const notification = JSON.parse(payload.body);
                this.handlePrivateChatNotification(notification);
            });
            this.client.subscribe(`/user/${this.username}/queue/message-notifications`, (payload) => {
                const notification = JSON.parse(payload.body);
                this.handleMessageNotification(notification);
            });
        });
    }

    handlePrivateChatNotification(notification) {
        switch (notification.type) {
            case 'NEW_CHAT':
                this.chatStore.addCreatedChat(notification.chat);
                break;
            case 'EDIT_CHAT':
                this.chatStore.editChat(notification.chat);
                break;
            default:
                console.warn('Unknown notification type:', notification.type);
        }
    }

    handleMessageNotification(notification) {
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
            default:
                console.warn('Unknown notification type:', notification.type);
        }
    }

    disconnect() {
        if (this.client && this.client.connected) {
            this.client.disconnect();
        }
    }
}
