import React from 'react';
import Navbar from "../components/Navbar";
import ProjectDescription from "../components/ProjectDescription";
import StackLogosCarousel from "../components/StackLogosCarousel";
import {Row, Layout} from "antd";

const Home = () => {
    return (
        <>
            <Navbar/>
            <Layout.Content>
                <Row justify="space-around" align="middle" style={{marginTop: "100px"}}>
                    <ProjectDescription/>
                    <StackLogosCarousel/>
                </Row>
            </Layout.Content>
        </>
    );
};

export default Home;