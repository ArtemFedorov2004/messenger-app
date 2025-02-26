import React, {useCallback, useState} from 'react';
import _debounce from 'lodash/debounce';
import './Search.css'
import api from "../../../api/api";


const Search = ({showSearchSection, setShowSearchSection}) => {
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
                    placeholder="Search chats..."
                    value={searchTerm}
                    onFocus={handleInputFocus}
                    onChange={handleInputChange}
                />
            </div>

            {showSearchSection && searchTerm ? (
                <div className="search-list">
                    {foundUsers.length > 0 ? (
                        foundUsers.map(user => (
                            <div key={user.id} className="search-box">
                                <div className="img-box">
                                    <img
                                        src="https://images.pexels.com/photos/2379005/pexels-photo-2379005.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1"
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