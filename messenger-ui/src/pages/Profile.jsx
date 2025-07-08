import React, {useContext} from 'react';
import {Avatar, Card, Col, Row, Typography} from 'antd';
import {MailOutlined, UserOutlined} from '@ant-design/icons';
import {Context} from "../index";
import {observer} from "mobx-react-lite";
import userLogo from "../assets/user.png";
import ProfileField from "../components/profile/ProfileField";

const {Title} = Typography;

const Profile = observer(() => {
    const {user} = useContext(Context);

    return (
        <div style={{padding: '24px'}}>
            <Row justify="center">
                <Col xs={24} sm={20} md={16} lg={12}>
                    <Card
                        title={
                            <Title level={3} style={{marginBottom: 0}}>
                                Профиль пользователя
                            </Title>
                        }
                    >
                        <div style={{textAlign: 'center', marginBottom: '24px'}}>
                            <Avatar size={128} src={userLogo}/>
                        </div>

                        <ProfileField
                            icon={<UserOutlined/>}
                            label="Имя пользователя"
                            value={user.user.username}
                        />

                        <ProfileField
                            icon={<MailOutlined/>}
                            label="Email"
                            value={user.user.email}
                        />
                    </Card>
                </Col>
            </Row>
        </div>
    );
});

export default Profile;