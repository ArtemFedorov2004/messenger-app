import Messages from "../ChatMessages/Messages";
import React, {useCallback} from "react";
import './FriendChat.css'
import {useUser} from "../../../contexts/UserContext";
import api from "../../../api/api";


const FriendChat = ({
                        friendChat,
                        appendMessageToChat,
                        clearNewMessage,
                        handleChange
                    }) => {
    const {user} = useUser();

    const handleSendMessage = useCallback(
        (e) => {
            e.preventDefault();
            const content = friendChat.newMessage.trim();
            if (content) {
                const message = {
                    senderId: user.id,
                    content: content,
                    createdAt: new Date()
                }
                api.post(`/friend-chats/${friendChat.friendId}/messages`, message)
                    .then(response => {
                        const sentMessage = response.data;
                        appendMessageToChat(friendChat.friendId, sentMessage);
                        clearNewMessage(friendChat.friendId);
                    })
                    .catch(error => console.error(error));
            }
        }, [user, friendChat.newMessage]);

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
                            alt="avatar"/>
                    </div>
                    <h4>{friendChat.friendName}<br/><span>Online</span></h4>
                </div>
            </div>

            <div className="chat-container">
                <Messages messages={friendChat.messages}/>
            </div>

            <div className="chatbox-input">
                <input
                    type="text"
                    placeholder="Введите сообщение"
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

export default FriendChat;