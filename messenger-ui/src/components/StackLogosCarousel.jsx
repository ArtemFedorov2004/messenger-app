import React from 'react';
import javaLogo from "../assets/stack-logos/java-logo.png";
import springBootLogo from "../assets/stack-logos/spring-boot-logo.png";
import mongodbLogo from "../assets/stack-logos/mongodb-logo.png";
import javaScriptLogo from "../assets/stack-logos/javascript-logo.png";
import reactLogo from "../assets/stack-logos/react-logo.png";
import htmlLogo from "../assets/stack-logos/html-logo.png";
import cssLogo from "../assets/stack-logos/css-logo.png";
import dockerLogo from "../assets/stack-logos/docker-logo.png";
import nginxLogo from "../assets/stack-logos/nginx-logo.png";
import tomcatLogo from "../assets/stack-logos/tomcat-logo.png";
import {Carousel, Col, Image} from "antd";

const StackLogosCarousel = () => {
    const logos = [
        javaLogo, springBootLogo, mongodbLogo, javaScriptLogo, reactLogo,
        htmlLogo, cssLogo, dockerLogo, nginxLogo, tomcatLogo
    ]

    return (
        <Col span={8}>
            <Carousel autoplay>
                {logos.map((logo, index) => {
                    return <Image key={index} width={400} src={logo}/>
                })}
            </Carousel>
        </Col>
    );
};

export default StackLogosCarousel;