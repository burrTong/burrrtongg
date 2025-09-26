import React from "react";
import Navbar from "./Navbar.jsx";
import Slider from "react-slick";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";

import cartImg from "../../assets/cart.png";
import productImg from "../../assets/product.png";
import userImg from "../../assets/user.png";
import AUImg from "../../assets/AU.jpg";

import "../css/App.css";
import HotProducts from "./HotProducts.jsx";

function HomePage() {
  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 3000,
  };

  return (
    <>
      <section className="hero" data-test="home-hero">
        <Slider {...settings}>
          <div><img src={cartImg} alt="Cart" className="colossal-image" /></div>
          <div><img src={productImg} alt="Product" className="colossal-image" /></div>
          <div><img src={userImg} alt="User" className="colossal-image" /></div>
          <div><img src={AUImg} alt="AU" className="colossal-image" /></div>
        </Slider>
      </section>

      <HotProducts limit={3} data-test="home-hot-products" />
    </>
  );
}

export default HomePage;
