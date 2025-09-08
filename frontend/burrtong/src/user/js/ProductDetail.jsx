import React from 'react';
import { useParams } from 'react-router-dom';
import { productDetails } from './data.js'; // Import data
import '../css/ProductDetail.css'; // Corrected path

const ProductDetail = () => {
  const { productId } = useParams();
  const product = productDetails[productId] || Object.values(productDetails)[0]; // Fallback to the first product if not found

  // Render nothing or a loading state if no product data is available
  if (!product) {
    return <div>Product not found!</div>;
  }

  return (
    <>
      {/* The duplicate Navbar is removed */}
      <div className="product-detail-page">
        <div className="breadcrumb">
            <a href="#" className="back-arrow">&larr;</a>
        </div>
        <div className="product-detail-container">
          <div className="product-detail-images">
            <div className="thumbnails">
              {product.thumbnails.map((thumb, index) => (
                <div key={index} className="thumbnail-image">
                    <img  src={thumb} alt={`thumbnail ${index + 1}`} />
                </div>
              ))}
            </div>
            <div className="main-image">
              <img src={product.mainImage} alt="main product" />
            </div>
          </div>
          <div className="product-info">
            <p className="brand">{product.brand}</p>
            <h1>{product.name}</h1>
            <p className="product-id">Product id : {product.productId}</p>
            <p className="price">{product.price}</p>
            <div className="size-selection">
              <p className="size-label">Size :</p>
              <div className="sizes">
                <button className="size-button">39 EU</button>
                <button className="size-button">39.5 EU</button>
                <button className="size-button">40 EU</button>
                <button className="size-button">41.5 EU</button>
                <button className="size-button">42 EU</button>
                <button className="size-button">42.5 EU</button>
                <button className="size-button">43 EU</button>
              </div>
            </div>
            <div className="quantity-selector">
              <p className='quantity-label'>จํานวน</p>
              <div className="quantity-controls">
                <button>-</button>
                <span>1</span>
                <button>+</button>
              </div>
            </div>
            <button className="add-to-cart">เพิ่มใส่ตะกร้า</button>
          </div>
        </div>
      </div>
    </>
  );
};

export default ProductDetail;