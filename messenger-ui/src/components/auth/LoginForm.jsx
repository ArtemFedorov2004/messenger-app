import React, {useContext, useState} from 'react';
import {Button, Form} from "antd";
import {Link} from "react-router-dom";
import {REGISTRATION_ROUTE} from "../../utils/consts";
import UsernameInput from "./UsernameInput";
import PasswordInput from "./PasswordInput";
import {Context} from "../../index";
import {observer} from "mobx-react-lite";
import {login} from "../../api/AuthService";

const LoginForm = observer(() => {
    const {user} = useContext(Context);
    const [isLoading, setIsLoading] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const submit = async () => {
        setIsLoading(true)
        try {
            const data = await login(username, password)

            if (data) {
                user.setUser(data)
                user.setIsAuth(true)
            }
        } finally {
            setIsLoading(false)
        }
    }


    return (
        <Form
            name="login"
            onFinish={submit}
        >
            <h1 style={{textAlign: 'center'}}>Вход</h1>
            <UsernameInput username={username} setUsername={setUsername}/>
            <PasswordInput password={password} setPassword={setPassword}/>
            <Form.Item>
                <Button block type="primary" htmlType="submit" loading={isLoading}>
                    Войти
                </Button>
                или <Link to={REGISTRATION_ROUTE}>Зарегистрируйтесь сейчас!</Link>
            </Form.Item>
        </Form>
    );
});

export default LoginForm;