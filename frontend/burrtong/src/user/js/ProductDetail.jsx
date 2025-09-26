import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProductById } from '../api/productApi.js';
import '../css/ProductDetail.css';

const ProductDetail = ({ cart, setCart }) => {
  const { productId } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [quantity, setQuantity] = useState(1);
  const [selectedSize, setSelectedSize] = useState(null);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const fetchedProduct = await getProductById(productId);
        setProduct(fetchedProduct);
      } catch (error) {
        console.error(`Failed to fetch product with id ${productId}:`, error);
      } finally {
        setLoading(false);
      }
    };
    fetchProduct();
  }, [productId]);

  const handleBack = () => navigate(-1);

  const handleIncrement = () => {
    if (product && quantity < product.stock) {
      setQuantity(prev => prev + 1);
    }
  };

  const handleDecrement = () => {
    setQuantity(prev => (prev > 1 ? prev - 1 : 1));
  };

  const handleSizeSelect = (size) => setSelectedSize(size);

  const handleAddToCart = () => {
    if (!product || product.stock === 0) return;
    if (quantity > product.stock) {
      alert('Not enough stock!');
      return;
    }
    const existingProduct = cart.find(item => item.id === product.id);
    if (existingProduct) {
      setCart(
        cart.map(item =>
          item.id === product.id
            ? { ...item, quantity: item.quantity + quantity }
            : item
        )
      );
    } else {
      const cartItem = { ...product, quantity, size: selectedSize };
      setCart([...cart, cartItem]);
    }
    navigate('/home/cart');
  };

  const SIZES = ["39 EU", "39.5 EU", "40 EU", "41.5 EU", "42 EU", "42.5 EU", "43 EU"];

  if (loading) return <div>Loading...</div>;
  if (!product) return <div>Product not found!</div>;

  return (
    <div className="product-detail-page" data-test="product-detail-page">
      <div className="breadcrumb"></div>
      <div className="back-arrow-container">
        <a
          href="#"
          onClick={(e) => { e.preventDefault(); handleBack(); }}
          className="back-arrow"
        >
          &larr;
        </a>
      </div>
      <div className="product-detail-container">
        <div className="product-detail-images">
          <div className="thumbnails">
            {product.thumbnails.map((thumb, index) => (
              <div key={index} className="thumbnail-image">
                <img src={thumb} alt={`thumbnail ${index + 1}`} data-test={`thumb-img-${index}`} />
              </div>
            ))}
          </div>
          <div className="main-image">
            <img src={product.mainImage} alt="main product" data-test="product-detail-image" />
          </div>
        </div>

        <div className="product-info">
          <p className="brand" data-test="product-brand">{product.brand}</p>
          <h1 data-test="product-detail-name">{product.name}</h1>
          <p className="product-id" data-test="product-id">Product id : {product.productId}</p>
          <p className="price" data-test="product-detail-price">{product.getFormattedPrice()}</p>
          <p className={`stock-status ${product.stock === 0 ? 'out-of-stock' : ''}`}
             data-test="product-detail-stock">
             {product.stock > 0 ? `Stock: ${product.stock}` : 'Out of Stock'}
          </p>

          <div className="size-selection">
            <p className="size-label">Size :</p>
            <div className="sizes" data-test="product-detail-size">
              <select data-test="size-select" onChange={(e) => handleSizeSelect(e.target.value)}>
                {SIZES.map(size => (
                  <option key={size} value={size}>{size}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="quantity-selector" data-test="product-quantity">
            <p className="quantity-label">จำนวน</p>
            <div className="quantity-controls">
              <button data-test="qty-dec" onClick={handleDecrement} disabled={product.stock === 0}>-</button>
              <input type="text" value={quantity} onChange={(e) => setQuantity(parseInt(e.target.value, 10) || 1)} data-test="quantity-input" />
              <button data-test="qty-inc" onClick={handleIncrement} disabled={product.stock === 0}>+</button>
            </div>
          </div>

          <button
            className="add-to-cart"
            data-test="add-to-cart-button"
            onClick={handleAddToCart}
            disabled={product.stock === 0}
          >
            {product.stock > 0 ? 'เพิ่มใส่ตะกร้า' : 'สินค้าหมด'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
