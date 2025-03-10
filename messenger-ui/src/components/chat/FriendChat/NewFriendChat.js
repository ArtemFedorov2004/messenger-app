import React, {useCallback} from "react";
import './FriendChat.css'
import {useUser} from "../../../contexts/UserContext";
import api from "../../../api/api";


const NewFriendChat = ({
                           friendChat,
                           markChatAsExisting,
                           appendMessageToChat,
                           handleChange,
                           clearNewMessage
                       }) => {
    const {user} = useUser();

    const createFriendChat = () => {
        const requestBody = {
            friendId: friendChat.friendId,
            message: {
                senderId: user.id,
                content: friendChat.newMessage,
                createdAt: new Date()
            }
        }

        return api.post('/friend-chats', requestBody)
            .then(response => response.data)
            .catch(error => console.error(error));
    }

    const handleSendMessage = useCallback(
        (e) => {
            e.preventDefault();
            const content = friendChat.newMessage.trim();
            if (content) {
                createFriendChat()
                    .then((createdFriendChat) => {
                        markChatAsExisting(friendChat.friendId);
                        const sentMessage = createdFriendChat.messages[0];
                        appendMessageToChat(friendChat.friendId, sentMessage);
                    })
                    .catch(error => console.error(error));

                clearNewMessage(friendChat.friendId);
            }
        }, [friendChat.newMessage]);

    const handleKeyDown = useCallback(
        (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                handleSendMessage(e);
            }
        }, [handleSendMessage]);


    return (
        <>
            <div className="header">
                <div className="img-text">
                    <div className="user-img">
                        <img
                            src="/logo512.png"
                            alt="friend-avatar"/>
                    </div>
                    <h4>{friendChat.friendName}<br/><span>Online</span></h4>
                </div>
            </div>

            <div className="chat-container">
                <p>Скажи привет...</p>
            </div>

            <div className="chatbox-input">
                <input
                    type="text"
                    placeholder="Type a message"
                    value={friendChat.newMessage}
                    onChange={handleChange}
                    onKeyDown={handleKeyDown}
                />
                <button
                    className="send-button"
                    onClick={handleSendMessage}
                >
                    Отправить
                </button>
            </div>
        </>
    );
};

export default NewFriendChat;