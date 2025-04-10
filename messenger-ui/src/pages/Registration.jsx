import React from 'react';
import {Card, Row} from "antd";
import RegistrationForm from "../components/auth/RegistrationForm";

const Registration = () => {
    return (
        <Row justify="center" align="middle" className="h100">
            <Card>
                <RegistrationForm/>
            </Card>
        </Row>
    );
};

export default Registration;