import {makeAutoObservable} from "mobx";

export default class ChatStore {
    constructor() {
        this._chats = []
        this._isChatSelected = false;
        this._selectedChat = {};
        this._messages = []
        this._messagesLoading = false;

        makeAutoObservable(this)
    }

    setChats(chats) {
        this._chats = chats
    }

    get chats() {
        return this._chats;
    }

    get isChatSelected() {
        return this._isChatSelected;
    }

    get selectedChat() {
        return this._selectedChat;
    }

    setMessages(messages) {
        this._messages = messages;
    }

    get messages() {
        return this._messages;
    }

    setMessagesLoading(bool) {
        this._messagesLoading = bool;
    }

    get messagesLoading() {
        return this._messagesLoading;
    }

    selectChat(chat) {
        this._isChatSelected = true;
        this._selectedChat = chat;
    }

    updateNewMessage(participantName, newMessage) {
        const chat = this._chats.find(c => c.participantName === participantName);

        if (chat) {
            chat.newMessage = newMessage;
        }

        if (this._selectedChat?.participantName === participantName) {
            this._selectedChat.newMessage = newMessage;
        }
    }

    updateLastMessage(chatId, message) {
        const chat = this._chats.find(c => c.id === chatId);
        if (chat) {
            chat.lastMessage = message;
        }

        if (this._selectedChat?.id === chatId) {
            this._selectedChat.lastMessage = message;
        }
    }

    addMessage(message) {
        if (this._selectedChat?.id === message.chatId && !this._messagesLoading) {
            this._messages.unshift(message);
        }

        this.updateLastMessage(message.chatId, message);
    }

    findChatIdWhereMessageIsLast(messageId) {
        const chat = this._chats.find(c => c.lastMessage?.id === messageId);

        return chat?.id ?? -1;
    }

    editMessage(message) {
        const messageToUpdate = this._messages.find(m => m.id === message.id);
        if (messageToUpdate) {
            Object.assign(messageToUpdate, message);
        }

        const chatId = this.findChatIdWhereMessageIsLast(message.id);
        if (chatId !== -1) {
            this.updateLastMessage(chatId, message);
        }
    }

    deleteMessage(message) {
        this._messages = this._messages.filter(m => m.id !== message.id);
    }

    deleteMessageAndUpdateLastMessage(messageId) {
        this.deleteMessage({id: messageId})

        const chatId = this.findChatIdWhereMessageIsLast(messageId)
        if (chatId === -1) {
            return
        }
        this.updateLastMessage(chatId, this._messages[0])
    }

    findChatWithUser(username) {
        return this._chats.find(chat =>
            chat.participantName === username)
    }

    deleteNewChatWithParticipant(participantName) {
        this._chats = this._chats.filter(chat => {
            return chat.id !== null || chat.participantName !== participantName;
        });
    }

    addCreatedChat(newChat) {
        const preparedChat = {...newChat, newMessage: {content: ''}};

        this.setChats([...this._chats, preparedChat]);

        this.deleteNewChatWithParticipant(newChat.participantName);

        if (this._selectedChat.participantName === newChat.participantName) {
            this.selectChat(preparedChat)
        }
    }

    editChat(updatedChat) {
        const chatToUpdate = this._chats.find(c => c.id === updatedChat.id);
        if (chatToUpdate) {
            Object.assign(chatToUpdate, updatedChat);
        }

        if (this._selectedChat?.id === updatedChat.id) {
            Object.assign(this._selectedChat, updatedChat);
        }
    }

    resetStore() {
        this._chats = [];
        this._isChatSelected = false;
        this._selectedChat = {};
        this._messages = [];
        this._messagesLoading = false;
    }
}