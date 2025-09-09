class Product {
  constructor(data) {
    this.id = data.id;
    this.brand = data.brand;
    this.name = data.name;
    this.productId = data.productId;
    this.price = data.price;
    this.status = data.status;
    this.mainImage = data.mainImage;
    this.thumbnails = data.thumbnails;
  }

  getFormattedPrice(currency = 'THB') {
    return `${this.price.toLocaleString()} ${currency}`;
  }
}

export default Product;
