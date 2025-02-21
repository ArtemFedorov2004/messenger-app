import React from 'react';
import './ChatList.css'
import {useUser} from "../../../contexts/UserContext";


const ChatList = ({connectedUsers, setMessages, selectedUser, setSelectedUser}) => {
    const {user} = useUser();

    const handleUserClick = (user) => {
        setSelectedUser(user);
        fetchUserChat(user);
    };

    const fetchUserChat = async (selectedUser) => {
        const response = await fetch(`http://localhost:8080/messages/${user.id}/${selectedUser.id}`);
        const chatMessages = await response.json();
        setMessages(chatMessages);
    };

    return (
        <div className="chat-list">
            {chats.map((chat) => (
                <div
                    key={chat.id}
                    onClick={() => handleUserClick(user)}
                    className="chat-box"
                >
                    <div className="img-box">
                        <img
                            src="https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
                            alt={chat.title}/>
                    </div>
                    <div className="chat-details">
                        <div className="text-head">
                            <h4>{chat.title}</h4>
                            <p className="time unread">{chat.time}</p>
                        </div>
                        <div className="text-message">
                            <p>{chat.lastMessage}</p>
                            {chat.unreadCount > 0 && <b>{chat.unreadCount}</b>}
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ChatList;