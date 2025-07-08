import {observer} from "mobx-react-lite";
import React, {useContext, useEffect, useState} from "react";
import {Context} from "../../index";
import InfiniteScroll from "react-infinite-scroll-component";
import {Skeleton} from "antd";
import './MessageList.css';
import MessageService from "../../service/MessageService";
import Message from "./Message";
import {MESSAGES_BATCH_SIZE, MIN_MESSAGES_THRESHOLD} from "../../utils/consts";

const MessageList = observer(() => {
    const {chat} = useContext(Context);

    const [hasMore, setHasMore] = useState(true);
    const [page, setPage] = useState(0);
    const [isComponentLoaded, setIsComponentLoaded] = useState(false);

    useEffect(() => {
        chat.setMessages([])
        loadMoreMessages()
            .then(() => setIsComponentLoaded(true));
    }, []);

    useEffect(() => {
        if (isComponentLoaded && chat.messages.length < MIN_MESSAGES_THRESHOLD && hasMore) {
            loadMoreMessages()
        }
    }, [chat.messages]);

    const loadMoreMessages = async () => {
        if (chat.messagesLoading || !hasMore) return;

        chat.setMessagesLoading(true);
        try {
            const response = await MessageService.fetchMessages(
                chat.selectedChat.id,
                page,
                MESSAGES_BATCH_SIZE)
            const newMessages = await response.data.content;

            if (newMessages.length === 0) {
                setHasMore(false);
                return;
            }

            chat.setMessages([...chat.messages, ...newMessages])
            setPage(prev => prev + 1);
        } catch (error) {
            console.error(error);
        } finally {
            chat.setMessagesLoading(false);
        }
    }

    return (
        <div
            id="scrollableDiv"
            className="message-list"
        >
            <InfiniteScroll
                className="infinite-scroll"
                dataLength={chat.messages.length}
                next={loadMoreMessages}
                inverse={true}
                hasMore={hasMore}
                loader={<Skeleton avatar paragraph={{rows: 1}} active/>}
                scrollableTarget="scrollableDiv"
            >
                {chat.messages.map((message) => (
                    <Message key={message.id} message={message}/>
                ))}
            </InfiniteScroll>
        </div>
    );
});


export default MessageList;