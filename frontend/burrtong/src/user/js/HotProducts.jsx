import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAllProducts } from "../api/productApi.js";
import "../css/App.css";

const HotProducts = ({ limit = 3 }) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetch = async () => {
      try {
        const list = await getAllProducts();
        setProducts(list.slice(0, limit));
      } catch (e) {
        console.error("Failed to fetch hot products:", e);
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [limit]);

  if (loading) return null;

  return (
    <section className="hot-products" data-test="hot-products-section">
      <h2>Hot Products</h2>
      <div className="hot-products-grid">
        {products.map((p) => (
          <Link
            key={p.id}
            to={`/home/products/${p.id}`}
            className="hot-product-card"
            data-test={`hot-product-card-${p.id}`}
          >
            <div className="hot-product-image">
              <img src={p.mainImage} alt={p.name} data-test={`hot-product-img-${p.id}`} />
            </div>
            <div className="hot-product-info">
              <h3 data-test={`hot-product-name-${p.id}`}>{p.name}</h3>
              <div className="hot-product-footer">
                <p className="hot-product-price" data-test={`hot-product-price-${p.id}`}>{p.getFormattedPrice()}</p>
                <p className="hot-product-status" data-test={`hot-product-status-${p.id}`}>{p.status}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
};

export default HotProducts;
