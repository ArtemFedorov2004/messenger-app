import React from 'react';
import {Card, Row} from "antd";
import LoginForm from "../components/auth/LoginForm";

const Login = () => {
    return (
        <Row justify="center" align="middle" className="h100">
            <Card>
                <LoginForm/>
            </Card>
        </Row>
    );
};

export default Login;