import {useState, useEffect} from 'react';
import api from "../api/api";


const useFriendChats = (user) => {
    const [friendChats, setFriendChats] = useState(new Map());

    const fetchUserChats = async () => {
        try {
            const friendChats = await fetchUserFriendChats();
            setFriendChats((prev) => {
                const newChats = new Map(prev);
                friendChats.forEach((chat) => {
                    newChats.set(chat.friendId, {
                        isNew: false,
                        ...chat,
                        newMessage: ''
                    });
                });
                return newChats;
            });
        } catch (error) {
            console.error("Failed to fetch user chats", error);
        }
    };

    const fetchUserFriendChats = async () => {
        return api.get("/friend-chats")
            .then((response) => response.data)
            .catch((error) => {
                console.error("Failed to fetch friend chats", error);
            });
    }

    const updateFriendChatInMap = (friendChat) => {
        setFriendChats((prev) => {
            const newChats = new Map(prev);

            if (newChats.has(friendChat.friendId)) {
                const existingChat = newChats.get(friendChat.friendId);

                const updatedChat = {...existingChat, ...friendChat};

                newChats.set(friendChat.friendId, updatedChat);
            } else {
                newChats.set(friendChat.friendId, friendChat);
            }

            return newChats;
        });
    };

    const updateNewMessageInChat = (friendId, value) => {
        setFriendChats((prev) => {
            const newChats = new Map(prev);
            const chat = newChats.get(friendId);

            if (chat) {
                newChats.set(friendId, {
                    ...chat,
                    newMessage: value
                });
            }

            return newChats;
        });
    };

    const appendMessageToChat = (friendId, message) => {
        setFriendChats((prev) => {
            const newChats = new Map(prev);
            const chat = newChats.get(friendId);

            if (chat) {
                const newMessages = [...chat.messages, message];
                chat.messages = newMessages;

                newChats.set(friendId, chat);
            }

            return newChats;
        });
    };

    const clearNewMessage = (friendId) => {
        if (friendChats.has(friendId)) {
            setFriendChats((prev) => {
                const newChats = new Map(prev);
                const chat = newChats.get(friendId);
                newChats.set(friendId, {
                    ...chat,
                    newMessage: ''
                });
                return newChats;
            });
        }
    };

    const markChatAsExisting = (friendId) => {
        setFriendChats((prev) => {
            const updatedChats = new Map(prev);
            const chat = updatedChats.get(friendId);

            if (chat) {
                chat.isNew = false;
                updatedChats.set(friendId, chat);
            }

            return updatedChats;
        });
    };

    useEffect(() => {
        if (user) {
            fetchUserChats()
                .catch((error) => console.error(error));
        }
    }, [user]);

    return {
        friendChats,
        updateFriendChatInMap,
        updateNewMessageInChat,
        appendMessageToChat,
        clearNewMessage,
        markChatAsExisting
    };
};

export default useFriendChats;