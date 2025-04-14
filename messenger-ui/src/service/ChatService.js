export default class ChatService {
    static chats = [
        {
            id: '1',
            type: 'USER',
            title: 'Alex Smirnov',
            messages: [
                {
                    id: 1,
                    chatId: '1',
                    senderId: '1',
                    content: 'Hi Alex! How are you?',
                    createdAt: Date.now() - 5000,
                    editedAt: null
                },
                {
                    id: 2,
                    chatId: '1',
                    senderId: '1',
                    content: 'Are you available to meet this weekend?',
                    createdAt: Date.now() - 10000,
                    editedAt: null
                },
                {
                    id: 3,
                    chatId: '1',
                    senderId: '2',
                    content: 'Hello everyone, are we meeting this Friday?',
                    createdAt: Date.now() - 20000,
                    editedAt: null
                },
                {
                    id: 4,
                    chatId: '1',
                    senderId: '1',
                    content: 'Yes, I think Friday works for me!',
                    createdAt: Date.now() - 25000,
                    editedAt: null
                },
                {
                    id: 5,
                    chatId: '1',
                    senderId: '2',
                    content: 'Hey Misha, long time no talk!',
                    createdAt: Date.now() - 30000,
                    editedAt: null
                },
                {
                    id: 6,
                    chatId: '1',
                    senderId: '2',
                    content: 'Can we reschedule the meeting for tomorrow?',
                    createdAt: Date.now() - 35000,
                    editedAt: null
                },
                {
                    id: 7,
                    chatId: '1',
                    senderId: '1',
                    content: 'Let’s do it! Coffee this weekend?',
                    createdAt: Date.now() - 40000,
                    editedAt: null
                },
                {
                    id: 8,
                    chatId: '1',
                    senderId: '2',
                    content: 'Are we visiting grandma this Saturday?',
                    createdAt: Date.now() - 45000,
                    editedAt: null
                },
                {
                    id: 9,
                    chatId: '1',
                    senderId: '1',
                    content: 'What’s going on? Meeting today?',
                    createdAt: Date.now() - 50000,
                    editedAt: null
                },
                {
                    id: 10,
                    chatId: '1',
                    senderId: '2',
                    content: 'Did everyone finish the book?',
                    createdAt: Date.now() - 55000,
                    editedAt: null
                },
                {
                    id: 11,
                    chatId: '1',
                    senderId: '1',
                    content: 'Thanks for the birthday wishes! 🥳',
                    createdAt: Date.now() - 60000,
                    editedAt: null
                },
                {
                    id: 12,
                    chatId: '1',
                    senderId: '2',
                    content: 'Are we hitting the gym tomorrow morning?',
                    createdAt: Date.now() - 65000,
                    editedAt: null
                }
            ]
        },
        {
            id: '2',
            type: 'USER',
            title: 'Pavel Fedorov',
            messages: [
                {
                    id: 1,
                    chatId: '2',
                    senderId: '1',
                    content: 'Hi Alex! How are you?',
                    createdAt: Date.now() - 5000,
                    editedAt: null
                },
                {
                    id: 2,
                    chatId: '2',
                    senderId: '1',
                    content: 'Are you available to meet this weekend?',
                    createdAt: Date.now() - 10000,
                    editedAt: null
                }
            ]
        }
    ];

    static async getChats() {
        return new Promise((resolve) => {
            setTimeout(async () => {
                resolve(ChatService.chats);
            }, 1000);
        });
    }

    static async createMessage(chatId, content) {
        return new Promise((resolve, reject) => {
            setTimeout(async () => {
                const chat = ChatService.chats.find(c => c.id === chatId);

                if (!chat) {
                    reject(new Error("Chat not found"));
                    return;
                }

                const lastId = chat.messages.length > 0
                    ? Math.max(...chat.messages.map(msg => msg.id))
                    : 0;

                const id = lastId + 1;

                const message = {
                    id: id,
                    chatId: chatId,
                    senderId: '1',
                    content: content,
                    createdAt: Date.now(),
                    editedAt: null
                };

                chat.messages.push(message);

                resolve(message);
            }, 1000);
        });
    }

    static async editMessage(chatId, id, newContent) {
        return new Promise((resolve, reject) => {
            setTimeout(async () => {
                const chat = ChatService.chats.find(c => c.id === chatId);

                if (!chat) {
                    reject(new Error("Chat not found"));
                    return;
                }

                const messageIndex = chat.messages.findIndex(msg => msg.id === id);

                if (messageIndex === -1) {
                    reject(new Error("Message not found"));
                    return;
                }

                const message = chat.messages[messageIndex];
                message.content = newContent;
                message.editedAt = Date.now();

                resolve(message);
            }, 1000);
        });
    }

    static async deleteMessage(chatId, id) {
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                const chat = ChatService.chats.find(c => c.id === chatId);

                if (!chat) {
                    reject(new Error("Chat not found"));
                    return;
                }

                const messageIndex = chat.messages.findIndex(msg => msg.id === id);

                if (messageIndex === -1) {
                    reject(new Error("Message not found"));
                    return;
                }

                const [deletedMessage] = chat.messages.splice(messageIndex, 1);

                resolve(deletedMessage);
            }, 1000);
        });
    }
}