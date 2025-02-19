import React, {useState, useEffect, useRef} from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const host = "http://localhost:8080";

function Chat({nickname, fullname, onLogout}) {
    const [stompClient, setStompClient] = useState(null);
    const [connectedUsers, setConnectedUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    const [messages, setMessages] = useState([]);
    const [messageInput, setMessageInput] = useState('');

    const selectedUserRef = useRef(null);

    useEffect(() => {
        selectedUserRef.current = selectedUser;
    }, [selectedUser]);

    let socket = null;
    let client = null;

    useEffect(() => {
        socket = new SockJS(`${host}/ws`);
        client = Stomp.over(socket);

        client.connect({}, () => {
            setStompClient(client);
            client.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
            client.subscribe(`/user/public`, onUserConnect);

            // register the user
            client.send(`/app/user.addUser`, {}, JSON.stringify({
                nickName: nickname,
                fullName: fullname,
                status: 'ONLINE'
            }));
            fetchConnectedUsers();
        });

        return () => {
            if (stompClient) stompClient.disconnect();
        };
    }, [nickname, fullname]);

    const fetchConnectedUsers = async () => {
        const response = await fetch(`${host}/users`);
        const users = await response.json();
        users.forEach(user => console.log(user));
        setConnectedUsers(users.filter(user => user.nickName !== nickname));
    };

    const onUserConnect = (payload) => {
        const user = JSON.parse(payload.body);
        setConnectedUsers((prevUsers) => [...prevUsers, user]);
    }

    const onMessageReceived = (payload) => {
        const message = JSON.parse(payload.body);
        const currentSelectedUser = selectedUserRef.current;
        console.log(currentSelectedUser);
        console.log(messages);
        if (currentSelectedUser && currentSelectedUser.nickName === message.senderId) {
            setMessages((prevMessages) => [...prevMessages, message]);
        }
    };

    // ----
    const handleUserClick = (user) => {
        setSelectedUser(user);
        fetchUserChat(user);
    };

    // ----
    const fetchUserChat = async (user) => {
        const response = await fetch(`${host}/messages/${nickname}/${user.nickName}`);
        const chatMessages = await response.json();
        setMessages(chatMessages);
    };

    // ----
    const sendMessage = (e) => {
        e.preventDefault();
        if (stompClient && messageInput.trim()) {
            const message = {
                senderId: nickname,
                recipientId: selectedUser.nickName,
                content: messageInput.trim(),
                timestamp: new Date(),
            };
            stompClient.send(`/app/chat`, {}, JSON.stringify(message));
            setMessages((prevMessages) => [...prevMessages, {senderId: nickname, content: messageInput.trim()}]);
            setMessageInput('');
        }
    };

    return (
        <div>
            <div>
                <h2>Online Users</h2>
                <ul>
                    {connectedUsers.map((user) => (
                        <li key={user.nickName} onClick={() => handleUserClick(user)}>
                            {user.fullName}
                        </li>
                    ))}
                </ul>
            </div>

            {selectedUser && (
                <div>
                    <h3>Chat with {selectedUser.fullName}</h3>
                    <div>
                        {messages.map((msg, idx) => (
                            <div key={idx}
                                 className={msg.senderId === nickname ? 'message-sender' : 'message-receiver'}>
                                <p>{msg.content}</p>
                            </div>
                        ))}
                    </div>

                    <form onSubmit={sendMessage}>
                        <input
                            type="text"
                            value={messageInput}
                            onChange={(e) => setMessageInput(e.target.value)}
                            placeholder="Type a message"
                        />
                        <button type="submit">Send</button>
                    </form>
                </div>
            )}

            <button onClick={onLogout}>Logout</button>
        </div>
    );
}

export default Chat;
