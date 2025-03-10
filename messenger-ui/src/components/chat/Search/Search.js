import React, {useCallback, useState} from 'react';
import _debounce from 'lodash/debounce';
import './Search.css'
import api from "../../../api/api";


const Search = ({
                    friendChats,
                    updateFriendChatInMap,
                    selectFriendChatById,
                    showSearchSection,
                    setShowSearchSection
                }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [foundUsers, setFoundUsers] = useState([]);

    const handleFetchUsersByQuery = (query) => {
        if (query) {
            api.get('/users', {
                params: {query: query}
            })
                .then((response) => response.data)
                .then(data => {

                    setFoundUsers(data);
                })
                .catch((err) => console.log(err));
        } else {
            setFoundUsers([]);
        }
    }

    const fetchUsersByQuery = useCallback(_debounce(handleFetchUsersByQuery, 1000), []);

    const handleInputChange = (e) => {
        const query = e.target.value;
        setSearchTerm(query);
        fetchUsersByQuery(query);
    };

    const handleInputFocus = () => {
        setShowSearchSection(true)
    };

    const clearSearch = () => {
        setSearchTerm('');
        setFoundUsers([]);
    };

    const handleBackToList = () => {
        clearSearch();
        setShowSearchSection(false);
    };

    const hasChatWithFriend = (friendId) => {
        return friendChats.has(friendId);
    };

    const selectUser = (user) => {
        if (!hasChatWithFriend(user.id)) {
            const newChat = {
                isNew: true,
                friendId: user.id,
                friendName: user.username,
                messages: [],
                newMessage: ''
            }
            updateFriendChatInMap(newChat);
        }
        selectFriendChatById(user.id);
        setSearchTerm('');
        setShowSearchSection(false);
    }

    return (
        <div>
            <div className="search-bar-container">
                {showSearchSection ? (
                    <button
                        className="back-button"
                        onClick={handleBackToList}
                    >
                        ←
                    </button>
                ) : null}
                <input
                    type="text"
                    className="search-input"
                    placeholder="Найти пользователей..."
                    value={searchTerm}
                    onFocus={handleInputFocus}
                    onChange={handleInputChange}
                />
            </div>

            {showSearchSection && searchTerm ? (
                <div className="search-list">
                    {foundUsers.length > 0 ? (
                        foundUsers.map(user => (
                            <div
                                key={user.id}
                                onClick={() => selectUser(user)}
                                className="search-box"
                            >
                                <div className="img-box">
                                    <img
                                        src="/logo512.png"
                                        alt={user.username}/>
                                </div>
                                <h4 className="username">{user.username}</h4>
                            </div>
                        ))
                    ) : (
                        <p>No users found.</p>
                    )}
                </div>
            ) : null}
        </div>
    );
};

export default Search;