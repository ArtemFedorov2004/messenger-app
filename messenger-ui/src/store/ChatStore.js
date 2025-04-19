import {makeAutoObservable} from "mobx";

export default class ChatStore {
    constructor() {
        this._chats = []
        this._newMessages = []
        this._selectedChatId = null;
        makeAutoObservable(this)
    }

    setChats(chats) {
        this._chats = chats

        this.setNewMessages(chats.map(chat =>
            ({
                id: null,
                chatId: chat.id,
                content: ''
            })
        ));
    }

    setNewMessages(newMessages) {
        this._newMessages = newMessages
    }

    setSelectedChatId(selectedChatId) {
        this._selectedChatId = selectedChatId
    }

    get chats() {
        return this._chats;
    }

    get newMessages() {
        return this._newMessages;
    }

    get selectedChatId() {
        return this._selectedChatId;
    }

    addChat(newChat) {
        this.setChats([...this._chats, newChat]);

        this.setNewMessages([
            ...this._newMessages,
            {id: null, chatId: newChat.id, content: ''}
        ]);
    }

    addMessage(message) {
        this.setChats(this._chats.map(chat => {
            if (chat.id === message.chatId) {
                const updatedMessages = [...chat.messages, message];
                return {
                    ...chat,
                    messages: updatedMessages
                };
            }
            return chat;
        }));
    }

    deleteMessage(message) {
        this.setChats(this._chats.map(chat => {
            if (chat.id === message.chatId) {
                const updatedMessages = chat.messages.filter(m => m.id !== message.id);
                return {
                    ...chat,
                    messages: updatedMessages
                };
            }
            return chat;
        }));
    }

    editMessage(editedMessage) {
        this.setChats(this._chats.map(chat => {
            if (chat.id === editedMessage.chatId) {
                const updatedMessages = chat.messages.map(msg => {
                    if (msg.id === editedMessage.id) {
                        return {...editedMessage};
                    }
                    return msg;
                });
                return {
                    ...chat,
                    messages: updatedMessages
                };
            }
            return chat;
        }));
    }

    updateNewMessage(updatedNewMessage) {
        this.setNewMessages(this._newMessages.map(newMessage => {
            if (newMessage.chatId === updatedNewMessage.chatId) {
                return {...updatedNewMessage};
            }
            return newMessage;
        }));
    }
}