import React from 'react';
import './ChatList.css'


const ChatList = ({friendChats, selectPrivateChatById}) => {

    return (
        <div className="chat-list">
            {Array.from(friendChats.values())
                .filter(chat => chat.isNew !== true)
                .map((chat) => (
                    <div
                        key={chat.friendId}
                        onClick={() => selectPrivateChatById(chat.friendId)}
                        className="chat-box"
                    >
                        <div className="img-box">
                            <img
                                src="/logo512.png"
                                alt={chat.friendName}/>
                        </div>
                        <div className="chat-details">
                            <div className="text-head">
                                <h4>{chat.friendName}</h4>
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