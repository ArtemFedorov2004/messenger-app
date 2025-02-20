import React from 'react';
import './ChatList.css'
import {useUser} from "../../../contexts/UserContext";


const ChatList = ({connectedUsers, setMessages, setSelectedUser, selectedUser}) => {
    const {user} = useUser()

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
            <ul>
                {connectedUsers.map((user) => (
                    <li
                        key={user.id}
                        className={`chat-item ${selectedUser === user.id ? 'selected' : ''}`}
                        onClick={() => handleUserClick(user)}
                    >
                        <img className="avatar" src={'logo512.png'} alt={user.name}/>
                        <div className="chat-info">
                            <div className="chat-name">{user.firstname + " " + user.lastname}</div>
                            <div className="last-message">{user.lastMessage}</div>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default ChatList;

/*const [chatList, setChatList] = useState([]);

    useEffect(() => {
        setChatList([
            {
                id: 1,
                name: 'Александр фффффффффффффффффффффффффффффффффффффффффффффффффффф',
                lastMessage: 'Привет, как дела?'
            },
            {
                id: 2,
                name: 'Мария',
                lastMessage: 'Нужна помощь с проектом аааааааааааааааааааааааааааааааааааааааааааааааааааааааааааа'
            },
            {
                id: 3,
                name: 'Дмитрий',
                lastMessage: 'Ты свободен сегодня?'
            }
        ]);
    }, []);*/