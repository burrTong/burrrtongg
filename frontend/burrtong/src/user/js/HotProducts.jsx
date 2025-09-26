import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getAllProducts } from "../api/productApi.js";
import "../css/App.css"; // ใช้ไฟล์เดิม เพิ่มคลาสด้านล่างในข้อ 3

const HotProducts = ({ limit = 3 }) => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetch = async () => {
      try {
        const list = await getAllProducts();
        // เลือก top-N เป็น hot ชั่วคราว (ปรับ logic ได้ภายหลัง เช่นตามยอดขาย/สถานะ)
        setProducts(list.slice(0, limit));
      } catch (e) {
        console.error("Failed to fetch hot products:", e);
      } finally {
        setLoading(false);
      }
    };
    fetch();
  }, [limit]);

  if (loading) return null; // หรือจะใส่ skeleton ก็ได้

  return (
    <section className="hot-products">
      <h2>Hot Products</h2>
      <div className="hot-products-grid">
        {products.map((p) => (
          <Link
            key={p.id}
            to={`/home/products/${p.id}`} // ไปหน้า detail แบบ absolute path
            className="hot-product-card"
          >
            <div className="hot-product-image">
              <img src={p.mainImage} alt={p.name} />
            </div>
            <div className="hot-product-info">
              <h3>{p.name}</h3>
              <div className="hot-product-footer">
                <p className="hot-product-price">{p.getFormattedPrice()}</p>
                <p className="hot-product-status">{p.status}</p>
              </div>
            </div>
          </Link>
        ))}
      </div>
    </section>
  );
};

export default HotProducts;
