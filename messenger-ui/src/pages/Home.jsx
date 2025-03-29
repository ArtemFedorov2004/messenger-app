import React from 'react';
import Navbar from "../components/Navbar";
import ProjectDescription from "../components/ProjectDescription";
import StackLogosCarousel from "../components/StackLogosCarousel";
import {Layout, Row} from "antd";

const Home = () => {
    return (
        <Layout className="h100">
            <Navbar/>
            <Layout.Content>
                <Row justify="space-around" align="middle" style={{marginTop: "100px"}}>
                    <ProjectDescription/>
                    <StackLogosCarousel/>
                </Row>
            </Layout.Content>
        </Layout>
    );
};

export default Home;