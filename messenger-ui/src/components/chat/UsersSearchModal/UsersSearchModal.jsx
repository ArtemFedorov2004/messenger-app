import React, {useEffect, useState} from 'react';
import {Modal} from 'antd';
import {useDebouncedCallback} from 'use-debounce';
import UserService from "../../../service/UserService";
import UsersSearchList from "./UsersSearchList";
import UsersSearchInput from "./UsersSearchInput";

const UsersSearchModal = ({open, onCancel}) => {
    const [users, setUsers] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [loading, setLoading] = useState(false);
    const paginationConfig = {
        pageSize: 5,
        current: 1,
        total: 0,
        simple: false,
        onChange: (page) => searchUsers(searchQuery, page),
    }
    const [pagination, setPagination] = useState(false);

    const searchUsers = async (query, page = 1) => {
        setLoading(true);
        try {
            const response = await UserService.searchUsers(query, page - 1, pagination.pageSize);

            if (response.data.empty) {
                setUsers([])
                setPagination(false);
            } else {
                setUsers(response.data.content);

                setPagination({
                    ...paginationConfig,
                    current: page,
                    total: response.data.totalElements,
                });
            }
        } catch (error) {
            console.error(error);
        } finally {
            setLoading(false);
        }
    };

    const debouncedSearchUsers = useDebouncedCallback(
        (query) => {
            searchUsers(query)
        },
        300
    );

    useEffect(() => {
        debouncedSearchUsers(searchQuery)
    }, [searchQuery]);

    return (
        <Modal
            title="Поиск пользователей"
            open={open}
            onCancel={onCancel}
            footer={null}
            width={600}
        >
            <div style={{marginBottom: 16}}>
                <UsersSearchInput
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery}
                />
            </div>

            <UsersSearchList
                loading={loading}
                users={users}
                pagination={pagination}
            />
        </Modal>
    );
};

export default UsersSearchModal;