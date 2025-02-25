import React from 'react';
import './ChatList.css'


const ChatList = ({chats, setSelectedChatId}) => {
    const handleUserClick = (id) => {
        setSelectedChatId(id)
    };

    return (
        <div className="chat-list">
            {Array.from(chats).map(([id, chat]) => (
                <div
                    key={id}
                    onClick={() => handleUserClick(id)}
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
                            {/*<p className="time unread">{chat.time}</p>*/}
                        </div>
                        <div className="text-message">
                            {/*<p>{chat.lastMessage}</p>
                            {chat.unreadCount > 0 && <b>{chat.unreadCount}</b>}*/}
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ChatList;